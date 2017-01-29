package com.teamalasca.requestdispatcher.ports;

import com.teamalasca.requestdispatcher.RequestDispatcher;
import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherManagementI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;


public class RequestDispatcherManagementInboundPort
extends	AbstractInboundPort
implements RequestDispatcherManagementI
{
	
	private static final long serialVersionUID = 1L;

	public RequestDispatcherManagementInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(RequestDispatcherManagementI.class, owner);

		assert owner != null && owner instanceof RequestDispatcher;
	}

	public RequestDispatcherManagementInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, RequestDispatcherManagementI.class, owner);

		assert owner != null && owner instanceof RequestDispatcher;
	}


	@Override
	public void associateVirtualMachine(
			final String virtualMachineRequestSubmissionInboundPortURI)
			throws Exception {
		final RequestDispatcher rd = (RequestDispatcher) this.owner;
		this.owner.handleRequestAsync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							rd.associateVirtualMachine(virtualMachineRequestSubmissionInboundPortURI);
							return null;
						}
					});		
	}

	@Override
	public void dissociateVirtualMachine(
			final String virtualMachineRequestSubmissionInboundPortURI)
			throws Exception {
		final RequestDispatcher rd = (RequestDispatcher) this.owner;
		this.owner.handleRequestAsync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							rd.dissociateVirtualMachine(virtualMachineRequestSubmissionInboundPortURI);
							return null;
						}
					});
	}

	@Override
	public boolean hasOnlyOneVirtualMachine() throws Exception {
		final RequestDispatcher rd = (RequestDispatcher) this.owner;
		return this.owner.handleRequestSync(
					new ComponentI.ComponentService<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return rd.hasOnlyOneVirtualMachine();
						}
					});
	}

}
