package com.teamalasca.adminssioncontroller.ports;

import com.teamalasca.admissioncontroller.interfaces.AdmissionI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionNotificationHandlerI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionNotificationI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class AdmissionNotificationOutboundPort extends AbstractOutboundPort implements AdmissionNotificationHandlerI {

	public AdmissionNotificationOutboundPort(ComponentI owner) throws Exception
	{
		super(AdmissionNotificationI.class, owner);
	}

	public AdmissionNotificationOutboundPort(String uri,ComponentI owner) throws Exception
	{
		super(uri, AdmissionNotificationI.class, owner) ;
	}

	@Override
	public void acceptAdmissionNotification(AdmissionI a) throws Exception {
		((AdmissionNotificationI)this.connector).notifyAdmission(a) ;
	}
}

