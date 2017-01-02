package com.alascateam.admission.interfaces;

import com.alascateam.admission.interfaces.VMRequestI;

import fr.upmc.datacenter.software.interfaces.RequestI;

public interface AjoutVMRequesthandlerI {
	public void receiveAddVMRequest(VMRequestI request) throws Exception;

	void acceptRequestTerminationNotification(RequestI r, int nbRequest)
			throws Exception;

	void startUnlimitedPushing(int interval, int n) throws Exception;


}
