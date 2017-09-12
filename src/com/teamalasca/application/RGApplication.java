package com.teamalasca.application;

import com.teamalasca.admissioncontroller.connectors.AdmissionRequestConnector;
import com.teamalasca.admissioncontroller.interfaces.AdmissionNotificationHandlerI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionNotificationI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestSubmitterI;
import com.teamalasca.admissioncontroller.ports.AdmissionNotificationInboundPort;
import com.teamalasca.admissioncontroller.ports.AdmissionRequestOutboundPort;
import com.teamalasca.admissioncontroller.requests.AdmissionRequest;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;

/**
 * The class <code>RGApplication</code> represents a client application.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Cl�ment George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class RGApplication
extends AbstractComponent
implements AdmissionNotificationHandlerI,
RequestNotificationHandlerI
{

	/** Request generator for the application normal activity. */
	private RequestGenerator rg;

	/** Second Request generator for the application pick activity. */
	private RequestGenerator rg2;

	/** Component URI. */
	private String URI;

	/** Admission notification inbound port. */
	private AdmissionNotificationInboundPort anibp;

	/** Admission request outbound port. */
	private AdmissionRequestOutboundPort asop;

	/**
	 * Construct a <code>RGApplication</code>.
	 * 
	 * @param uri the component URI.
	 * @throws Exception throws an exception if an error occured..
	 */
	public RGApplication(String uri) throws Exception
	{
		super(1, 1);

		// create request generator
		URI = uri;
		rg = new RequestGenerator(
				URI + "_rg",		// generator component URI
				500,				// mean time between two requests
				600000000L,		// mean number of instructions in requests
				URI + "_rg_mip",
				URI + "_rg_rsobp",
				URI + "_rg_rnibp"
				);
		
		rg2 = new RequestGenerator(
				URI + "_rg2",		// generator component URI
				250,				// mean time between two requests
				600000000L,		// mean number of instructions in requests
				URI + "_rg2_mip",
				URI + "_rg2_rsobp",
				URI + "_rg2_rnibp"
				);

		//rg.toggleLogging();
		//rg.toggleTracing();

		// connect ports
		this.addOfferedInterface(AdmissionNotificationI.class);
		this.anibp = new AdmissionNotificationInboundPort(URI + "_anibp", this) ;
		this.addPort(this.anibp) ;
		this.anibp.publishPort() ;

		this.addRequiredInterface(AdmissionRequestSubmitterI.class);
		this.asop = new AdmissionRequestOutboundPort(URI + "_asobp", this) ;
		this.addPort(this.asop) ;
		this.asop.publishPort() ;
	}

	/**
	 * Start the application.
	 * 
	 * @throws Exception the exception/
	 */
	public void	startApp() throws Exception
	{
		logMessage(this.toString() + " starts");
		AdmissionRequest request = new AdmissionRequest(this.URI, anibp.getPortURI());
		this.asop.handleAdmissionRequestAndNotify(request);
	}

	public void stopApp() throws Exception{
		rg.stopGeneration();
		this.shutdown();
	}

	/**
	 * Connect the application with the admission controller.
	 * 
	 * @param asibp the admission request submitter inbound port.
	 * @throws Exception throws an exception if an error occured..
	 */
	public void doConnectionAdmissionControler(String asibp) throws Exception
	{
		this.asop.doConnection(asibp, AdmissionRequestConnector.class.getCanonicalName());
	}

	/** 
	 * @see com.teamalasca.admissioncontroller.interfaces.AdmissionNotificationHandlerI#acceptAdmissionNotification(com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI)
	 */
	@Override
	public void acceptAdmissionNotification(AdmissionRequestI request) throws Exception 
	{
		if (request.isAccepted()) {
			String rqURI = request.getRequestSubmissionInboundPortURI();
			rg.findPortFromURI(URI + "_rg_rsobp").doConnection(rqURI, RequestSubmissionConnector.class.getCanonicalName());
			rg2.findPortFromURI(URI + "_rg2_rsobp").doConnection(rqURI, RequestSubmissionConnector.class.getCanonicalName());
			rg.startGeneration();

			//let's make the second generator active every minute for 30 seconds

			new Thread(new Runnable() {

				@Override
				public void run() {

					while(!RGApplication.this.isShutdown()){
						try{
							Thread.sleep(60000);
							rg2.startGeneration();
							logMessage(RGApplication.this.toString() + " starts pike activity");
							Thread.sleep(30000);
							logMessage(RGApplication.this.toString() + " ends pike activity");
							rg2.stopGeneration();
						}catch(Exception e){
							e.printStackTrace();
						}
					}

				}
			}).start();
		}
	}

	/** 
	 * @see fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI#acceptRequestTerminationNotification(fr.upmc.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception 
	{
		rg.acceptRequestTerminationNotification(r);
	}

	/** 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "application '" + URI + "'";
	}

}
