package com.teamalasca.autonomiccontroller.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;


public interface AutonomicControllerServicesI 
extends	OfferedI,
		RequiredI
{
	
	public void doPeriodicAdaptation() throws Exception;

}
