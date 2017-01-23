package com.teamalasca.tests;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.teamalasca.requestdispatcher.RequestDispatcher;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.tests.ComputerMonitor;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;
import fr.upmc.datacenterclient.tests.TestRequestGenerator;

/**
 * This class is a consistent components assembly allowing testing the request
 * dispatcher component.
 * Strongly inspired by the class {@link TestRequestGenerator}
 */
public class			TestRequestDispatcher
extends		AbstractCVM
{
	// ------------------------------------------------------------------------
	// Constants and instance variables
	// ------------------------------------------------------------------------

	// Predefined URI of the different ports visible at the component assembly
	// level.

	public static final String	ComputerServicesInboundPortURI = "cs-ibp" ;
	public static final String	ComputerServicesOutboundPortURI = "cs-obp" ;
	public static final String	ComputerStaticStateDataInboundPortURI = "css-dip" ;
	public static final String	ComputerStaticStateDataOutboundPortURI = "css-dop" ;
	public static final String	ComputerDynamicStateDataInboundPortURI = "cds-dip" ;
	public static final String	ComputerDynamicStateDataOutboundPortURI = "cds-dop" ;

	// Request dispatcher ports
	public static final String RequestDispatcherRequestSubmissionInboundPortURI = "rd-rsip";
	public static final String RequestDispatcherRequestNotificationInboundPortURI = "rd-rnip";
	public static final String RequestDispatcherRequestNotificationOutboundPortURI = "rd-rnop";

	// Request generator ports
	public static final String	RequestGeneratorManagementInboundPortURI = "rg-mip" ;
	public static final String	RequestGeneratorManagementOutboundPortURI = "rg-mop" ;
	public static final String	RequestGeneratorRequestNotificationInboundPortURI = "rg-rnip" ;
	public static final String RequestGeneratorRequestSubmissionOutboundPortURI = "rg-rsob" ;

	// Virtual machines ports
	public static final String	VirtualMachineRequestSubmissionInboundPortURI1 = "vm-rsip1" ;
	public static final String	VirtualMachineRequestSubmissionInboundPortURI2 = "vm-rsip2" ;
	public static final String	VirtualMachineRequestSubmissionInboundPortURI3 = "vm-rsip3" ;
	public static final String VirtualMachineRequestNotificationOutboundPortURI1 = "vm-rnop1" ;
	public static final String VirtualMachineRequestNotificationOutboundPortURI2="vm-rnop2";
	public static final String VirtualMachineRequestNotificationOutboundPortURI3="vm-rnop3";

	// Virtual machines management ports
	public static final String	ApplicationVMManagementInboundPortURI1 = "avm-ibp" ;
	public static final String ApplicationVMManagementInboundPortURI2="avm-ibp1";
	public static final String ApplicationVMManagementInboundPortURI3="avm-ibp2";
	public static final String	ApplicationVMManagementOutboundPortURI = "avm-obp" ;
	public static final String	ApplicationVMManagementOutboundPortURI1 = "avm-obp1";
	public static final String	ApplicationVMManagementOutboundPortURI2 = "avm-obp2";


	/** Request Dispatcher **/
	protected RequestDispatcher requestDispatcher;

	/** Request Generator **/
	protected RequestGenerator requestGenerator;

	/** Port connected to the computer component to access its services.	*/
	protected ComputerServicesOutboundPort				csPort ;
	/** Port connected to the computer component to receive the static
	 *  state data.															*/
	protected ComputerStaticStateDataOutboundPort		cssPort ;
	/** Port connected to the computer component to receive the dynamic
	 *  state data.															*/
	protected ComputerDynamicStateDataOutboundPort		cdsPort ;
	/** Port connected to the first virtual machine to manage its cores */
	protected ApplicationVMManagementOutboundPort		avmmop1 ;
	/** Port connected to the second virtual machine to manage its cores */
	protected ApplicationVMManagementOutboundPort		avmmop2 ;
	/** Port connected to the third virtual machine to manage its cores */
	protected ApplicationVMManagementOutboundPort       avmmop3 ;

	/** Port connected to the request generator component to manage its
	 *  execution (starting and stopping the request generation).			*/
	protected RequestGeneratorManagementOutboundPort	rgmop ;

	// ------------------------------------------------------------------------
	// Component virtual machine constructors
	// ------------------------------------------------------------------------

	public				TestRequestDispatcher()
			throws Exception
	{
		super();
	}

	// ------------------------------------------------------------------------
	// Component virtual machine methods
	// ------------------------------------------------------------------------

	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.configureLogging("", "", 0, '|') ;
		Processor.DEBUG = true ;

		// --------------------------------------------------------------------
		// Create and deploy a computer component with its 2 processors and
		// each with 2 cores.
		// --------------------------------------------------------------------
		String computerURI = "computer0" ;
		int numberOfProcessors = 2 ;
		int numberOfCores = 2 ;
		Set<Integer> admissibleFrequencies = new HashSet<Integer>() ;
		admissibleFrequencies.add(1500) ;	// Cores can run at 1,5 GHz
		admissibleFrequencies.add(3000) ;	// and at 3 GHz
		Map<Integer,Integer> processingPower = new HashMap<Integer,Integer>() ;
		processingPower.put(1500, 1500000) ;	// 1,5 GHz executes 1,5 Mips
		processingPower.put(3000, 3000000) ;	// 3 GHz executes 3 Mips
		Computer c = new Computer(
				computerURI,
				admissibleFrequencies,
				processingPower,  
				1500,		// Test scenario 1, frequency = 1,5 GHz
				// 3000,	// Test scenario 2, frequency = 3 GHz
				1500,		// max frequency gap within a processor
				numberOfProcessors,
				numberOfCores,
				ComputerServicesInboundPortURI,
				ComputerStaticStateDataInboundPortURI,
				ComputerDynamicStateDataInboundPortURI) ;
		this.addDeployedComponent(c) ;

		// Create a mock-up computer services port to later allocate its cores
		// to the application virtual machine.
		this.csPort = new ComputerServicesOutboundPort(
				ComputerServicesOutboundPortURI,
				new AbstractComponent() {}) ;
		this.csPort.publishPort() ;
		this.csPort.doConnection(
				ComputerServicesInboundPortURI,
				ComputerServicesConnector.class.getCanonicalName()) ;
		// --------------------------------------------------------------------

		// --------------------------------------------------------------------
		// Create the computer monitor component and connect its to ports
		// with the computer component.
		// --------------------------------------------------------------------
		ComputerMonitor cm =
				new ComputerMonitor(computerURI,
						true,
						ComputerStaticStateDataOutboundPortURI,
						ComputerDynamicStateDataOutboundPortURI) ;
		this.addDeployedComponent(cm) ;
		this.cssPort =
				(ComputerStaticStateDataOutboundPort)
				cm.findPortFromURI(ComputerStaticStateDataOutboundPortURI) ;
		this.cssPort.doConnection(
				ComputerStaticStateDataInboundPortURI,
				DataConnector.class.getCanonicalName()) ;

		this.cdsPort =
				(ComputerDynamicStateDataOutboundPort)
				cm.findPortFromURI(ComputerDynamicStateDataOutboundPortURI) ;
		this.cdsPort.
		doConnection(
				ComputerDynamicStateDataInboundPortURI,
				ControlledDataConnector.class.getCanonicalName()) ;

		// --------------------------------------------------------------------

		// --------------------------------------------------------------------
		// Create 3 ApplicationVM components and deploy it
		// --------------------------------------------------------------------
		ApplicationVM vm =
				new ApplicationVM("vm1",	// application component URI
						ApplicationVMManagementInboundPortURI1,
						VirtualMachineRequestSubmissionInboundPortURI1,
						VirtualMachineRequestNotificationOutboundPortURI1) ;

		ApplicationVM vm1 =
				new ApplicationVM("vm2",	// application component URI
						ApplicationVMManagementInboundPortURI2,
						VirtualMachineRequestSubmissionInboundPortURI2,
						VirtualMachineRequestNotificationOutboundPortURI2) ;


		ApplicationVM vm2 =
				new ApplicationVM("vm3",	// application component URI
						ApplicationVMManagementInboundPortURI3,
						VirtualMachineRequestSubmissionInboundPortURI3,
						VirtualMachineRequestNotificationOutboundPortURI3) ;

		this.addDeployedComponent(vm);
		this.addDeployedComponent(vm1);
		this.addDeployedComponent(vm2);



		// Create a mock up port to manage the AVM components (allocate cores).
		this.avmmop1 = new ApplicationVMManagementOutboundPort(ApplicationVMManagementOutboundPortURI,new AbstractComponent() {}) ;
		this.avmmop1.publishPort() ;
		this.avmmop1.doConnection(ApplicationVMManagementInboundPortURI1,ApplicationVMManagementConnector.class.getCanonicalName()) ;

		this.avmmop1 =new ApplicationVMManagementOutboundPort(ApplicationVMManagementOutboundPortURI1,new AbstractComponent() {});
		this.avmmop1.publishPort();
		this.avmmop1.doConnection(ApplicationVMManagementInboundPortURI2,ApplicationVMManagementConnector.class.getCanonicalName());

		this.avmmop2 =new ApplicationVMManagementOutboundPort(ApplicationVMManagementOutboundPortURI2,new AbstractComponent(){});
		this.avmmop2.publishPort();
		this.avmmop2.doConnection(ApplicationVMManagementInboundPortURI3,ApplicationVMManagementConnector.class.getCanonicalName());


		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		vm.toggleTracing() ;
		vm.toggleLogging() ;
		vm1.toggleTracing() ;
		vm1.toggleLogging() ;
		vm2.toggleTracing() ;
		vm2.toggleLogging() ;

		// --------------------------------------------------------------------
		// Creating the requestDispatcher component.
		// --------------------------------------------------------------------
		requestDispatcher = new RequestDispatcher("rd1",RequestDispatcherRequestSubmissionInboundPortURI, RequestDispatcherRequestNotificationInboundPortURI, RequestDispatcherRequestNotificationOutboundPortURI);

		// Associating 3 VMs to the request dispatcher
		requestDispatcher.associateVirtualMachine(VirtualMachineRequestSubmissionInboundPortURI1);
		requestDispatcher.associateVirtualMachine(VirtualMachineRequestSubmissionInboundPortURI2);
		requestDispatcher.associateVirtualMachine(VirtualMachineRequestSubmissionInboundPortURI3);

		// Connecting VMs notifications ports with the one of the request dispatcher

		RequestNotificationOutboundPort vmrnop1 =
				(RequestNotificationOutboundPort) vm.findPortFromURI(
						VirtualMachineRequestNotificationOutboundPortURI1) ;
		vmrnop1.doConnection(
				RequestDispatcherRequestNotificationInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName()) ;
		RequestNotificationOutboundPort vmrnop2 =
				(RequestNotificationOutboundPort) vm1.findPortFromURI(
						VirtualMachineRequestNotificationOutboundPortURI2) ;
		vmrnop2.doConnection(
				RequestDispatcherRequestNotificationInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName()) ;

		RequestNotificationOutboundPort vmrnop3 =
				(RequestNotificationOutboundPort) vm2.findPortFromURI(
						VirtualMachineRequestNotificationOutboundPortURI3) ;
		vmrnop3.doConnection(
				RequestDispatcherRequestNotificationInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName()) ;

		this.addDeployedComponent(requestDispatcher);

		// Toggle on tracing and logging in the request dispatcher to
		// follow the submission and end of execution notification of
		// individual requests.
		requestDispatcher.toggleTracing() ;
		requestDispatcher.toggleLogging() ;

		// --------------------------------------------------------------------

		// --------------------------------------------------------------------
		// Creating the request generator component.
		// --------------------------------------------------------------------
		requestGenerator =
				new RequestGenerator(
						"rg",			// generator component URI
						500.0,			// mean time between two requests
						6000000000L,	// mean number of instructions in requests
						RequestGeneratorManagementInboundPortURI,
						RequestGeneratorRequestSubmissionOutboundPortURI,
						RequestGeneratorRequestNotificationInboundPortURI) ;
		this.addDeployedComponent(requestGenerator) ;

		// Toggle on tracing and logging in the request generator to
		// follow the submission and end of execution notification of
		// individual requests.
		requestGenerator.toggleTracing() ;
		requestGenerator.toggleLogging() ;

		// Connecting the request generator to request dispatcher
		
		RequestSubmissionOutboundPort rsobp =
				(RequestSubmissionOutboundPort) requestGenerator.findPortFromURI(
						RequestGeneratorRequestSubmissionOutboundPortURI) ;
		rsobp.doConnection(
				RequestDispatcherRequestSubmissionInboundPortURI,
				RequestSubmissionConnector.class.getCanonicalName()) ;

		// Create a mock up port to manage to request generator component
		// (starting and stopping the generation).
		this.rgmop = new RequestGeneratorManagementOutboundPort(
				RequestGeneratorManagementOutboundPortURI,
				new AbstractComponent() {}) ;
		this.rgmop.publishPort() ;
		this.rgmop.doConnection(
				RequestGeneratorManagementInboundPortURI,
				RequestGeneratorManagementConnector.class.getCanonicalName()) ;

		// Connecting request dispatcher notifications outbound port with the request generator one.
		RequestNotificationOutboundPort  out=(RequestNotificationOutboundPort) requestDispatcher.findPortFromURI(RequestDispatcherRequestNotificationOutboundPortURI);
		out.doConnection(RequestGeneratorRequestNotificationInboundPortURI, RequestNotificationConnector.class.getCanonicalName());

		// --------------------------------------------------------------------

		// complete the deployment at the component virtual machine level.
		super.deploy();
	}

	/**
	 * @see fr.upmc.components.cvm.AbstractCVM#start()
	 */
	@Override
	public void			start() throws Exception
	{
		super.start() ;

		// Allocate the 4 cores of the computer to the application virtual
		// machine.
		AllocatedCore[] ac = this.csPort.allocateCores(4) ;
		this.avmmop1.allocateCores(ac) ;
		ac = this.csPort.allocateCores(4) ;
		this.avmmop1.allocateCores(ac) ;
		ac = this.csPort.allocateCores(4) ;
		this.avmmop2.allocateCores(ac) ;
	}

	/**
	 * @see fr.upmc.components.cvm.AbstractCVM#shutdown()
	 */
	@Override
	public void			shutdown() throws Exception
	{

		// disconnect all ports explicitly connected in the deploy phase.
		this.csPort.doDisconnection() ;
		this.avmmop1.doDisconnection() ;
		this.rgmop.doDisconnection() ;

		// the super method will disconnect all the deployed components ports
		super.shutdown() ;
	}

	/**
	 * generate requests for 20 seconds and then stop generating.
	 *
	 * @throws Exception
	 */
	public void			testScenario() throws Exception
	{
		// start the request generation in the request generator.
		this.rgmop.startGeneration() ;
		// wait 10 seconds
		Thread.sleep(10000L) ;
		// disconnecting a virtual machine from the request dispatcher
		this.requestDispatcher.dissociateVirtualMachine(VirtualMachineRequestSubmissionInboundPortURI1);
		// wait 10 seconds
		Thread.sleep(10000L) ;
		// then stop the generation.
		this.rgmop.stopGeneration() ;
	}

	/**
	 * execute the test application.
	 * 
	 * @param args	command line arguments, disregarded here.
	 */
	public static void	main(String[] args)
	{
		// Uncomment next line to execute components in debug mode.
		// AbstractCVM.toggleDebugMode() ;
		try {
			final TestRequestDispatcher trg = new TestRequestDispatcher() ;
			// Deploy the components
			trg.deploy() ;
			System.out.println("starting...") ;
			// Start them.
			trg.start() ;
			// Execute the chosen request generation test scenario in a
			// separate thread.
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						trg.testScenario() ;
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			}).start() ;
			// Sleep to let the test scenario execute to completion.
			Thread.sleep(30000L) ;
			// Shut down the application.
			System.out.println("shutting down...") ;
			trg.shutdown() ;
			System.out.println("ending...") ;
			// Exit from Java.
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
