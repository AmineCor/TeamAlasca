package com.teamalasca.admissioncontroller.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;


public interface AdmissionRequestSubmitterI
extends OfferedI,
	    RequiredI
{
	
	public void submitAdmissionRequest(AdmissionRequestI a) throws Exception;
	public void submitAdmissionRequestAndNotify(AdmissionRequestI a) throws Exception;
	
}
