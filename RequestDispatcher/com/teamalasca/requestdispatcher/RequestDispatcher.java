package com.teamalasca.requestdispatcher;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.ports.AbstractPort;
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


/**
 * A request dispatcher is a component receiving request submissions from
 * a given application, and dispatching these requests to the different VM
 * allocated for this application
 */
public final class RequestDispatcher extends AbstractComponent 
implements RequestSubmissionHandlerI , RequestNotificationHandlerI{

	/** URIs of the virtual machines allocated to this request dispatcher */
	private final List<String> virtualMachinesPortURIs;

	/** Outbound ports of the request dispatcher connected with the virtual machines allocated for execute the application.
	 * A linked list is used in order to deal with our dispatching policy */
	private final LinkedList<RequestSubmissionOutboundPort> rsops;

	/** Inbound port of the request dispatcher receiving notifications from the virtual machines */
	private final RequestNotificationInboundPort rnip;

	/** Inbound port of the request dispatcher connected with the application
	 */
	private final RequestSubmissionInboundPort rsip;

	/** Outbound port  of the request dispatcher sending notifications to the application */
	private final RequestNotificationOutboundPort rnop;

	public RequestDispatcher(final String requestSubmissionInboundPortURI ,
			final String requestNotificationInboundPortURI,final String requestNotificationOutboundPortURI)
					throws Exception {


		this.virtualMachinesPortURIs = new ArrayList<>(); // for now, no vm is allocated to this request dispatcher

		this.rsops =new LinkedList<>(); // and no outbound port is initialized
		this.addRequiredInterface(RequestSubmissionI.class);

		// whenever the other ports are initialized

		this.rsip = new RequestSubmissionInboundPort(requestSubmissionInboundPortURI, this);
		this.addPort(rsip);
		this.rsip.publishPort();
		this.addOfferedInterface(RequestSubmissionI.class);

		this.rnop = new RequestNotificationOutboundPort(requestNotificationOutboundPortURI, this) ;
		this.addPort(this.rnop) ;
		this.rnop.publishPort() ;
		this.addRequiredInterface(RequestNotificationI.class);

		this.rnip = new RequestNotificationInboundPort(requestNotificationInboundPortURI, this) ;
		this.addPort(this.rnip) ;
		this.rnip.publishPort() ;
		this.addOfferedInterface(RequestNotificationI.class);


	}

	public void associateVirtualMachine(final String virtualMachineRequestSubmissionInboundPortURI) throws Exception{

		if(this.virtualMachinesPortURIs.contains(virtualMachineRequestSubmissionInboundPortURI)){
			return;
		}

		// adding the new virtual machine to our internal vm list
		this.virtualMachinesPortURIs.add(virtualMachineRequestSubmissionInboundPortURI);

		// creating a new outbound port for the VM

		final String URI = AbstractPort.generatePortURI();

		RequestSubmissionOutboundPort rsop = new RequestSubmissionOutboundPort(URI,this);
		this.addPort(rsop);
		rsop.publishPort();

		// creating the connection from the new port to the VM
		rsop.doConnection(virtualMachineRequestSubmissionInboundPortURI, RequestSubmissionConnector.class.getCanonicalName());

		// adding the port to our internal outbound port list
		this.rsops.addFirst(rsop);
		
		logMessage("a new virtual machine (submission input port:'"+ virtualMachineRequestSubmissionInboundPortURI + "') has been associated to the request dispatcher");
	}

	public void dissociateVirtualMachine(final String virtualMachineRequestSubmissionInboundPortURI) throws Exception{

		if(!this.virtualMachinesPortURIs.contains(virtualMachineRequestSubmissionInboundPortURI)){
			return;
		}

		synchronized (rsops) {

			for(Iterator<RequestSubmissionOutboundPort> it = rsops.iterator();it.hasNext();){

				RequestSubmissionOutboundPort rsop = it.next();
				if(rsop.getServerPortURI().equals(virtualMachineRequestSubmissionInboundPortURI)){
					it.remove();
					rsop.unpublishPort();
					rsop.doDisconnection();
				}
			}

		}

		this.virtualMachinesPortURIs.remove(virtualMachineRequestSubmissionInboundPortURI);
		
		logMessage("virtual machine (submission input port:'"+ virtualMachineRequestSubmissionInboundPortURI + "') has been dissociated to the request dispatcher");
	}

	@Override
	public void acceptRequestSubmission(final RequestI r) throws Exception{

		if(rsops.isEmpty())
			throw new Exception("request '"+r.getRequestURI()+"' cant be handled because no vm is connected to the request dispatcher");

		RequestSubmissionOutboundPort rsop = rsops.removeFirst(); 

		if(!rsop.connected())
			throw new Exception("port '"+rsop.getPortURI()+"' of the request dispatcher is disconnected, that should not happen");

		rsop.submitRequest(r);
		logMessage("request '"+r.getRequestURI()+"' submitted to request dispatcher");

		rsops.addLast(rsop); // the port is pushed at the last position of the list, performing a good ports turnover
	}

	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {

		if(rsops.isEmpty())
			throw new Exception("request '"+r.getRequestURI()+"' cant be handled because no vm is connected to the request dispatcher");

		RequestSubmissionOutboundPort rsop = rsops.removeFirst(); 

		if(!rsop.connected())
			throw new Exception("port '"+rsop.getPortURI()+"' of the request dispatcher is disconnected, that should not happen");

		rsop.submitRequestAndNotify(r);
		logMessage("request '"+r.getRequestURI()+"' submitted to request dispatcher");

		rsops.addLast(rsop); // the port is pushed at the last position of the list, performing a good ports turnover
	}


	@Override
	public void shutdown() throws ComponentShutdownException
	{
		try{

			for(RequestSubmissionOutboundPort rsop:rsops){
				rsop.doDisconnection();
				rsop.unpublishPort();
			}

			rnop.doDisconnection();
			rnop.unpublishPort();
		}
		catch(Exception e)
		{
			throw new ComponentShutdownException(e);
		}
	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r)
			throws Exception {

		rnop.notifyRequestTermination(r);

	}
}


