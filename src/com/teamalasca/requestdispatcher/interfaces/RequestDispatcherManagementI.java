package com.teamalasca.requestdispatcher.interfaces;

/**
 * The interface <code>RequestDispatcherManagementI</code> defines the services offered by
 * <code>RequestDispatcher</code> components.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Cl�ment George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public interface RequestDispatcherManagementI
{
	
	/**
	 * Associate a virtual machine with the request dispatcher.
	 * 
	 * @param virtualMachineRequestSubmissionInboundPortURI the virtual machine request submission inbound port URI to associate.
	 * @throws Exception throws an exception if an error occured..
	 */
	public void associateVirtualMachine(final String virtualMachineURI,final String virtualMachineRequestSubmissionInboundPortURI) throws Exception;
	
	/**
	 * Dissociate a virtual machine with the request dispatcher.
	 * 
	 * @param virtualMachineRequestSubmissionInboundPortURI the virtual machine request submission inbound port URI to dissociate.
	 * @throws Exception throws an exception if an error occured..
	 */
	public void dissociateVirtualMachine(final String virtualMachineRequestSubmissionInboundPortURI) throws Exception;
	
}
