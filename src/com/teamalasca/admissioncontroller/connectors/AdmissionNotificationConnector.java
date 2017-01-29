package com.teamalasca.admissioncontroller.connectors;

import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionNotificationI;

import fr.upmc.components.connectors.AbstractConnector;

/**
 * The class <code>AdmissionNotificationConnector</code> implements 
 * a connector for ports exchanging through the interface 
 * <code>AdmissionNotificationI</code>.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class AdmissionNotificationConnector
extends AbstractConnector
implements AdmissionNotificationI
{

	/** 
	 * @see com.teamalasca.admissioncontroller.interfaces.AdmissionNotificationI#notifyAdmission(com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI)
	 */
	@Override
	public void notifyAdmission(AdmissionRequestI a) throws Exception
	{
		((AdmissionNotificationI) this.offering).notifyAdmission(a);
	}

}
