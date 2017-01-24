package com.teamalasca.autonomiccontroller;

import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherStateDataConsumerI;
import com.teamalasca.requestdispatcher.ports.RequestDispatcherDynamicStateDataOutboundPort;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ports.AbstractPort;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;

public final class AutonomicController extends AbstractComponent
implements RequestDispatcherStateDataConsumerI {
	
	/** A private URI to identify this autonomic controller, for debug purpose */
	private final String URI;
	
	/** URI of the dispatcher associated with this autonomic controller */
	private String requestDispatcherURI;
	
	/** Outbound port connected to the request dispatcher component to receive its data */
	private RequestDispatcherDynamicStateDataOutboundPort rddsdop;
	
	private Double requestExecutionAverage;
	
	public AutonomicController(String autonomicControllerURI) {
		super();
		this.URI = autonomicControllerURI;
	}
	
	public AutonomicController() {
		this(AbstractPort.generatePortURI());
	}

	@Override
	public void acceptRequestDispatcherDynamicData(String dispatcherURI,
			RequestDispatcherDynamicStateI currentDynamicState)
			throws Exception {
		
		this.logMessage(this.toString() + "received a message from a dispatcher");
		
		if(dispatcherURI != requestDispatcherURI) // data received from an unknown dispatcher
			return;
		
		synchronized (requestExecutionAverage) {
			requestExecutionAverage = currentDynamicState.getRequestExecutionTimeAverage();
		}
	}
	
	/** Connecting the autonomic controller with the request dispatcher */
	public void doConnectionWithRequestDispatcher(final String requestDispatcherURI,final String requestDispatcherDynamicStateDataInboundPortURI) throws Exception{
		
		this.requestDispatcherURI = requestDispatcherURI;
		
		this.rddsdop = new RequestDispatcherDynamicStateDataOutboundPort(this, requestDispatcherURI);
		this.addPort(rddsdop);
		this.rddsdop.publishPort();
		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class) ;
		this.rddsdop.doConnection(
				requestDispatcherDynamicStateDataInboundPortURI,
				ControlledDataConnector.class.getCanonicalName());
		this.rddsdop.startUnlimitedPushing(200);
	}
	
	@Override
	public String toString() {
		return "autonomic controller '"+this.URI+"'";
	}

}
