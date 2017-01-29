package com.teamalasca.admissioncontroller.connectors;

import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionNotificationI;

import fr.upmc.components.connectors.AbstractConnector;


public class AdmissionNotificationConnector extends AbstractConnector implements AdmissionNotificationI
{

	@Override
	public void notifyAdmission(AdmissionRequestI a) throws Exception
	{
		((AdmissionNotificationI) this.offering).notifyAdmission(a);
	}

}
