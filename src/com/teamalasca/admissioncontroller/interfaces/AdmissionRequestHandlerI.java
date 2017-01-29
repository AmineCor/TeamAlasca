package com.teamalasca.admissioncontroller.interfaces;

import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI;


public interface AdmissionRequestHandlerI
{
	
    public void handleAdmissionRequest(AdmissionRequestI a) throws Exception;
	public void handleAdmissionRequestAndNotify(AdmissionRequestI a) throws Exception;
	
}
