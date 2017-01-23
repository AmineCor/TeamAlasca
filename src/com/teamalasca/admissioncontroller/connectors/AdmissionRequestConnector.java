package com.teamalasca.admissioncontroller.connectors;

import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestSubmitterI;

import fr.upmc.components.connectors.AbstractConnector;

public class AdmissionRequestConnector extends AbstractConnector implements AdmissionRequestSubmitterI {

	@Override
	public void submitAdmissionRequest(AdmissionRequestI a) throws Exception {
		((AdmissionRequestSubmitterI)this.offering).submitAdmissionRequest(a);
		
	}

	@Override
	public void submitAdmissionRequestAndNotify(AdmissionRequestI a) throws Exception {
		((AdmissionRequestSubmitterI)this.offering).submitAdmissionRequestAndNotify(a);
		
	}

}
