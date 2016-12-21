package com.teamalasca.fonction;

import java.util.HashMap;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;

public class RequestDispatcher extends com.teamalasca.requestdispatcher.RequestDispatcher {
	
	
	public RequestDispatcher(String requestDispatcherURI,
			String requestSubmissionInboundPortURI,
			String requestNotificationInboundPortURI,
			String requestNotificationOutboundPortURI) throws Exception {
		super(requestDispatcherURI, requestSubmissionInboundPortURI,
				requestNotificationInboundPortURI, requestNotificationOutboundPortURI);
	}

	Class<?> connectorClass;
	
	public void doAppConnexion(String uriPort, Class<?> offeredInterface,HashMap<String,String> methodNamesMap) throws Exception
	{		
		connectorClass = fcMakeconnector.makeConnectorClassJavassist("makeConnectorClassJavassist.GenerateConnector",AbstractConnector.class, RequestNotificationI.class, offeredInterface, methodNamesMap) ; 
		//Je sais pas c'est quoi ce port
		rsb.doConnection(uriPort, connectorClass.getCanonicalName());
	}
	
	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		logMessage("Request terminated "+r.getRequestURI());
		//Pareil
		((RequestNotificationI)rsb).notifyRequestTermination(r);
	}
}
