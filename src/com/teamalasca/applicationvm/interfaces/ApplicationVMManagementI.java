package com.teamalasca.applicationvm.interfaces;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

/**
 * 
 * The interface extends the default interface for adding the possibility to release a core.
 *
 */
public interface ApplicationVMManagementI extends fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI {
	
	/**
	 * Release an AllocatedCore
	 * @param ac the AllocatedCore
	 * @throws Exception
	 */
	public void releaseCore(AllocatedCore ac) throws Exception;
	
	/**
	 * Notify an application virtual machine to terminate its execution
	 * @throws Exception
	 */
	public void dispose() throws Exception;

}
