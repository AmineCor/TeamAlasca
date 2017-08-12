package com.teamalasca.admissioncontroller.ports;

import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionNotificationHandlerI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionNotificationI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;


/**
 * The class <code>AdmissionNotificationOutboundPort</code> implements the
 * inbound port requiring the interface <code>AdmissionNotificationHandlerI</code>.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class AdmissionNotificationOutboundPort
extends AbstractOutboundPort
implements AdmissionNotificationHandlerI
{
	
	/**
	 * Construct an <code>AdmissionNotificationOutboundPort</code>.
	 * 
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public AdmissionNotificationOutboundPort(ComponentI owner) throws Exception
	{
		super(AdmissionNotificationI.class, owner);
	}

	/**
	 * Construct an <code>AdmissionNotificationOutboundPort</code>.
	 * 
 	 * @param uri the URI of the port.
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public AdmissionNotificationOutboundPort(String uri ,ComponentI owner) throws Exception
	{
		super(uri, AdmissionNotificationI.class, owner);
	}

	/**
	 * @see com.teamalasca.admissioncontroller.interfaces.AdmissionNotificationHandlerI#acceptAdmissionNotification(com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI)
	 */
	@Override
	public void acceptAdmissionNotification(AdmissionRequestI a) throws Exception
	{
		((AdmissionNotificationI) this.connector).notifyAdmission(a);
	}
	
}
