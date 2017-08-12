package com.teamalasca.requestdispatcher.ports;

import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherStateDataConsumerI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.ports.AbstractControlledDataOutboundPort;

/**
 * The class <code>ComputerDynamicDataOutboundPort</code> implements a data
 * outbound port requiring the <code>RequestDispatcherDynamicStateI</code> interface.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class RequestDispatcherDynamicStateDataOutboundPort
extends	AbstractControlledDataOutboundPort
{
	
	/**
	 * A unique serial version identifier.
	 * @see java.io.Serializable#serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/** Request dispatcher URI. */
	protected String requestDispatcherURI;

	/**
	 * Construct a <code>RequestDispatcherDynamicStateDataOutboundPort</code>.
	 * 
	 * @param owner the owner component.
	 * @param requestDispatcherURI the request dispatcher URI.
	 * @throws Exception throws an exception if an error occured..
	 */
	public RequestDispatcherDynamicStateDataOutboundPort(ComponentI owner, String requestDispatcherURI) throws Exception
	{
		super(owner);
		this.requestDispatcherURI = requestDispatcherURI;
		
		assert owner instanceof RequestDispatcherStateDataConsumerI;
	}
	
	/**
	 * Construct a <code>RequestDispatcherDynamicStateDataOutboundPort</code>.
	 * 
 	 * @param uri the uri of the port.
	 * @param owner the owner component.
	 * @param requestDispatcherURI the request dispatcher URI.
	 * @throws Exception throws an exception if an error occured..
	 */
	public RequestDispatcherDynamicStateDataOutboundPort(String uri, ComponentI owner, String requestDispatcherURI) throws Exception
	{
		super(uri, owner);
		this.requestDispatcherURI = requestDispatcherURI;

		assert owner instanceof RequestDispatcherStateDataConsumerI;
	}

	/**
	 * @see fr.upmc.components.interfaces.DataRequiredI.PushI#receive(fr.upmc.components.interfaces.DataRequiredI.DataI)
	 */
	@Override
	public void	receive(DataRequiredI.DataI d) throws Exception
	{
		((RequestDispatcherStateDataConsumerI) this.owner).
						acceptRequestDispatcherDynamicData(
								this.requestDispatcherURI,
								(RequestDispatcherDynamicStateI) d);
	}
	
}
