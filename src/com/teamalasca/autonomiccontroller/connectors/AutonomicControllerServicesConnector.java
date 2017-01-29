package com.teamalasca.autonomiccontroller.connectors;

import com.teamalasca.autonomiccontroller.interfaces.AutonomicControllerServicesI;

import fr.upmc.components.connectors.AbstractConnector;


public class AutonomicControllerServicesConnector
extends	AbstractConnector
implements AutonomicControllerServicesI
{

	@Override
	public void doPeriodicAdaptation() throws Exception
	{
		((AutonomicControllerServicesI) this.offering).doPeriodicAdaptation();
	}

}

