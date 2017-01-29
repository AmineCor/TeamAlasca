package com.teamalasca.fonction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.teamalasca.admissioncontroller.AdmissionController;
import com.teamalasca.application.RGApplication;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

public class TestJavassist 
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
	public static final String	ApplicationVMManagementOutboundPortURI = "avm-obp" ;
	public static final String	ApplicationVMManagementInboundPortURI_1 = "avm-ibp_1" ;
	public static final String	ApplicationVMManagementOutboundPortURI_1 = "avm-obp_1" ;
	public static final String	ApplicationVMManagementInboundPortURI_2 = "avm-ibp_2" ;
	public static final String	ApplicationVMManagementOutboundPortURI_2 = "avm-obp_2" ;
	public static final String	RequestSubmissionInboundPortURI_AVM = "rsibp-AVM" ;
	public static final String	RequestSubmissionInboundPortURI_AVM_1 = "rsibp-AVM_1" ;
	public static final String	RequestSubmissionInboundPortURI_AVM_2 = "rsibp-AVM_2" ;
	public static final String	RequestSubmissionInboundPortURI_RR = "rsibp-RR" ;
	public static final String	RequestSubmissionOutboundPortURI_RG = "rsobp-RG" ;
	public static final String	RequestSubmissionOutboundPortURI_RR = "rsobp-RR" ;
	public static final String	RequestNotificationInboundPortURI = "rnibp" ;
	public static final String	RequestNotificationOutboundPortURI = "rnobp" ;
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
	/** Port connected to the AVM component to allocate it cores.			*/
	protected ApplicationVMManagementOutboundPort		avmPort ;
	/** Port connected to the AVM component to allocate it cores.			*/
	protected ApplicationVMManagementOutboundPort		avmPort1 ;
	protected ApplicationVMManagementOutboundPort		avmPort2 ;
	/** Port of the request generator component sending requests to the
	 *  RR component.														*/
	protected RequestSubmissionOutboundPort				rsobp_rg ;
	/** Port of the request RequsetDispatcher component sending requests to the
	 *  AVM component.														*/
	protected RequestSubmissionOutboundPort				rsobp_rr ;
	/** Port of the request RequestDispatcher component sending requests to the
	 *  AVM component.														*/
	protected RequestSubmissionOutboundPort				rsobp_rr1 ;
	/** Port of the request generator component used to receive end of
	 *  execution notifications from the AVM component.						*/
	protected RequestNotificationOutboundPort			nobp ;
	/** Port connected to the request generator component to manage its
	 *  execution (starting and stopping the request generation).			*/
	protected RequestGeneratorManagementOutboundPort	rgmop ;
	
	protected RGApplication app;
	// ------------------------------------------------------------------------
	// Component virtual machine constructors
	// ------------------------------------------------------------------------

	public		TestJavassist()
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

		// --------------------------------------------------------------------
		ApplicationVM vm =
				new ApplicationVM("vm0",	// application vm component URI
						ApplicationVMManagementInboundPortURI,
						RequestSubmissionInboundPortURI_AVM,
						RequestNotificationOutboundPortURI) ;
		this.addDeployedComponent(vm) ;

		// Create a mock up port to manage the AVM component (allocate cores).
		this.avmPort = new ApplicationVMManagementOutboundPort(
				ApplicationVMManagementOutboundPortURI,
				new AbstractComponent() {}) ;
		this.avmPort.publishPort() ;
		this.avmPort.
		doConnection(
				ApplicationVMManagementInboundPortURI,
				ApplicationVMManagementConnector.class.getCanonicalName()) ;

		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		vm.toggleTracing() ;
		vm.toggleLogging() ;
		
		
		app = new RGApplication("MyApplication");
		this.addDeployedComponent(app);
		
	    AdmissionController ac = new AdmissionController ("AD", ComputerServicesOutboundPortURI,ComputerDynamicStateDataInboundPortURI);
		this.addDeployedComponent(ac);
		app.doConnectionAdmissionControler("AD_asibp");
		
		app.toggleTracing() ;
		app.toggleLogging() ;
		
		ac.toggleTracing() ;
		ac.toggleLogging() ;

		super.deploy();
	}

	/**
	 * @see fr.upmc.components.cvm.AbstractCVM#start()
	 */
	@Override
	public void			start() throws Exception
	{
		super.start();
		AllocatedCore[] ac = this.csPort.allocateCores(4) ;
		this.avmPort.allocateCores(ac) ;

	}

	/**
	 * @see fr.upmc.components.cvm.AbstractCVM#shutdown()
	 */
	@Override
	public void			shutdown() throws Exception
	{
		// disconnect all ports explicitly connected in the deploy phase.
		this.csPort.doDisconnection() ;
		app.shutdown();
		super.shutdown() ;
	}

	/**
	 * generate requests for 20 seconds and then stop generating.
	 *
	 * @throws Exception
	 */
	public void			testScenario() throws Exception
	{
		System.out.println("test Scenario") ;
		app.startApp();
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
			final TestJavassist jt = new TestJavassist() ;
			// Deploy the components
			jt.deploy() ;
			System.out.println("starting...") ;
			// Start them.
			jt.start() ;
			// Execute the chosen request generation test scenario in a
			// separate thread.
			
			System.out.println("starting...1") ;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						jt.testScenario() ;
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			}).start() ;
			
			// Sleep to let the test scenario execute to completion.
			Thread.sleep(90000L) ;
			// Shut down the application.
			System.out.println("shutting down...") ;
			jt.shutdown() ;
			System.out.println("ending...") ;
			// Exit from Java.
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

}
