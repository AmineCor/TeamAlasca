package com.teamalasca.admissioncontroller.ports;

import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionNotificationHandlerI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionNotificationI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * The class <code>AdmissionNotificationInboundPort</code> implements the
 * inbound port offering the interface <code>AdmissionNotificationI</code>.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class AdmissionNotificationInboundPort
extends AbstractInboundPort
implements AdmissionNotificationI
{

	/**
	 * A unique serial version identifier.
	 * @see java.io.Serializable#serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Construct an <code>AdmissionNotificationInboundPort</code>.
	 * 
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public AdmissionNotificationInboundPort(ComponentI owner) throws Exception
	{
		super(AdmissionNotificationI.class, owner);
	}
	
	/**
	 * Construct an <code>AdmissionNotificationInboundPort</code>.
	 * 
 	 * @param uri the uri of the port.
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public AdmissionNotificationInboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, AdmissionNotificationI.class, owner);
	}
	
	/**
	 * @see com.teamalasca.admissioncontroller.interfaces.AdmissionNotificationI#notifyAdmission(com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI)
	 */
	@Override
	public void notifyAdmission(final AdmissionRequestI a) throws Exception
	{
		final AdmissionNotificationHandlerI anh = (AdmissionNotificationHandlerI) this.owner;
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						anh.acceptAdmissionNotification(a);
						return null;
					}
				});
	}

}
