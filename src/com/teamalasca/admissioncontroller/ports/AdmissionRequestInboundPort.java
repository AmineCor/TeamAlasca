package com.teamalasca.admissioncontroller.ports;

import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestHandlerI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestSubmitterI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;


public class AdmissionRequestInboundPort extends AbstractInboundPort implements AdmissionRequestSubmitterI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public AdmissionRequestInboundPort(ComponentI owner) throws Exception {
		super(AdmissionRequestSubmitterI.class, owner);
	}
	public AdmissionRequestInboundPort(String URI, ComponentI owner) throws Exception {
		super(URI,AdmissionRequestSubmitterI.class, owner);
	}
	@Override
	public void submitAdmissionRequest(final AdmissionRequestI a) throws Exception {
		final AdmissionRequestHandlerI ah =
				(AdmissionRequestHandlerI) this.owner ;
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						ah.handleAdmissionRequestAndNotify(a) ;
						return null ;
					}
				}) ;	
		}
	

	@Override
	public void submitAdmissionRequestAndNotify(final AdmissionRequestI a) throws Exception {
		final AdmissionRequestHandlerI ah =
				(AdmissionRequestHandlerI) this.owner ;
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						ah.handleAdmissionRequestAndNotify(a) ;
						return null ;
					}
				}) ;	}



}

