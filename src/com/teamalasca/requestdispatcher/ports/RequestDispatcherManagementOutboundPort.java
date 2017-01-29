package com.teamalasca.requestdispatcher.ports;

import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherManagementI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

/**
 * The class <code>RequestDispatcherManagementOutboundPort</code> implements the
 * inbound port requiring the interface <code>RequestDispatcherManagementI</code>.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class RequestDispatcherManagementOutboundPort
extends	AbstractOutboundPort
implements RequestDispatcherManagementI
{
	
	/**
	 * Construct an <code>RequestDispatcherManagementOutboundPort</code>.
	 * 
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public RequestDispatcherManagementOutboundPort(ComponentI owner) throws Exception
	{
		super(RequestDispatcherManagementI.class, owner);

		assert owner != null;
	}

	/**
	 * Construct an <code>RequestDispatcherManagementOutboundPort</code>.
	 * 
 	 * @param uri the uri of the port.
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public RequestDispatcherManagementOutboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, RequestDispatcherManagementI.class, owner);

		assert uri != null && owner != null;
	}

	/**
	 * @see com.teamalasca.requestdispatcher.interfaces.RequestDispatcherManagementI#associateVirtualMachine(java.lang.String)
	 */
	@Override
	public void associateVirtualMachine(String virtualMachineRequestSubmissionInboundPortURI)
			throws Exception {
		((RequestDispatcherManagementI) this.connector).
		associateVirtualMachine(virtualMachineRequestSubmissionInboundPortURI);		
	}

	/**
	 * @see com.teamalasca.requestdispatcher.interfaces.RequestDispatcherManagementI#dissociateVirtualMachine(java.lang.String)
	 */
	@Override
	public void dissociateVirtualMachine(String virtualMachineRequestSubmissionInboundPortURI)
			throws Exception {
		((RequestDispatcherManagementI) this.connector).
		dissociateVirtualMachine(virtualMachineRequestSubmissionInboundPortURI);		
	}

	/**
	 * @see com.teamalasca.requestdispatcher.interfaces.RequestDispatcherManagementI#hasOnlyOneVirtualMachine()
	 */
	@Override
	public boolean hasOnlyOneVirtualMachine() throws Exception
	{
		return ((RequestDispatcherManagementI) this.connector).
		hasOnlyOneVirtualMachine();
	}
	
}
