package com.teamalasca.autonomiccontroller.connectors;

import com.teamalasca.autonomiccontroller.interfaces.AutonomicControllerNotificationI;

import fr.upmc.components.connectors.AbstractConnector;

/**
 * The class <code>AutonomicControllerNotificationConnector</code> implements 
 * a connector for ports exchanging through the interface 
 * <code>AutonomicControllerServicesI</code>.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Cl�ment George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class AutonomicControllerNotificationConnector
extends	AbstractConnector
implements AutonomicControllerNotificationI
{

	@Override
	public void notifyVirtualMachineUsageTermination(String aVirtualMachineURI)
			throws Exception {
		((AutonomicControllerNotificationI) this.offering).notifyVirtualMachineUsageTermination(aVirtualMachineURI);
		
	}

}

