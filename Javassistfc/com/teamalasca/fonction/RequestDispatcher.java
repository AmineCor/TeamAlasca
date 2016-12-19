package com.teamalasca.fonction;

import java.util.HashMap;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;

public class RequestDispatcher extends com.teamalasca.requestdispatcher.RequestDispatcher {
	
	
	Class<?> connectorClass;

	public RequestDispatcher(String requestSubmissionOutbounPortUri,
			String requestSubmissionInboundPortUri, String AppUri,
			String requestNotificationInboundPortUri, String outbound1)
			throws Exception {
		super(requestSubmissionOutbounPortUri, requestSubmissionInboundPortUri, AppUri,
				requestNotificationInboundPortUri, outbound1);
		// TODO Auto-generated constructor stub
	}
	
	public void doAppConnexion(String uriPort, Class<?> offeredInterface,HashMap<String,String> methodNamesMap) throws Exception
	{		
		connectorClass = fcMakeconnector.makeConnectorClassJavassist("makeConnectorClassJavassist.GenerateConnector",AbstractConnector.class, RequestNotificationI.class, offeredInterface, methodNamesMap) ; 
		rsb.doConnection(uriPort, connectorClass.getCanonicalName());
	}
	
	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		logMessage("Request terminated "+r.getRequestURI());
		((RequestNotificationI)rsb).notifyRequestTermination(r);
	}


}
