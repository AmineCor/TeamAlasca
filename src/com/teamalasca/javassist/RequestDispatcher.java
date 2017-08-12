package com.teamalasca.javassist;

import java.util.HashMap;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;

/**
 * The class <code>RequestDispatcher</code> (with Javassist) is a component receiving request submissions from
 * a given application, and dispatching these requests to the different virtual machines
 * allocated for this application.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class RequestDispatcher
extends com.teamalasca.requestdispatcher.RequestDispatcher
{

	/** Connector class. */
	private Class<?> connectorClass;
	
	/**
	 * Construct a <code>RequestDispatcher</code>.
	 * 
	 * @param requestDispatcherURI the request dispatcher URI.
	 * @param requestDispatcherManagementInboundPortURI the request dispatcher management inbound port URI.
	 * @param requestSubmissionInboundPortURI the request submission inbound port URI.
	 * @param requestNotificationInboundPortURI the request notification inbound port URI.
	 * @param requestNotificationOutboundPortURI the request notification outbound port URI.
	 * @param requestDispatcherDynamicStateDataInboundPortURI the request dispatcher dynamic state data inbound port URI.
	 * @throws Exception throws an exception if an error occured..
	 */
	public RequestDispatcher(
			String requestDispatcherURI,
		    String requestDispatcherManagementInboundPortURI,
			String requestSubmissionInboundPortURI,
			String requestNotificationInboundPortURI,
			String requestNotificationOutboundPortURI,
			String requestDispatcherDynamicStateDataInboundPortURI)
					throws Exception {
		super(
				requestDispatcherURI,
				requestDispatcherManagementInboundPortURI,
				requestSubmissionInboundPortURI,
				requestNotificationInboundPortURI,
				requestNotificationOutboundPortURI,
				requestDispatcherDynamicStateDataInboundPortURI);
	}

	/**
	 * Do an application connection.
	 * 
	 * @param uriPort the URI port.
	 * @param offeredInterface the offered interface.
	 * @param methodNamesMap the method names map.
	 * @throws Exception throws an exception if an error occured..
	 */
	public void doAppConnection(
			String uriPort, Class<?> offeredInterface,
			HashMap<String,String> methodNamesMap)
					throws Exception
	{		
		connectorClass = MakeConnector.makeConnectorClassJavassist(
				"makeConnectorClassJavassist.GenerateConnector",
				AbstractConnector.class,
				RequestNotificationI.class,
				offeredInterface, methodNamesMap);
		
		rnop.doConnection(uriPort, connectorClass.getCanonicalName());
	}
	
	/**
	 * @see fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI#acceptRequestTerminationNotification(fr.upmc.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception
	{
		logMessage("Request terminated " + r.getRequestURI());
		
		// same port
		((RequestNotificationI) rnop).notifyRequestTermination(r);
	}
	
}
