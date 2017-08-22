package com.teamalasca.admissioncontroller;

import com.teamalasca.admissioncontroller.connectors.AdmissionNotificationConnector;
import com.teamalasca.admissioncontroller.interfaces.AdmissionNotificationI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestHandlerI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestSubmitterI;
import com.teamalasca.admissioncontroller.ports.AdmissionNotificationOutboundPort;
import com.teamalasca.admissioncontroller.ports.AdmissionRequestInboundPort;
import com.teamalasca.autonomiccontroller.AutonomicController;
import com.teamalasca.autonomiccontroller.connectors.AutonomicControllerServicesConnector;
import com.teamalasca.autonomiccontroller.ports.AutonomicControllerServicesOutboundPort;
import com.teamalasca.requestdispatcher.RequestDispatcher;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ports.AbstractPort;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStateDataConsumerI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;

/**
 * The class <code>AdmissionRequest</code> is a component receiving admission requests
 * from applications and check if they can be accept by the data center.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Cl�ment George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class AdmissionController
extends AbstractComponent
implements AdmissionRequestHandlerI,
		   ComputerStateDataConsumerI
{

	/** Internal URI for debug purpose. */
	private final String URI;

	/** Computer URI. */
	private String computerURI;

	/** Management core inbout port URI. */
	private String manageCoreInboundPortURI;

	/** Computer dynamic state data inbound port URI to get dynamic data from the computer. */
	private String computerDynamicStateDataInboundPortURI;

	/** Outbound port of the admission controller sending admissions notifications. */
	private final AdmissionNotificationOutboundPort anop;

	/** Inbound port of the admission controller receiving admission requests from applications. */
	private final AdmissionRequestInboundPort asibp;

	/** Outbound port connected to the computer component to receive its data. */
	private ComputerDynamicStateDataOutboundPort cdsdop;

	/** Resources of the computer. */
	private boolean[][] resources;

	/**
	 * Construct an <code>AdmissionController</code>.
	 * 
	 * @param admissionControllerURI the admission controller URI.
	 * @param admissionRequestInboundPortURI the admission request inbound port URI.
	 * @param admissionNotifiationOutboundPortURI the admission notification oubtound port URI.
	 * @throws Exception throws an exception if an error occured..
	 */
	public AdmissionController(
			final String admissionControllerURI,
			final String admissionRequestInboundPortURI,
			final String admissionNotifiationOutboundPortURI)
					throws Exception
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

		resources = new boolean[0][0];
	}

	/**
	 * Construct an <code>AdmissionController</code>.
	 * 
	 * @param admissionRequestInboundPortURI the admission request inbound port URI.
	 * @param admissionNotifiationOutboundPortURI the admission notification oubtound port URI.
	 * @throws Exception throws an exception if an error occured..
	 */
	public AdmissionController(
			final String admissionRequestInboundPortURI,
			final String admissionNotifiationOutboundPortURI)
					throws Exception
	{
		this(
				AbstractPort.generatePortURI(),
				admissionRequestInboundPortURI,
				admissionNotifiationOutboundPortURI);
	}


	/**
	 * Connect the admission controller with a computer component.
	 * 
	 * @param computerURI the computer URI to connect.
	 * @param manageCoreInboundPortURI the management core inbound port URI.
	 * @param computerDynamicStateDataInboundPortURI the computer dynamic state data inbound port URI.
	 * @throws Exception throws an exception if an error occured..
	 */
	public void doConnectionWithComputer(
			final String computerURI,
			final String manageCoreInboundPortURI,
			final String computerDynamicStateDataInboundPortURI)
					throws Exception
	{
		// keep the related URIs of the computer
		this.computerURI = computerURI;
		this.manageCoreInboundPortURI = manageCoreInboundPortURI;
		this.computerDynamicStateDataInboundPortURI = computerDynamicStateDataInboundPortURI;

		// create port to receive computer data
		this.cdsdop = new ComputerDynamicStateDataOutboundPort(this, computerURI);
		this.addPort(cdsdop);
		this.cdsdop.publishPort();
		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class);
		this.cdsdop.doConnection(
				computerDynamicStateDataInboundPortURI,
				ControlledDataConnector.class.getCanonicalName());
		
		// push data
		this.cdsdop.startUnlimitedPushing(200);
	}

	/** 
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerStateDataConsumerI#acceptComputerStaticData(java.lang.String, fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI)
	 */
	@Override
	public void acceptComputerStaticData(String computerURI, ComputerStaticStateI staticState)
			throws Exception
	{
	}
	
	/** 
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerStateDataConsumerI#acceptComputerDynamicData(java.lang.String, fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI)
	 */
	@Override
	public void acceptComputerDynamicData(String computerURI, ComputerDynamicStateI currentDynamicState)
			throws Exception 
	{
		// keep resources of the computer
		synchronized (resources) {
			resources = currentDynamicState.getCurrentCoreReservations();
		}
	}

	/** 
	 * @see com.teamalasca.admissioncontroller.interfaces.AdmissionRequestHandlerI#handleAdmissionRequest(com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI)
	 */
	@Override
	public void handleAdmissionRequest(AdmissionRequestI request) throws Exception
	{
		logMessage(this.toString() + " receive an admission request from app(uri:" + request.getApplicationURI() + ")");

		// accept the admission request if the computer has enough resources
		if (hasEnoughResources(AutonomicController.DEFAULT_CORE_NUMBER)) {
			acceptAdmissionRequest(request);
		}
		else {
			refuseAdmissionRequest(request);
		}
	}

	/* 
	 * @see com.teamalasca.admissioncontroller.interfaces.AdmissionRequestHandlerI#handleAdmissionRequestAndNotify(com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI)
	 */
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
	 * Check it the computer has resources  to allocate a new virtual machine.
	 * 
	 * @return true if the computer has enough resources to allocate a new virtual machine.
	 */
	private boolean hasEnoughResources(int requestedCoreNumber)
	{
		synchronized(resources) {
			int count = 0;
			for (int i = 0; i < resources.length; ++i)
			{
				for (int j = 0; j < resources[i].length; ++j)
				{
					if (!resources[i][j]) {
						++count;
					}
				}
			}
			
			return count >= requestedCoreNumber;
		}
	}

	/**
	 * Accept an admission request.
	 * 
	 * @param request the admission request.
	 * @throws Exception throws an exception if an error occured..
	 */
	private void acceptAdmissionRequest(AdmissionRequestI request) throws Exception
	{
		// create components : request dispatcher and autonomic controller
		createComponents(request);
		request.acceptRequest();

		logMessage(this.toString() + " accept the admission of app(uri:" + request.getApplicationURI() + ")");
	}

	/**
	 * Refuse an admission request.
	 * 
	 * @param request the admission request.
	 * @throws Exception throws an exception if an error occured..
	 */
	private void refuseAdmissionRequest(AdmissionRequestI request) throws Exception
	{
		request.refuseRequest();
		
		logMessage(this.toString() + " refuse the admission of app(uri:" + request.getApplicationURI() + ")");
	}

	/**
	 * Create the request dispatcher and the autonomic controller for the submitted application.
	 * 
	 * @param request the admission request.
	 * @throws Exception throws an exception if an error occured..
	 */
	private void createComponents(AdmissionRequestI request) throws Exception
	{
		// ------------- Create the request dispatcher ------------------/
		final String RDURI = AbstractPort.generatePortURI();
		final String RDManagementInboundPortURI = AbstractPort.generatePortURI();
		final String RDRequestNotificationInboundPortURI = AbstractPort.generatePortURI();
		final String RDRequestSubmissionInboundPortURI = AbstractPort.generatePortURI();
		final String RDRequestNotificationOutboundPortURI = AbstractPort.generatePortURI();
		final String RDDynamicStateDataInboundPortURI = AbstractPort.generatePortURI();

		new RequestDispatcher(
				RDURI,
				RDManagementInboundPortURI,
				RDRequestSubmissionInboundPortURI,
				RDRequestNotificationInboundPortURI,
				RDRequestNotificationOutboundPortURI,
				RDDynamicStateDataInboundPortURI);

		// -------- Create the autonomic controller to manage the request dispatcher and connect them ------/
		final String ACServicesInboundPortURI = AbstractPort.generatePortURI();
		final String ACServicesOutboundPortURI = AbstractPort.generatePortURI();
		
		final AutonomicController ac = new AutonomicController(
				ACServicesInboundPortURI,
				computerURI,
				computerDynamicStateDataInboundPortURI,
				manageCoreInboundPortURI,
				RDURI,
				RDManagementInboundPortURI,
				RDDynamicStateDataInboundPortURI,
				RDRequestNotificationInboundPortURI);

		// Active tracing and logging
		if (isTracing()) {
			ac.toggleTracing();
			//rd.toggleTracing();
		}

		if (isLogging()) {
			ac.toggleLogging();
			//rd.toggleLogging();
		}

		// -------- Port URI's are shared through the request ------/
		request.setRequestSubmissionInboundPortURI(RDRequestSubmissionInboundPortURI);
		request.setRequestNotificationOutboundPortURI(RDRequestNotificationInboundPortURI);
		
		// -------- Create the outbound port for the autonomic controller ------/
		final AutonomicControllerServicesOutboundPort acsop = new AutonomicControllerServicesOutboundPort(
				ACServicesOutboundPortURI,
				new AbstractComponent() {});
		acsop.publishPort();
		acsop.doConnection(ACServicesInboundPortURI, AutonomicControllerServicesConnector.class.getCanonicalName());
		
		// Do adaptation
		acsop.doPeriodicAdaptation();
	}
	
	/** 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "admission controller '" + this.URI + "'";
	}
	
}
