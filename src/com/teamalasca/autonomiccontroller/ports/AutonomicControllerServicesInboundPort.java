package com.teamalasca.autonomiccontroller.ports;

import com.teamalasca.autonomiccontroller.AutonomicController;
import com.teamalasca.autonomiccontroller.interfaces.AutonomicControllerServicesI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;


public class AutonomicControllerServicesInboundPort 
extends	AbstractInboundPort
implements AutonomicControllerServicesI
{
	
	private static final long serialVersionUID = 1L;

	public AutonomicControllerServicesInboundPort(ComponentI owner) throws Exception
	{
		super(AutonomicControllerServicesI.class, owner) ;

		assert owner instanceof AutonomicController;
	}

	public AutonomicControllerServicesInboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, AutonomicControllerServicesI.class, owner);

		assert owner instanceof AutonomicController;
	}

	@Override
	public void doPeriodicAdaptation() throws Exception
	{
		final AutonomicController ac = (AutonomicController) this.owner ;
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
