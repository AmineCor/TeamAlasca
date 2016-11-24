package com.teamalasca.admissioncontroller.connectors;

import com.teamalasca.admissioncontroller.interfaces.AdmissionI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionSubmissionI;

import fr.upmc.components.connectors.AbstractConnector;

public class AdmissionSubmissionConnector extends AbstractConnector implements AdmissionSubmissionI {

	@Override
	public void submitAdmissionRequest(AdmissionI a) throws Exception {
		// TODO Auto-generated method stub
		((AdmissionSubmissionI)this.offering).submitAdmissionRequest(a);
		
	}

	@Override
	public void submitAdmissionRequestAndNotify(AdmissionI a) throws Exception {
		// TODO Auto-generated method stub
		((AdmissionSubmissionI)this.offering).submitAdmissionRequestAndNotify(a);
		
	}

}
