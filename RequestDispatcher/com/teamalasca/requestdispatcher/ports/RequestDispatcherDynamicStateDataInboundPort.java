package com.teamalasca.requestdispatcher.ports;

import com.teamalasca.requestdispatcher.RequestDispatcher;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.datacenter.ports.AbstractControlledDataInboundPort;

public class RequestDispatcherDynamicStateDataInboundPort
extends	AbstractControlledDataInboundPort
{
	
	private static final long serialVersionUID = 1L;

	public RequestDispatcherDynamicStateDataInboundPort(ComponentI owner) throws Exception
	{
		super(owner) ;

		assert owner instanceof RequestDispatcher;
	}

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
