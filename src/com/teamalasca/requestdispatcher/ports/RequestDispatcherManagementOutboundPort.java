package com.teamalasca.requestdispatcher.ports;

import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherManagementI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class RequestDispatcherManagementOutboundPort
extends	AbstractOutboundPort
implements RequestDispatcherManagementI
{
	public RequestDispatcherManagementOutboundPort(ComponentI owner) throws Exception
	{
		super(RequestDispatcherManagementI.class, owner) ;

		assert owner != null ;
	}

	public RequestDispatcherManagementOutboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, RequestDispatcherManagementI.class, owner) ;

		assert uri != null && owner != null ;
	}

	@Override
	public void associateVirtualMachine(
			String virtualMachineRequestSubmissionInboundPortURI)
			throws Exception {
		((RequestDispatcherManagementI) this.connector).
		associateVirtualMachine(virtualMachineRequestSubmissionInboundPortURI);		
	}

	@Override
	public void dissociateVirtualMachine(
			String virtualMachineRequestSubmissionInboundPortURI)
			throws Exception {
		((RequestDispatcherManagementI) this.connector).
		dissociateVirtualMachine(virtualMachineRequestSubmissionInboundPortURI);		
	}
	
}
