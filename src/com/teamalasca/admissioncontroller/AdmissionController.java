
package com.teamalasca.admissioncontroller;


import com.teamalasca.admissioncontroller.connectors.AdmissionNotificationConnector;
import com.teamalasca.admissioncontroller.interfaces.AdmissionNotificationI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestHandlerI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestSubmitterI;
import com.teamalasca.admissioncontroller.ports.AdmissionNotificationOutboundPort;
import com.teamalasca.admissioncontroller.ports.AdmissionRequestInboundPort;
import com.teamalasca.autonomiccontroller.AutonomicController;
import com.teamalasca.requestdispatcher.RequestDispatcher;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ports.AbstractPort;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStateDataConsumerI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;

/**
 * An admission controller is a component receiving admission requests from applications
 * and manage it allocating available cores to execute these applications.
 */
public class AdmissionController extends AbstractComponent
implements AdmissionRequestHandlerI,ComputerStateDataConsumerI {

	private static final int CORES_NUMBER_THRESHOLD = 4;

	/** Internal URI for debug purpose */
	private final String URI;
	
	private String computerURI;
	
	/** */
	private String computerServiceInboundPortURI;
	
	private String computerDynamicStateDataInboundPortURI;


	/** Outbound port of the admission controller sending admissions notifications */
	private final AdmissionNotificationOutboundPort anop;

	/** Inbound port of the admission controller receiving admission requests from applications */
	private final AdmissionRequestInboundPort asibp;

	/** Outbound port connected to the computer component to receive its data */
	private ComputerDynamicStateDataOutboundPort cdsdop;

	/** Internal port to manage the application virtual machines allocated. In specially, allocate its cores */
	private ApplicationVMManagementOutboundPort avmmop;


	private boolean[][] ressources;

	public AdmissionController(final String admissionControllerURI,
			final String admissionRequestInboundPortURI,
			final String admissionNotifiationOutboundPortURI) throws Exception
	{
		super(1, 1);
		
		// Preconditions
		assert admissionControllerURI != null;
		assert admissionRequestInboundPortURI != null;
		assert admissionNotifiationOutboundPortURI != null;
		assert computerURI != null;
		assert computerDynamicStateDataInboundPortURI != null;
		
		this.URI=admissionControllerURI;

		// create port receiving admission request from applications
		this.asibp = new AdmissionRequestInboundPort(admissionRequestInboundPortURI,this);
		this.addOfferedInterface(AdmissionRequestSubmitterI.class);
		this.addPort(asibp);
		this.asibp.publishPort();

		// create port sending notifications to applications
		this.anop = new AdmissionNotificationOutboundPort(admissionNotifiationOutboundPortURI,this);
		this.addRequiredInterface(AdmissionNotificationI.class);
		this.addPort(anop);
		this.anop.publishPort();

		// create a mock up port to manage the application virtual machine (allocate cores).
		avmmop = new ApplicationVMManagementOutboundPort(
				AbstractPort.generatePortURI(),
				this);
		this.addPort(avmmop);
		avmmop.publishPort();

		ressources = new boolean[0][0];
	}

	public AdmissionController(
			final String admissionRequestInboundPortURI,
			final String admissionNotifiationOutboundPortURI) throws Exception
	{
		this(AbstractPort.generatePortURI(), admissionRequestInboundPortURI, admissionNotifiationOutboundPortURI);
	}


	/** Connecting the admission controller with a computer component */
	public void doConnectionWithComputer(final String computerURI, final String computerServiceInboundPortURI, final String computerDynamicStateDataInboundPortURI) throws Exception
	{
		this.computerURI = computerURI;
		this.computerServiceInboundPortURI = computerServiceInboundPortURI;
		this.computerDynamicStateDataInboundPortURI = computerDynamicStateDataInboundPortURI;
		
		// create port to receive computer data
		this.cdsdop = new ComputerDynamicStateDataOutboundPort(this, computerURI);
		this.addPort(cdsdop) ;
		this.cdsdop.publishPort() ;
		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class) ;
		this.cdsdop.
		doConnection(
				computerDynamicStateDataInboundPortURI,
				ControlledDataConnector.class.getCanonicalName()) ;
		this.cdsdop.startUnlimitedPushing(200);
	}

	@Override
	public void acceptComputerDynamicData(String computerURI, ComputerDynamicStateI currentDynamicState)
			throws Exception 
	{
		synchronized (ressources)
		{
			ressources = currentDynamicState.getCurrentCoreReservations();
		}
	}

	@Override
	public void handleAdmissionRequest(AdmissionRequestI request) throws Exception
	{

		logMessage(this.toString()+" receive an admission request from app(uri:"+request.getApplicationURI()+")");

		if(hasEnoughResources(CORES_NUMBER_THRESHOLD))
			acceptAdmissionRequest(request);
		else
			refuseAdmissionRequest(request);	
	}

	@Override
	public void handleAdmissionRequestAndNotify(AdmissionRequestI request) throws Exception
	{
		this.handleAdmissionRequest(request);

		// perform the notification
		anop.doConnection(request.getApplicationAdmissionNotificationInboundPortURI(),AdmissionNotificationConnector.class.getCanonicalName());			
		anop.acceptAdmissionNotification(request);
		anop.doDisconnection();
	}

	/**
	 * @return true if the computer has enough resources to allocate a new virtual machine
	 */
	private boolean hasEnoughResources(int requestedCoreNumber)
	{
		synchronized(ressources){
			int count = 0;
			for (int i=0; i< ressources.length; i++)
			{
				for (int j=0; j<ressources[i].length; j++)
				{
					if (!ressources[i][j]) {
						count++;
					}
				}
			}
			return count>=requestedCoreNumber;
		}
	}

	private void acceptAdmissionRequest(AdmissionRequestI request) throws Exception
	{
		createComponents(request);
		request.acceptRequest();

		logMessage(this.toString()+" accept the admission of app(uri:"+request.getApplicationURI()+")");
	}

	private void refuseAdmissionRequest(AdmissionRequestI request) throws Exception
	{
		request.refuseRequest();

		logMessage(this.toString()+" refuse the admission of app(uri:"+request.getApplicationURI()+")");
	}

	// TODO rename this method
	private void createComponents(AdmissionRequestI request) throws Exception
	{
		//------------- Create the application virtual machine ------------------/
/*
		final String applicationVMURI = request.getApplicationURI() + "_vm";

		final String AVMApplicationVMManagementInboundPortURI = AbstractPort.generatePortURI();
		final String AVMRequestSubmissionInboundPortURI = AbstractPort.generatePortURI();
		final String AVMRequestNotificationOutboundPortURI = AbstractPort.generatePortURI();

		final ApplicationVM applicationVM = new ApplicationVM(applicationVMURI,
				AVMApplicationVMManagementInboundPortURI,
				AVMRequestSubmissionInboundPortURI,
				AVMRequestNotificationOutboundPortURI);

		// allocate its cores
		AllocatedCore[] ac = this.csop.allocateCores(CORES_NUMBER_THRESHOLD) ;

		this.avmmop.doConnection(AVMApplicationVMManagementInboundPortURI, ApplicationVMManagementConnector.class.getCanonicalName());

		this.avmmop.allocateCores(ac) ;
		this.avmmop.doDisconnection();
*/


		// ------------- Create the request dispatcher ------------------/

		final String RDURI = AbstractPort.generatePortURI();
		final String RDRequestNotificationInboundPortURI = AbstractPort.generatePortURI();
		final String RDRequestSubmissionInboundPortURI = AbstractPort.generatePortURI();
		final String RDRequestNotificationOutboundPortURI = AbstractPort.generatePortURI();
		final String RDDynamicStateDataInboundPortURI = AbstractPort.generatePortURI();

		final RequestDispatcher rd = new RequestDispatcher(RDURI,
				RDRequestSubmissionInboundPortURI,
				RDRequestNotificationInboundPortURI,
				RDRequestNotificationOutboundPortURI,RDDynamicStateDataInboundPortURI);
		
		// -------- Create the autonomic controller to manage the request dispatcher and connect them ------/
		final AutonomicController ac = new AutonomicController(computerServiceInboundPortURI,
				computerURI,
				computerDynamicStateDataInboundPortURI,
				RDURI,
				RDDynamicStateDataInboundPortURI);
		
		// TODO cige yves saint laurent
		// Active tracing and logging
		if (isTracing()) {
			ac.toggleTracing();
			rd.toggleTracing();
		}
		
		if (isLogging()) {
			ac.toggleLogging();
			rd.toggleLogging();
		}


		// ------- Connect the request dispatcher with the application virtual machine ------/
/*
		requestDispatcher.associateVirtualMachine(AVMRequestSubmissionInboundPortURI);

		applicationVM.findPortFromURI(AVMRequestNotificationOutboundPortURI)
		.doConnection
		(RDRequestNotificationInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName());
*/
		
		// -------- Port URI's are shared through the request ------/

		request.setRequestSubmissionInboundPortURI(RDRequestSubmissionInboundPortURI);
		request.setRequestNotificationOutboundPortURI(RDRequestNotificationInboundPortURI);
	}

	@Override
	public String toString()
	{
		return "admission controller '"+this.URI+"'";
	}

	@Override
	public void acceptComputerStaticData(String computerURI,
			ComputerStaticStateI staticState) throws Exception
	{
		logMessage("recevied static data");
	}
}
