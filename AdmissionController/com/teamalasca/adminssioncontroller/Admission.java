package com.teamalasca.adminssioncontroller;

import com.teamalasca.admissioncontroller.interfaces.AdmissionI;
/**
 * 
 * 
 *
 */

public class Admission implements AdmissionI{

	String app_URI;
	String rsibp;
	String anibp;
	String rnibp;
	String rsibp_vm;
	boolean allowed;
	
	public String RequestSubmissionInboundPortVM() {
		return rsibp_vm;
	}

	public void setRsibp_vm(String rsibp_vm) {
		this.rsibp_vm = rsibp_vm;
	}


	
	@Override
	public String getAppUri() {
		// TODO Auto-generated method stub
		return app_URI;
	}

	@Override
	public boolean isAllowed() {
		// TODO Auto-generated method stub
		return allowed;
	}

	public void setAllowed(boolean allowed) {
		// TODO Auto-generated method stub
		this.allowed=allowed;
	}
	public String AdmissionNotificationInboundPort() {
		return anibp;
	}

	public void setAnibp(String anibp) {
		this.anibp = anibp;
	}

	public String RequestNotificationInboundPort() {
		return rnibp;
	}

	public void setRnibp(String rnibp) {
		this.rnibp = rnibp;
	}

	public Admission(String app_URI, String rnibp, String anibp) {
		super();
		this.app_URI = app_URI;
		this.rnibp = rnibp;
		this.anibp = anibp;
		this.allowed = false;
	}

	

	@Override
	public String requestSubmissionInboundPortURIRep() {
		// TODO Auto-generated method stub
		return rsibp;
	}
public void setRsibp(String rsibp)
{
	this.rsibp=rsibp;
}

@Override
public String getRequestSubmissionOutboundPort() {
	// TODO Auto-generated method stub
	return null;
}
	
}
