package com.teamalasca.requestdispatcher.ports;

import com.teamalasca.requestdispatcher.RequestDispatcher;
import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherManagementI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * The class <code>RequestDispatcherManagementInboundPort</code> implements the
 * inbound port offering the interface <code>RequestDispatcherManagementI</code>.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class RequestDispatcherManagementInboundPort
extends	AbstractInboundPort
implements RequestDispatcherManagementI
{
	
	/**
	 * A unique serial version identifier.
	 * @see java.io.Serializable#serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construct a <code>RequestDispatcherManagementInboundPort</code>.
	 * 
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public RequestDispatcherManagementInboundPort(ComponentI owner) throws Exception
	{
		super(RequestDispatcherManagementI.class, owner);

		assert owner != null && owner instanceof RequestDispatcher;
	}
	
	/**
	 * Construct a <code>RequestDispatcherManagementInboundPort</code>.
	 * 
 	 * @param uri the uri of the port.
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public RequestDispatcherManagementInboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, RequestDispatcherManagementI.class, owner);

		assert owner != null && owner instanceof RequestDispatcher;
	}

	/**
	 * @see com.teamalasca.requestdispatcher.interfaces.RequestDispatcherManagementI#associateVirtualMachine(java.lang.String)
	 */
	@Override
	public void associateVirtualMachine(final String virtualMachineRequestSubmissionInboundPortURI)
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


	/**
	 * @see com.teamalasca.requestdispatcher.interfaces.RequestDispatcherManagementI#dissociateVirtualMachine(java.lang.String)
	 */
	@Override
	public void dissociateVirtualMachine(final String virtualMachineRequestSubmissionInboundPortURI)
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
	
	/**
	 * @see com.teamalasca.requestdispatcher.interfaces.RequestDispatcherManagementI#hasOnlyOneVirtualMachine()
	 */
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
