package com.teamalasca.requestdispatcher;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;

public class RequestDispatcher extends AbstractComponent 
	implements RequestSubmissionHandlerI , RequestNotificationHandlerI{
	
	ArrayList<String> virtualMachineMap;
	String outbound;
    RequestSubmissionOutboundPort rsb;
    RequestNotificationOutboundPort rnob;
    RequestNotificationInboundPort rnib;
	
	public RequestDispatcher(String requestSubmissionOutbounPortUri,String requestSubmissionInboundPortUri ,
    						String AppUri,String requestNotificationInboundPortUri,String outbound1)
    throws Exception {
		
    	this.virtualMachineMap =new ArrayList<>();
    	outbound = requestSubmissionOutbounPortUri;
    	
    	this.addOfferedInterface(RequestSubmissionI.class);
    	this.addRequiredInterface(RequestSubmissionI.class);
    	
    	RequestSubmissionInboundPort rst = new RequestSubmissionInboundPort(requestSubmissionInboundPortUri, this);
    	this.addPort(rst);
    	rst.publishPort();
    	
    	rsb = new RequestSubmissionOutboundPort(outbound, this);
    	this.addPort(rsb);
    	rsb.publishPort();
    	
    	this.addOfferedInterface(RequestNotificationI.class);
		this.rnib = new RequestNotificationInboundPort(requestNotificationInboundPortUri, this) ;
		this.addPort(this.rnib) ;
		this.rnib.publishPort() ;
		
		this.addOfferedInterface(RequestNotificationI.class);
		this.rnob = new RequestNotificationOutboundPort(outbound1, this) ;
		this.addPort(this.rnob) ;
		this.rnob.publishPort() ;
    	
    }
	
	public void associate(String UriCo) throws Exception{
		
		if(!this.virtualMachineMap.contains(UriCo)){
			this.virtualMachineMap.add(UriCo);
		}
	}

	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		logMessage("RequestRepartitor : New Request "+r.getRequestURI());
		String port=virtualMachineMap.get(0);
		
		rsb.doConnection(port, RequestSubmissionConnector.class.getCanonicalName());
		rsb.submitRequest(r);
		
		virtualMachineMap.remove(port);
		virtualMachineMap.add(port);
    	rsb.doDisconnection();
	}

	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		
		logMessage("RequestDispatcher : New Request "+r.getRequestURI());

		String port=virtualMachineMap.get(0);
		
		rsb.doConnection(port, RequestSubmissionConnector.class.getCanonicalName());
		rsb.submitRequestAndNotify(r);
		
		virtualMachineMap.remove(port);
		virtualMachineMap.add(port);
		rsb.doDisconnection();
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException
	{
		try{
		
		}
		catch(Exception e)
		{
			throw new ComponentShutdownException(e);
		}
	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r)
			throws Exception {
		
		rnob.notifyRequestTermination(r);
		logMessage("RequestDispatcher : Request_termination "+r.getRequestURI());
		
	}
}


