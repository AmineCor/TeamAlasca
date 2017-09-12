package com.teamalasca.applicationvm.ports;

import com.teamalasca.applicationvm.ApplicationVM;
import com.teamalasca.applicationvm.interfaces.ApplicationVMManagementI;

import fr.upmc.components.ComponentI;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;


/**
 * InboundPort for receiving  ManageCore
 */
public class ApplicationVMManagementInboundPort extends fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementInboundPort implements ApplicationVMManagementI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ApplicationVMManagementInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, owner);
	}

	/**
	 * Release an AllocatedCore
	 * @param core the AllocatedCore to be released
	 * @throws Exception 
	 */
	@Override
	public void releaseCore(final AllocatedCore core) throws Exception {
		final ApplicationVM app = (ApplicationVM) this.owner ;
		app.handleRequestSync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							 app.releaseCore(core);
							return null;
							
						}
					}) ;
		
	}

	@Override
	public void dispose() throws Exception {
		final ApplicationVM app = (ApplicationVM) this.owner ;
		app.handleRequestSync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							 app.dispose();
							return null;
							
						}
					}) ;
		
	}

}
