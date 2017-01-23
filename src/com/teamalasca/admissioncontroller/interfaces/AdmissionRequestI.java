package com.teamalasca.admissioncontroller.interfaces;

public interface AdmissionRequestI {


	public String getApplicationURI();
	public String getApplicationAdmissionNotificationInboundPortURI();

	public void acceptRequest();
	public void refuseRequest();
	public boolean isAccepted();

	public String getRequestSubmissionInboundPortURI();
	public String getRequestNotificationOutboundPortURI();

	public void setRequestSubmissionInboundPortURI(String requestSubmissionInboundPortURI);
	public void setRequestNotificationOutboundPortURI(String requestNotificationOutboundPortURI);

	
}
