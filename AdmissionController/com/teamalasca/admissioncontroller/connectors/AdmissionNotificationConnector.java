package com.teamalasca.admissioncontroller.connectors;

import com.teamalasca.admissioncontroller.interfaces.AdmissionI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionNotificationI;

import fr.upmc.components.connectors.AbstractConnector;

public class AdmissionNotificationConnector extends AbstractConnector implements AdmissionNotificationI {

	@Override
	public void notifyAdmission(AdmissionI a) throws Exception {
		// TODO Auto-generated method stub
		
		((AdmissionNotificationI)this.offering).notifyAdmission(a);
	}
	
	
	

}
