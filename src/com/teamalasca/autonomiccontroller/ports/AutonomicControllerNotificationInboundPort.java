package com.teamalasca.autonomiccontroller.ports;

import com.teamalasca.autonomiccontroller.AutonomicController;
import com.teamalasca.autonomiccontroller.interfaces.AutonomicControllerNotificationI;
import com.teamalasca.autonomiccontroller.interfaces.AutonomicControllerServicesI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

public class AutonomicControllerNotificationInboundPort extends AbstractInboundPort implements AutonomicControllerNotificationI{
	
	/**
	 * A unique serial version identifier.
	 * @see java.io.Serializable#serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construct an <code>AutonomicControllerNotificationInboundPort</code>.
	 * 
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public AutonomicControllerNotificationInboundPort(ComponentI owner) throws Exception
	{
		super(AutonomicControllerServicesI.class, owner);

		assert owner instanceof AutonomicController;
	}

	/**
	 * Construct an <code>AutonomicControllerNotificationInboundPort</code>.
	 * 
 	 * @param uri the uri of the port.
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public AutonomicControllerNotificationInboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, AutonomicControllerServicesI.class, owner);

		assert owner instanceof AutonomicController;
	}

	@Override
	public void notifyVirtualMachineUsageTermination(final String aVirtualMachineURI) throws Exception {
		final AutonomicController ac = (AutonomicController) this.owner;
		this.owner.handleRequestAsync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							ac.notifyVirtualMachineUsageTermination(aVirtualMachineURI);
							return null;
						}
					});	
		
	}

}
