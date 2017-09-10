package com.teamalasca.autonomiccontroller.interfaces;

public interface AutonomicControllerNotificationI {
	
	/**
	 * Notify the autonomic controller that a virtual machine has finished its execution queue, and therefore 
	 * its resources can be liberated.
	 * @param aVirtualMachineURI
	 * @throws Exception
	 */
	public void notifyVirtualMachineUsageTermination(String aVirtualMachineURI) throws Exception;

}
