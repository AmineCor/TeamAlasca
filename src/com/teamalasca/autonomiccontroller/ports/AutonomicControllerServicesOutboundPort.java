package com.teamalasca.autonomiccontroller.ports;

import com.teamalasca.autonomiccontroller.interfaces.AutonomicControllerServicesI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class AutonomicControllerServicesOutboundPort 
extends	AbstractOutboundPort
implements AutonomicControllerServicesI
{
	
	public AutonomicControllerServicesOutboundPort(ComponentI owner) throws Exception
	{
		super(AutonomicControllerServicesI.class, owner) ;
	}

	public AutonomicControllerServicesOutboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, AutonomicControllerServicesI.class, owner);
	}


	@Override
	public void doPeriodicAdaptation() throws Exception
	{
		((AutonomicControllerServicesI) this.connector).doPeriodicAdaptation();
	}

}
