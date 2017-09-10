package com.teamalasca.autonomiccontroller.ports;

import com.teamalasca.autonomiccontroller.interfaces.AutonomicControllerNotificationI;
import com.teamalasca.autonomiccontroller.interfaces.AutonomicControllerServicesI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class AutonomicControllerNotificationOutboundPort extends AbstractOutboundPort implements AutonomicControllerNotificationI {
	
	/**
	 * Construct an <code>AutonomicControllerNotificationOutboundPort</code>.
	 * 
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public AutonomicControllerNotificationOutboundPort(ComponentI owner) throws Exception
	{
		super(AutonomicControllerServicesI.class, owner);
	}

	/**
	 * Construct an <code>AutonomicControllerNotificationOutboundPort</code>.
	 * 
 	 * @param uri the uri of the port.
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public AutonomicControllerNotificationOutboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, AutonomicControllerServicesI.class, owner);
	}

	@Override
	public void notifyVirtualMachineUsageTermination(String aVirtualMachineURI)
			throws Exception {
		((AutonomicControllerNotificationI) this.connector).notifyVirtualMachineUsageTermination(aVirtualMachineURI);
		
	}

}
