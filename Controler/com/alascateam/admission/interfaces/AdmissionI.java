package com.alascateam.admission.interfaces;

import java.io.Serializable;

public interface AdmissionI extends Serializable {
	
	/** 
	 * Return the Application URI and  get the URI of the application
	 */
	
	String getAppUri();
	
	/**
	 * Return a boolean true if the application is allowed to be executed and false if not
	 */
	boolean isAllowed();
	
	/**
	 * Allow if the application can be executed 
	 */
	void setAllowed(boolean allowed);
		
	/**
	 * Get the RequestSubmissionInboundPort URI of the RequestDispatcher component and return an URI that is the URI of
	 * the request dispatcher
	 */
	public String getURIRequestSubmissionInboundPortDispatcher();
	
	/**
	 * Get the RequestSubmissionInboundPort URI of the VM application component
	 */
	public String getURIRequestSubmissionInboundPortVM();
	
	/**
	 * this method get the AdmissionNotificationInboundPort URI of the Application component and return an URI
	 */
	public String getURIAdmissionNotificationInboundPortApplication();
	
	/**
	 * this method get the RequestSubmissionInboundPort URI of the Request dispatcher component and return an URI
	 */
	public String getUriRequestNotificationInboundPortApplication();
	
	/**
	 * this method set the RequestSubmissionInboundPort URI of the VM of the application component
	 */
	public void setURIRequestSubmissionInboundPortVM(String rsibp);
	
	/**
	 * This method set the RequestSubmissionInboundPort URI of the ApplicationVM component
	 */
	public void setURIRequestSubmissionInboundPortDispatcher(String rsibp);
	
	
}
