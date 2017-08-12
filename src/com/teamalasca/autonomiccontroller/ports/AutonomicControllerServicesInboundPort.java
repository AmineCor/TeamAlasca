package com.teamalasca.autonomiccontroller.ports;

import com.teamalasca.autonomiccontroller.AutonomicController;
import com.teamalasca.autonomiccontroller.interfaces.AutonomicControllerServicesI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * The class <code>AutonomicControllerServicesInboundPort</code> implements the
 * inbound port offering the interface <code>AutonomicControllerServicesI</code>.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class AutonomicControllerServicesInboundPort 
extends	AbstractInboundPort
implements AutonomicControllerServicesI
{
	
	/**
	 * A unique serial version identifier.
	 * @see java.io.Serializable#serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construct an <code>AutonomicControllerServicesInboundPort</code>.
	 * 
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public AutonomicControllerServicesInboundPort(ComponentI owner) throws Exception
	{
		super(AutonomicControllerServicesI.class, owner);

		assert owner instanceof AutonomicController;
	}

	/**
	 * Construct an <code>AutonomicControllerServicesInboundPort</code>.
	 * 
 	 * @param uri the uri of the port.
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public AutonomicControllerServicesInboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, AutonomicControllerServicesI.class, owner);

		assert owner instanceof AutonomicController;
	}

	/**
	 * @see com.teamalasca.autonomiccontroller.interfaces.AutonomicControllerServicesI#doPeriodicAdaptation()
	 */
	@Override
	public void doPeriodicAdaptation() throws Exception
	{
		final AutonomicController ac = (AutonomicController) this.owner;
		this.owner.handleRequestAsync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							ac.doPeriodicAdaptation();
							return null;
						}
					});		
	}
	
}
