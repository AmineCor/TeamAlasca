package com.teamalasca.admissioncontroller.interfaces;

/**
 * The interface <code>AdmissionRequestI</code> defines the services
 * offered by <code>AdmissionRequest</code>.
 * 
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public interface AdmissionRequestI
{

	/**
	 * Get the application URI.
	 * 
	 * @return the application URI.
	 */
	public String getApplicationURI();
	
	/**
	 * Get the application admission notification inbound port URI.
	 * 
	 * @return the application admission notification inbound port URI.
	 */
	public String getApplicationAdmissionNotificationInboundPortURI();

	/**
	 * Accept an admission request.
	 */
	public void acceptRequest();
	
	/**
	 * Refuse an admission request.
	 */
	public void refuseRequest();
	
	/**
	 * Check is an admission request is accepted.
	 * 
	 * @return true if the admission request if accepted.
	 */
	public boolean isAccepted();

	/**
	 * Get the request submission inbound port URI.
	 * 
	 * @return the request submission inbound port URI.
	 */
	public String getRequestSubmissionInboundPortURI();
	
	/**
	 * Get the request notification inbound port URI.
	 * 
	 * @return the request notification inbound port URI.
	 */
	public String getRequestNotificationOutboundPortURI();

	/**
	 * Set the request submission inbound URI to set.
	 * 
	 * @param requestSubmissionInboundPortURI the request submission inbound URI to set.
	 */
	public void setRequestSubmissionInboundPortURI(String requestSubmissionInboundPortURI);
	
	/**
	 * Set the request notification outbound URI to set.
	 * 
	 * @param requestNotificationOutboundPortURI the request notification outbound URI to set.
	 */
	public void setRequestNotificationOutboundPortURI(String requestNotificationOutboundPortURI);
	
}
