package com.teamalasca.admissioncontroller.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface AdmissionNotificationI extends OfferedI,RequiredI {
	public void notifyAdmission(AdmissionI a) throws Exception;

}
