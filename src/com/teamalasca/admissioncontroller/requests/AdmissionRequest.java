package com.teamalasca.admissioncontroller.requests;

import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI;

/**
 * The class <code>AdmissionRequest</code> implements 
 * a component that represents an admission request.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public final class AdmissionRequest
implements AdmissionRequestI
{

	/** Flag to know if the admission request is allowed. */
	private boolean allowed;
	
	/** Application URI. */
	private final String applicationURI;
	
	/** Request submission inbound port URI. */
	private String rsip;
	
	/** Request notification outbound port URI. */
	private String rnop;
	
	/** Application admission notification inbound port URI. */
	private String anip;
	
	/**
	 * Construct an <code>AdmissionRequest</code>.
	 * 
	 * @param applicationURI the application URI.
	 * @param applicationAdmissionNotificationInboundPortURI the application admission notification inboundPort URI.
	 */
	public AdmissionRequest(String applicationURI, String applicationAdmissionNotificationInboundPortURI)
	{
		super();
		
		this.allowed = false;
		this.applicationURI = applicationURI;
		this.anip = applicationAdmissionNotificationInboundPortURI;
	}

	/** 
	 * @see com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI#getApplicationURI()
	 */
	@Override
	public String getApplicationURI()
	{
		return applicationURI;
	}

	/** 
	 * @see com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI#getApplicationURI()
	 */
	@Override
	public boolean isAccepted()
	{
		return allowed;
	}

	/** 
	 * @see com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI#getRequestSubmissionInboundPortURI()
	 */
	@Override
	public String getRequestSubmissionInboundPortURI()
	{
		return rsip;
	}

	/** 
	 * @see com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI#acceptRequest()
	 */
	@Override
	public void acceptRequest()
	{
		this.allowed = true;
	}

	/** 
	 * @see com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI#refuseRequest()
	 */
	@Override
	public void refuseRequest()
	{
		this.allowed = false;
	}

	/** 
	 * @see com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI#getApplicationAdmissionNotificationInboundPortURI()
	 */
	@Override
	public String getApplicationAdmissionNotificationInboundPortURI()
	{
		return this.anip;
	}

	/** 
	 * @see com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI#getRequestNotificationOutboundPortURI()
	 */
	@Override
	public String getRequestNotificationOutboundPortURI()
	{
		return this.rnop;
	}

	/** 
	 * @see com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI#setRequestSubmissionInboundPortURI(java.lang.String)
	 */
	@Override
	public void setRequestSubmissionInboundPortURI(String requestSubmissionInboundPortURI)
	{
		this.rsip = requestSubmissionInboundPortURI;
	}

	/** 
	 * @see com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI#setRequestNotificationOutboundPortURI(java.lang.String)
	 */
	@Override
	public void setRequestNotificationOutboundPortURI(String requestNotificationOutboundPortURI)
	{
		this.rnop = requestNotificationOutboundPortURI;
	}

}
