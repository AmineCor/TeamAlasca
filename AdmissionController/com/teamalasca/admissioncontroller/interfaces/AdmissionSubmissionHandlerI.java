package com.teamalasca.admissioncontroller.interfaces;

import com.teamalasca.admissioncontroller.interfaces.AdmissionI;

public interface AdmissionSubmissionHandlerI {
	
    public void handleAdmissionSubmission(AdmissionI a) throws Exception;
	
	public void handleAdmissionSubmissionAndNotify(AdmissionI a) throws Exception;
}
