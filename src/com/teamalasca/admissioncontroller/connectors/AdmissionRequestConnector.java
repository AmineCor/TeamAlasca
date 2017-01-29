package com.teamalasca.admissioncontroller.connectors;

import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI;
import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestSubmitterI;

import fr.upmc.components.connectors.AbstractConnector;


/**
 * The class <code>AdmissionRequestConnector</code> implements 
 * a connector for ports exchanging through the interface 
 * <code>AdmissionRequestSubmitterI</code>.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class AdmissionRequestConnector
extends AbstractConnector
implements AdmissionRequestSubmitterI
{

	/** 
	 * @see com.teamalasca.admissioncontroller.interfaces.AdmissionRequestSubmitterI#submitAdmissionRequest(com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI)
	 */
	@Override
	public void submitAdmissionRequest(AdmissionRequestI a) throws Exception
	{
		((AdmissionRequestSubmitterI) this.offering).submitAdmissionRequest(a);
	}

	/** 
	 * @see com.teamalasca.admissioncontroller.interfaces.AdmissionRequestSubmitterI#submitAdmissionRequestAndNotify(com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI)
	 */
	@Override
	public void submitAdmissionRequestAndNotify(AdmissionRequestI a) throws Exception
	{
		((AdmissionRequestSubmitterI) this.offering).submitAdmissionRequestAndNotify(a);
	}

}
