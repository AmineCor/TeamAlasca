package com.teamalasca.admissioncontroller.requests;

import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI;


public final class AdmissionRequest
implements AdmissionRequestI
{

	boolean allowed;
	private final String applicationURI;
	private String rsip;
	private String rnop;
	private String anip;
	
	public AdmissionRequest(String applicationURI, String applicationAdmissionNotificationInboundPortURI)
	{
		super();
		
		this.allowed = false;
		this.applicationURI = applicationURI;
		this.anip = applicationAdmissionNotificationInboundPortURI;
	}

	@Override
	public String getApplicationURI()
	{
		return applicationURI;
	}

	@Override
	public boolean isAccepted()
	{
		return allowed;
	}

	@Override
	public String getRequestSubmissionInboundPortURI()
	{
		return rsip;
	}

	@Override
	public void acceptRequest()
	{
		this.allowed = true;
	}

	@Override
	public void refuseRequest()
	{
		this.allowed = false;
	}

	@Override
	public String getApplicationAdmissionNotificationInboundPortURI()
	{
		return this.anip;
	}

	@Override
	public String getRequestNotificationOutboundPortURI()
	{
		return this.rnop;
	}

	@Override
	public void setRequestSubmissionInboundPortURI(String requestSubmissionInboundPortURI)
	{
		this.rsip = requestSubmissionInboundPortURI;
	}

	@Override
	public void setRequestNotificationOutboundPortURI(String requestNotificationOutboundPortURI)
	{
		this.rnop = requestNotificationOutboundPortURI;
	}

}
