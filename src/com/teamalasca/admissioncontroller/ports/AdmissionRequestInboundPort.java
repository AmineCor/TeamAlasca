package com.teamalasca.admissioncontroller.ports;

import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestHandlerI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestSubmitterI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;


/**
 * The class <code>AdmissionRequestInboundPort</code> implements the
 * inbound port offering the interface <code>AdmissionRequestSubmitterI</code>.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class AdmissionRequestInboundPort
extends AbstractInboundPort
implements AdmissionRequestSubmitterI
{
	
	/**
	 * A unique serial version identifier.
	 * @see java.io.Serializable#serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construct an <code>AdmissionRequestInboundPort</code>.
	 * 
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public AdmissionRequestInboundPort(ComponentI owner) throws Exception
	{
		super(AdmissionRequestSubmitterI.class, owner);
	}
	
	/**
	 * Construct an <code>AdmissionRequestInboundPort</code>.
	 * 
 	 * @param uri the uri of the port.
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public AdmissionRequestInboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, AdmissionRequestSubmitterI.class, owner);
	}
	
	/**
	 * @see com.teamalasca.admissioncontroller.interfaces.AdmissionRequestSubmitterI#submitAdmissionRequest(com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI)
	 */
	@Override
	public void submitAdmissionRequest(final AdmissionRequestI a) throws Exception
	{
		final AdmissionRequestHandlerI ah = (AdmissionRequestHandlerI) this.owner;
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						ah.handleAdmissionRequestAndNotify(a);
						return null;
					}
				});	
	}
	
	/**
	 * @see com.teamalasca.admissioncontroller.interfaces.AdmissionRequestSubmitterI#submitAdmissionRequestAndNotify(com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI)
	 */
	@Override
	public void submitAdmissionRequestAndNotify(final AdmissionRequestI a) throws Exception
	{
		final AdmissionRequestHandlerI ah = (AdmissionRequestHandlerI) this.owner;
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						ah.handleAdmissionRequestAndNotify(a);
						return null;
					}
				}) ;	
	}

}
