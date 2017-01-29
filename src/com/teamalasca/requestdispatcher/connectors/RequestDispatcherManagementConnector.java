package com.teamalasca.requestdispatcher.connectors;

import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherManagementI;

import fr.upmc.components.connectors.AbstractConnector;


public class RequestDispatcherManagementConnector
extends	AbstractConnector
implements RequestDispatcherManagementI
{

	@Override
	public void associateVirtualMachine(
			String virtualMachineRequestSubmissionInboundPortURI)
			throws Exception {
		((RequestDispatcherManagementI) this.offering).
		associateVirtualMachine(virtualMachineRequestSubmissionInboundPortURI);		
	}

	@Override
	public void dissociateVirtualMachine(
			String virtualMachineRequestSubmissionInboundPortURI)
			throws Exception {
		((RequestDispatcherManagementI) this.offering).
		dissociateVirtualMachine(virtualMachineRequestSubmissionInboundPortURI);		
	}

	@Override
	public boolean hasOnlyOneVirtualMachine() throws Exception
	{
		return ((RequestDispatcherManagementI) this.offering).hasOnlyOneVirtualMachine();
	}
	
}
