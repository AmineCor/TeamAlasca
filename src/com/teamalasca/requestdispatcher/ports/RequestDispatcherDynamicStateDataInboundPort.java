package com.teamalasca.requestdispatcher.ports;

import com.teamalasca.requestdispatcher.RequestDispatcher;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.datacenter.ports.AbstractControlledDataInboundPort;


/**
 * The class <code>RequestDispatcherDynamicStateDataInboundPort</code> implements a data
 * inbound port offering the <code>RequestDispatcherDynamicStateI</code> interface.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class RequestDispatcherDynamicStateDataInboundPort
extends	AbstractControlledDataInboundPort
{
	
	/**
	 * A unique serial version identifier.
	 * @see java.io.Serializable#serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construct a <code>RequestDispatcherDynamicStateDataInboundPort</code>.
	 * 
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public RequestDispatcherDynamicStateDataInboundPort(ComponentI owner) throws Exception
	{
		super(owner);

		assert owner instanceof RequestDispatcher;
	}

	/**
	 * Construct a <code>RequestDispatcherDynamicStateDataInboundPort</code>.
	 * 
 	 * @param uri the uri of the port.
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public RequestDispatcherDynamicStateDataInboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, owner);

		assert owner instanceof RequestDispatcher;
	}

	/**
	 * @see fr.upmc.components.interfaces.DataOfferedI.PullI#get()
	 */
	@Override
	public DataOfferedI.DataI get() throws Exception
	{
		final RequestDispatcher requestDispatcher = (RequestDispatcher) this.owner;
		return requestDispatcher.handleRequestSync(
				new ComponentI.ComponentService<DataOfferedI.DataI>() {
					@Override
					public DataOfferedI.DataI call() throws Exception {
						return requestDispatcher.getDynamicState();
					}
				}
		);
	}
	
}
