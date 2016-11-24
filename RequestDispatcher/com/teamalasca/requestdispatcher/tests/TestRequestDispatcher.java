package com.teamalasca.requestdispatcher.tests;


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

/**
 * The class <code>TestRequestGenerator</code> deploys a test application for
 * request generation in a single JVM (no remote execution provided) for a data
 * center simulation.
 *
 * <p><strong>Description</strong></p>
 * 
 * A data center has a set of computers, each with several multi-core
 * processors. Application virtual machines (AVM) are created to run
 * requests of an application. Each AVM is allocated cores of different
 * processors of a computer. AVM then receive requests for their application.
 * See the data center simulator documentation for more details about the
 * implementation of this simulation.
 *  
 * This test creates one computer component with two processors, each having
 * two cores. It then creates an AVM and allocates it all four cores of the
 * two processors of this unique computer. A request generator component is
 * then created and linked to the application virtual machine.  The test
 * scenario starts the request generation, wait for a specified time and then
 * stops the generation. The overall test allots sufficient time to the
 * execution of the application so that it completes the execution of all the
 * generated requests.
 * 
 * The waiting time in the scenario and in the main method must be manually
 * set by the tester.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 5 mai 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
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
	public static final String	ApplicationVMManagementInboundPortURI = "avm-ibp" ;
	public static final String ApplicationVMManagementInboundPortURI1="avm-ibp1";
	public static final String ApplicationVMManagementInboundPortURI2="avm-ibp2";
	public static final String	ApplicationVMManagementOutboundPortURI = "avm-obp" ;
	public static final String	ApplicationVMManagementOutboundPortURI1 = "avm-obp1";
	public static final String	ApplicationVMManagementOutboundPortURI2 = "avm-obp2";
			
	public static final String	RequestSubmissionInboundPortURI = "rsibp" ;
	public static final String RequestSubmissionInboundPortURI1="rsip1";
	public static final String RequestSubmissionInboundPortURI2="rsip2";
	public static final String	RequestSubmissionOutboundPortURI = "rsobp" ;
	public static final String	RequestNotificationInboundPortURI = "rnibp" ;
	public static final String	RequestNotificationOutboundPortURI = "rnobp" ;
	public static final String RequestNotificationOutboundPortURI1="rnop1";
	public static final String RequestNotificationOutboundPortURI2="rnop2";
	public static final String	RequestGeneratorManagementInboundPortURI = "rgmip" ;
	public static final String	RequestGeneratorManagementOutboundPortURI = "rgmop" ;


	/** Port connected to the computer component to access its services.	*/
	protected ComputerServicesOutboundPort				csPort ;
	/** Port connected to the computer component to receive the static
	 *  state data.															*/
	protected ComputerStaticStateDataOutboundPort		cssPort ;
	/** Port connected to the computer component to receive the dynamic
	 *  state data.															*/
	protected ComputerDynamicStateDataOutboundPort		cdsPort ;
	/** Port connected to the AVM component to allocate it cores(for the 3 virtual machine).			*/
	protected ApplicationVMManagementOutboundPort		avmPort ;
	protected ApplicationVMManagementOutboundPort		avmPort1 ;
	protected ApplicationVMManagementOutboundPort       avmPort2 ;
	/** Port of the request generator component sending requests to the
	 *  AVM component.														*/
	protected RequestSubmissionOutboundPort				rsobp ;
	/** Port of the request generator component used to receive end of
	 *  execution notifications from the AVM component.						*/
	protected RequestNotificationOutboundPort			nobp ;
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
		int numberOfProcessors = 6 ;
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
		// Create an Application VM component
		// --------------------------------------------------------------------
		ApplicationVM vm =
				new ApplicationVM("vm0",	// application vm component URI
								  ApplicationVMManagementInboundPortURI,
								  RequestSubmissionInboundPortURI,
								  RequestNotificationOutboundPortURI) ;

		ApplicationVM vm1 =
				new ApplicationVM("vm1",	// application vm1 component URI
								  ApplicationVMManagementInboundPortURI1,
								  RequestSubmissionInboundPortURI1,
								  RequestNotificationOutboundPortURI1) ;
		

		ApplicationVM vm2 =
				new ApplicationVM("vm2",	// application vm2 component URI
								  ApplicationVMManagementInboundPortURI2,
								  RequestSubmissionInboundPortURI2,
								  RequestNotificationOutboundPortURI2) ;
		this.addDeployedComponent(vm);
		this.addDeployedComponent(vm1);
		this.addDeployedComponent(vm2);
		
		

		// Create a mock up port to manage the AVM component (allocate cores).
		this.avmPort = new ApplicationVMManagementOutboundPort(
									ApplicationVMManagementOutboundPortURI,
									new AbstractComponent() {}) ;
		this.avmPort.publishPort() ;
		this.avmPort.
				doConnection(
					ApplicationVMManagementInboundPortURI,
					ApplicationVMManagementConnector.class.getCanonicalName()) ;
		this.avmPort1 =new ApplicationVMManagementOutboundPort(ApplicationVMManagementOutboundPortURI1,new AbstractComponent() {});
		this.avmPort1.publishPort();
		this.avmPort1.doConnection(ApplicationVMManagementInboundPortURI1,ApplicationVMManagementConnector.class.getCanonicalName());
		
		this.avmPort2 =new ApplicationVMManagementOutboundPort(ApplicationVMManagementOutboundPortURI2,new AbstractComponent(){});
		this.avmPort2.publishPort();
		this.avmPort2.doConnection(ApplicationVMManagementInboundPortURI2,ApplicationVMManagementConnector.class.getCanonicalName());
		

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
		RequestDispatcher distributeurDeRequete = new RequestDispatcher("PS","PE","RequetDis","PIN","PON");
		this.rsobp =
				(RequestSubmissionOutboundPort) distributeurDeRequete.findPortFromURI(
											"PS") ;
		rsobp.doConnection(
				 RequestSubmissionInboundPortURI,
				RequestSubmissionConnector.class.getCanonicalName()) ;
		
		distributeurDeRequete.associate(RequestSubmissionInboundPortURI);
		distributeurDeRequete.associate(RequestSubmissionInboundPortURI1);
		distributeurDeRequete.associate(RequestSubmissionInboundPortURI2);
		// Connection VM>DR 
		this.nobp =
				(RequestNotificationOutboundPort) vm.findPortFromURI(
											RequestNotificationOutboundPortURI) ;
			nobp.doConnection(
					"PIN",
					RequestNotificationConnector.class.getCanonicalName()) ;
		this.nobp =
					(RequestNotificationOutboundPort) vm1.findPortFromURI(
												RequestNotificationOutboundPortURI1) ;
				nobp.doConnection(
						"PIN",
						RequestNotificationConnector.class.getCanonicalName()) ;
				
		this.nobp =
						(RequestNotificationOutboundPort) vm2.findPortFromURI(
													RequestNotificationOutboundPortURI2) ;
					nobp.doConnection(
							"PIN",
							RequestNotificationConnector.class.getCanonicalName()) ;

					

		this.addDeployedComponent(distributeurDeRequete);	
		distributeurDeRequete.toggleTracing() ;
		distributeurDeRequete.toggleLogging() ;
		
		
		
		
		// --------------------------------------------------------------------

		// --------------------------------------------------------------------
		// Creating the request generator component.
		// --------------------------------------------------------------------
		RequestGenerator rg =
			new RequestGenerator(
					"rg",			// generator component URI
					500.0,			// mean time between two requests
					6000000000L,	// mean number of instructions in requests
					RequestGeneratorManagementInboundPortURI,
					RequestSubmissionOutboundPortURI,
					RequestNotificationInboundPortURI) ;
		this.addDeployedComponent(rg) ;

		// Toggle on tracing and logging in the request generator to
		// follow the submission and end of execution notification of
		// individual requests.
		rg.toggleTracing() ;
		rg.toggleLogging() ;

		// Connecting the request generator to the application virtual machine.
		// Request generators have three different interfaces:
		// - one for submitting requests to application virtual machines,
		// - one for receiving end of execution notifications from application
		//   virtual machines, and
		// - one for request generation management i.e., starting and stopping
		//   the generation process.
		this.rsobp =
			(RequestSubmissionOutboundPort) rg.findPortFromURI(
										RequestSubmissionOutboundPortURI) ;
		rsobp.doConnection(
				"PE",
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
		//Connection RD>GR Notifications
		RequestNotificationOutboundPort  out=(RequestNotificationOutboundPort) distributeurDeRequete.findPortFromURI("PON");
		out.doConnection(RequestNotificationInboundPortURI, RequestNotificationConnector.class.getCanonicalName());
		
		
		
		
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
		this.avmPort.allocateCores(ac) ;
		ac = this.csPort.allocateCores(4) ;
		this.avmPort1.allocateCores(ac) ;
	    ac = this.csPort.allocateCores(4) ;
		this.avmPort2.allocateCores(ac) ;
	}

	/**
	 * @see fr.upmc.components.cvm.AbstractCVM#shutdown()
	 */
	@Override
	public void			shutdown() throws Exception
	{
		// disconnect all ports explicitly connected in the deploy phase.
		this.csPort.doDisconnection() ;
		this.avmPort.doDisconnection() ;
		this.rsobp.doDisconnection() ;
		this.nobp.doDisconnection() ;
		this.rgmop.doDisconnection() ;

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
		// wait 20 seconds
		Thread.sleep(20000L) ;
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
			Thread.sleep(90000L) ;
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