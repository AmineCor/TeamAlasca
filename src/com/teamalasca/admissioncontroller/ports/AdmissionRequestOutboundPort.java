package com.teamalasca.admissioncontroller.ports;

import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestHandlerI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestSubmitterI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

/**
 * The class <code>AdmissionRequestOutboundPort</code> implements the
 * inbound port requiring the interface <code>AdmissionRequestHandlerI</code>.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class AdmissionRequestOutboundPort
extends AbstractOutboundPort
implements AdmissionRequestHandlerI
{

	/**
	 * Construct an <code>AdmissionRequestOutboundPort</code>.
	 * 
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public AdmissionRequestOutboundPort(ComponentI owner) throws Exception
	{
		super(AdmissionRequestSubmitterI.class, owner);
	}
	
	
	/**
	 * Construct an <code>AdmissionRequestOutboundPort</code>.
	 * 
 	 * @param uri the uri of the port.
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public AdmissionRequestOutboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, AdmissionRequestSubmitterI.class, owner);
	}
	
	@Override
	public void handleAdmissionRequest(AdmissionRequestI a) throws Exception
	{
		((AdmissionRequestSubmitterI) this.connector).submitAdmissionRequest(a);
	}

	/**
	 * @see com.teamalasca.admissioncontroller.interfaces.AdmissionRequestHandlerI#handleAdmissionRequestAndNotify(com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI)
	 */
	@Override
	public void handleAdmissionRequestAndNotify(AdmissionRequestI a) throws Exception
	{
		((AdmissionRequestSubmitterI) this.connector).submitAdmissionRequestAndNotify(a);
	}
	
}
