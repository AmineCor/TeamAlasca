package com.teamalasca.requestdispatcher.interfaces;


public interface RequestDispatcherManagementI
{
	
	public void associateVirtualMachine(final String virtualMachineRequestSubmissionInboundPortURI) throws Exception;
	public void dissociateVirtualMachine(final String virtualMachineRequestSubmissionInboundPortURI) throws Exception;
	public boolean hasOnlyOneVirtualMachine() throws Exception;
	
}
