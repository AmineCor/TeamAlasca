package com.teamalasca.requestdispatcher.connectors;

import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherManagementI;

import fr.upmc.components.connectors.AbstractConnector;

/**
 * The class <code>RequestDispatcherManagementConnector</code> implements 
 * a connector for ports exchanging through the interface 
 * <code>RequestDispatcherManagementI</code>.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Cl�ment George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class RequestDispatcherManagementConnector
extends	AbstractConnector
implements RequestDispatcherManagementI
{

	/**
	 * @see com.teamalasca.requestdispatcher.interfaces.RequestDispatcherManagementI#associateVirtualMachine(java.lang.String)
	 */
	@Override
	public void associateVirtualMachine(
			String virtualMachineURI,String virtualMachineRequestSubmissionInboundPortURI)
			throws Exception {
		((RequestDispatcherManagementI) this.offering).
		associateVirtualMachine(virtualMachineURI,virtualMachineRequestSubmissionInboundPortURI);		
	}

	/**
	 * @see com.teamalasca.requestdispatcher.interfaces.RequestDispatcherManagementI#dissociateVirtualMachine(java.lang.String)
	 */
	@Override
	public void dissociateVirtualMachine(
			String virtualMachineRequestSubmissionInboundPortURI)
			throws Exception {
		((RequestDispatcherManagementI) this.offering).
		dissociateVirtualMachine(virtualMachineRequestSubmissionInboundPortURI);		
	}
	
}
