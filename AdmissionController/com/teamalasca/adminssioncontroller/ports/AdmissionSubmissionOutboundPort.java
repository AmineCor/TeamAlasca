package com.teamalasca.adminssioncontroller.ports;


import com.teamalasca.admissioncontroller.interfaces.AdmissionI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionSubmissionHandlerI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionSubmissionI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class AdmissionSubmissionOutboundPort extends AbstractOutboundPort implements AdmissionSubmissionHandlerI {

    public AdmissionSubmissionOutboundPort(ComponentI owner) throws Exception {
		super(AdmissionSubmissionI.class, owner);
		// TODO Auto-generated constructor stub
	}
	public AdmissionSubmissionOutboundPort(String URI, ComponentI owner) throws Exception {
		super(URI,AdmissionSubmissionI.class, owner);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void handleAdmissionSubmission(AdmissionI a) throws Exception {
		// TODO Auto-generated method stub
		((AdmissionSubmissionI)this.connector).submitAdmissionRequest(a) ;
	}

	@Override
	public void handleAdmissionSubmissionAndNotify(AdmissionI a) throws Exception {
		// TODO Auto-generated method stub
		((AdmissionSubmissionI)this.connector).submitAdmissionRequestAndNotify(a) ;

	}

}
