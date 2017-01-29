package com.teamalasca.autonomiccontroller.ports;

import com.teamalasca.autonomiccontroller.interfaces.AutonomicControllerServicesI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

/**
 * The class <code>AutonomicControllerServicesOutboundPort</code> implements the
 * inbound port requiring the interface <code>AutonomicControllerServicesI</code>.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class AutonomicControllerServicesOutboundPort 
extends	AbstractOutboundPort
implements AutonomicControllerServicesI
{
	
	/**
	 * Construct an <code>AutonomicControllerServicesOutboundPort</code>.
	 * 
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public AutonomicControllerServicesOutboundPort(ComponentI owner) throws Exception
	{
		super(AutonomicControllerServicesI.class, owner);
	}

	/**
	 * Construct an <code>AutonomicControllerServicesOutboundPort</code>.
	 * 
 	 * @param uri the uri of the port.
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public AutonomicControllerServicesOutboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, AutonomicControllerServicesI.class, owner);
	}

	/**
	 * @see com.teamalasca.autonomiccontroller.interfaces.AutonomicControllerServicesI#doPeriodicAdaptation()
	 */
	@Override
	public void doPeriodicAdaptation() throws Exception
	{
		((AutonomicControllerServicesI) this.connector).doPeriodicAdaptation();
	}

}
