package com.teamalasca.autonomiccontroller.connectors;

import com.teamalasca.autonomiccontroller.interfaces.AutonomicControllerServicesI;

import fr.upmc.components.connectors.AbstractConnector;

/**
 * The class <code>AutonomicControllerServicesConnector</code> implements 
 * a connector for ports exchanging through the interface 
 * <code>AutonomicControllerServicesI</code>.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class AutonomicControllerServicesConnector
extends	AbstractConnector
implements AutonomicControllerServicesI
{

	/**
	 * @see com.teamalasca.autonomiccontroller.interfaces.AutonomicControllerServicesI#doPeriodicAdaptation()
	 */
	@Override
	public void doPeriodicAdaptation() throws Exception
	{
		((AutonomicControllerServicesI) this.offering).doPeriodicAdaptation();
	}

}

