package com.teamalasca.tests;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.teamalasca.admissioncontroller.AdmissionController;
import com.teamalasca.application.RGApplication;
import com.teamalasca.computer.Computer;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.tests.ComputerMonitor;
import fr.upmc.datacenterclient.tests.TestRequestGenerator;

/**
 * This class is a consistent components assembly allowing testing the admission controller
 * component.
 * Strongly inspired by the class {@link TestRequestGenerator}
 */
public class			TestAutonomicController
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
	public static final String	ComputerManageCoreInboundPortURI = "cmc-ib" ;

	// Admission controller ports
	private static final String AdmissionControllerAdmissionRequestInboundPortURI = "ac-arip";
	private static final String AdmissionControllerAdmissionNotifiationOutboundPortURI = "ac-anop";

	// Virtual machines ports
	public static final String	VirtualMachineRequestSubmissionInboundPortURI1 = "vm-rsip1" ;
	public static final String	VirtualMachineRequestSubmissionInboundPortURI2 = "vm-rsip2" ;
	public static final String	VirtualMachineRequestSubmissionInboundPortURI3 = "vm-rsip3" ;
	public static final String VirtualMachineRequestNotificationOutboundPortURI1 = "vm-rnop1" ;
	public static final String VirtualMachineRequestNotificationOutboundPortURI2="vm-rnop2" ;
	public static final String VirtualMachineRequestNotificationOutboundPortURI3="vm-rnop3" ;

	// Virtual machines management ports
	public static final String	ApplicationVMManagementInboundPortURI1 = "avm-ibp" ;
	public static final String ApplicationVMManagementInboundPortURI2="avm-ibp1";
	public static final String ApplicationVMManagementInboundPortURI3="avm-ibp2";
	public static final String	ApplicationVMManagementOutboundPortURI = "avm-obp" ;
	public static final String	ApplicationVMManagementOutboundPortURI1 = "avm-obp1";
	public static final String	ApplicationVMManagementOutboundPortURI2 = "avm-obp2";


	/** Admission Controller **/
	protected AdmissionController admissionController;

	/** Application 1 **/
	protected RGApplication app1;

	/** Application 2 **/
	protected RGApplication app2;

	/** Port connected to the computer component to access its services.	*/
	protected ComputerServicesOutboundPort				csPort ;
	/** Port connected to the computer component to receive the static
	 *  state data.															*/
	protected ComputerStaticStateDataOutboundPort		cssPort ;
	/** Port connected to the computer component to receive the dynamic
	 *  state data.															*/
	protected ComputerDynamicStateDataOutboundPort		cdsPort ;

	// ------------------------------------------------------------------------
	// Component virtual machine constructors
	// ------------------------------------------------------------------------

	public				TestAutonomicController()
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
				ComputerDynamicStateDataInboundPortURI,
				ComputerManageCoreInboundPortURI) ;
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
		// Creating the admission controller component
		// --------------------------------------------------------------------

		// --------------------------------------------------------------------

		admissionController  = new AdmissionController("ac1",
				AdmissionControllerAdmissionRequestInboundPortURI,
				AdmissionControllerAdmissionNotifiationOutboundPortURI);

		this.addDeployedComponent(admissionController);

		admissionController.doConnectionWithComputer(computerURI, ComputerManageCoreInboundPortURI, ComputerDynamicStateDataInboundPortURI);
		admissionController.toggleLogging();
		admissionController.toggleTracing();

		// -------------- Creating 2 applications ----------- //

		app1 = new RGApplication("app1");
		this.addDeployedComponent(app1);
		app1.doConnectionAdmissionControler(AdmissionControllerAdmissionRequestInboundPortURI);

		app1.toggleTracing();
		app1.toggleLogging();

		app2 = new RGApplication("app2");

		this.addDeployedComponent(app2);
		app2.doConnectionAdmissionControler(AdmissionControllerAdmissionRequestInboundPortURI);

		app2.toggleTracing() ;
		app2.toggleLogging() ;


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
	}

	/**
	 * @see fr.upmc.components.cvm.AbstractCVM#shutdown()
	 */
	@Override
	public void			shutdown() throws Exception
	{
		app1.shutdown();
		app2.shutdown();

		// disconnect all ports explicitly connected in the deploy phase.
		this.csPort.doDisconnection() ;
		this.cdsPort.doDisconnection();
		this.cssPort.doDisconnection();

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
		// wait a few seconds before lauching application
		Thread.sleep(200L) ;
		// start the first application
		this.app1.startApp();
		// wait 5 seconds
		Thread.sleep(5000L) ;
		// start the second application
		this.app2.startApp();
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
			final TestAutonomicController tac = new TestAutonomicController() ;
			// Deploy the components
			tac.deploy() ;
			System.out.println("starting...") ;
			// Start them.
			tac.start() ;
			// Execute the chosen request generation test scenario in a
			// separate thread.
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						tac.testScenario() ;
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			}).start() ;
			// Sleep to let the test scenario execute to completion.
			Thread.sleep(30000L) ;
			// Shut down the application.
			System.out.println("shutting down...") ;
			tac.shutdown() ;
			System.out.println("ending...") ;
			// Exit from Java.
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
