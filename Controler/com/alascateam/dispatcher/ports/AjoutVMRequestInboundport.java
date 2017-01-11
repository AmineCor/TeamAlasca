package com.alascateam.dispatcher.ports;


import com.alascateam.admission.interfaces.AdmissionNotificationI;
import com.alascateam.admission.interfaces.AjoutVMRequestI;
import com.alascateam.admission.interfaces.AddVMRequestHandlerI;
import com.alascateam.admission.interfaces.VMRequestI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * AjoutVMRequestInboundport : This port is used to receive VMRequest 
 *
 */
public class AjoutVMRequestInboundport extends AbstractInboundPort implements AjoutVMRequestI {


	private static final long serialVersionUID = 1L;

	public AjoutVMRequestInboundport(Class<?> implementedInterface,
			ComponentI owner) throws Exception {
		super(AdmissionNotificationI.class, owner);
		// TODO Auto-generated constructor stub
	}
	public AjoutVMRequestInboundport (String uri, ComponentI owner) throws Exception
	{
		super(uri, AdmissionNotificationI.class,owner);
	}

	@Override
	public void sendAddVMRequest(final VMRequestI request) throws Exception {
		
		
		final AddVMRequestHandlerI handler =
				(AddVMRequestHandlerI) this.owner ;
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						handler.receiveAddVMRequest(request) ;
						return null;
					}
				}) ;
		
		
		
	}

}
