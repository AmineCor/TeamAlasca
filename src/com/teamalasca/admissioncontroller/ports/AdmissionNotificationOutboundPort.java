package com.teamalasca.admissioncontroller.ports;

import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI;
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
	public void acceptAdmissionNotification(AdmissionRequestI a) throws Exception {
		((AdmissionNotificationI)this.connector).notifyAdmission(a) ;
	}
}

