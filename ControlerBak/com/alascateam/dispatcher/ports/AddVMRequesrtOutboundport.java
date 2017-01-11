package com.alascateam.dispatcher.ports;


import com.alascateam.admission.interfaces.AdmissionNotificationI;
import com.alascateam.admission.interfaces.AjoutVMRequestI;
import com.alascateam.admission.interfaces.VMRequestI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class AddVMRequesrtOutboundport extends AbstractOutboundPort implements AjoutVMRequestI {

	public AddVMRequesrtOutboundport  (ComponentI owner) throws Exception {
		super(AdmissionNotificationI.class, owner);
	}

	public AddVMRequesrtOutboundport  (String uri, ComponentI owner) throws Exception
	{
		super(uri, AdmissionNotificationI.class,owner);
	}
	
	@Override
	public void sendAddVMRequest(VMRequestI request) throws Exception {
		((AjoutVMRequestI)this.connector).sendAddVMRequest(request) ;
	}
}
