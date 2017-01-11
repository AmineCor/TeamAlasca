package com.teamalasca.application;

import com.teamalasca.admissioncontroller.connectors.AdmissionRequestConnector;
import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionNotificationHandlerI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionNotificationI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestSubmitterI;
import com.teamalasca.admissioncontroller.ports.AdmissionNotificationInboundPort;
import com.teamalasca.admissioncontroller.ports.AdmissionRequestOutboundPort;
import com.teamalasca.admissioncontroller.requests.AdmissionRequest;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;


/**
 * 
 * @author Corchi - George
 *
 */
public class RGApplication
extends AbstractComponent
implements AdmissionNotificationHandlerI, RequestNotificationHandlerI
{

	RequestGenerator rg ;
	String URI;
	AdmissionNotificationInboundPort anibp;
	AdmissionRequestOutboundPort asop;

	public RGApplication(String uri, AbstractCVM acvm) throws Exception
	{
		super();

		URI = uri;
		rg = new RequestGenerator(
				URI + "_rg",		// generator component URI
				500.0,				// mean time between two requests
				6000000000L,		// mean number of instructions in requests
				URI + "_rg_mip",
				URI + "_rg_rsobp",
				URI + "_rg_rnibp"
		);
		acvm.addDeployedComponent(rg);
		
		rg.toggleLogging();
		rg.toggleTracing();
		
		this.addOfferedInterface(AdmissionNotificationI.class);
		this.anibp = new AdmissionNotificationInboundPort(URI + "_anibp", this) ;
		this.addPort(this.anibp) ;
		this.anibp.publishPort() ;

		this.addRequiredInterface(AdmissionRequestSubmitterI.class);
		this.asop = new AdmissionRequestOutboundPort(URI + "_asobp", this) ;
		this.addPort(this.asop) ;
		this.asop.publishPort() ;
	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r)
			throws Exception 
	{
		rg.acceptRequestTerminationNotification(r);
	}

	public void	startApp() throws Exception
	{
		logMessage(this.toString() + " starts");
		AdmissionRequest request = new AdmissionRequest(this.URI, anibp.getPortURI());
		this.asop.handleAdmissionRequestAndNotify(request);
	}

	public void doConnectionAdmissionControler(String asibp) throws Exception
	{
		this.asop.doConnection(asibp, AdmissionRequestConnector.class.getCanonicalName());
	}

	@Override
	public void acceptAdmissionNotification(AdmissionRequestI request) throws Exception 
	{
		if (request.isAccepted()) {
			String rqURI = request.getRequestSubmissionInboundPortURI();
			rg.findPortFromURI(URI + "_rg_rsobp").doConnection(rqURI, RequestSubmissionConnector.class.getCanonicalName());
			rg.startGeneration();
		}
	}

	@Override
	public String toString() {
		return "application '" + URI + "'";
	}

	public void startAsync()
	{
		// TODO Auto-generated method stub
	}

}
