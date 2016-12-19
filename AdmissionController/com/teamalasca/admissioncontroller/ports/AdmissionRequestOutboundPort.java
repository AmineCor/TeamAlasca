package com.teamalasca.admissioncontroller.ports;


import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestHandlerI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestSubmitterI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class AdmissionRequestOutboundPort extends AbstractOutboundPort implements AdmissionRequestHandlerI {

	public AdmissionRequestOutboundPort(ComponentI owner) throws Exception {
		super(AdmissionRequestSubmitterI.class, owner);
	}
	public AdmissionRequestOutboundPort(String URI, ComponentI owner) throws Exception {
		super(URI,AdmissionRequestSubmitterI.class, owner);
	}
	@Override
	public void handleAdmissionRequest(AdmissionRequestI a) throws Exception {
		((AdmissionRequestSubmitterI)this.connector).submitAdmissionRequest(a) ;
	}

	@Override
	public void handleAdmissionRequestAndNotify(AdmissionRequestI a) throws Exception {
		((AdmissionRequestSubmitterI)this.connector).submitAdmissionRequestAndNotify(a) ;

	}

}
