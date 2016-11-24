package com.teamalasca.admissioncontroller.interfaces;

public interface AdmissionI {
String getAppUri();
	
	boolean isAllowed();
	
	String getRequestSubmissionOutboundPort();
	
	String requestSubmissionInboundPortURIRep();
	
	String RequestSubmissionInboundPortVM();
	
	String AdmissionNotificationInboundPort();
	
	String RequestNotificationInboundPort();
	
	void setAllowed(boolean allowed);
	
	public void setRsibp_vm(String rsibp_vm);
	
	public void setRsibp(String rsibp);

}
