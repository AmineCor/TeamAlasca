package com.teamalasca.autonomiccontroller.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * The interface <code>AutonomicControllerServicesI</code> defines the services offered by
 * <code>AutonomicController</code> components.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public interface AutonomicControllerServicesI 
extends	OfferedI,
		RequiredI
{
	
	/**
	 * Do a periodic adaptation.
	 * 
	 * @throws Exception throws an exception if an error occured..
	 */
	public void doPeriodicAdaptation() throws Exception;

}
