package com.alascateam.admission.interfaces;

import java.io.Serializable;



/**
 * The interface must be implemented for objects which are sent
 * with the VMRequest mechanism.
 * See {@link AddVMRequestI} and {@link AddVMRequestHandlerI}
 *  
 */
public interface VMRequestI extends Serializable{

	/**
	 * Get the ApplicationVM URI associated to the request
	 * @return an URI
	 */
	public String getVMUri();
	
	/**
	 * Get the Application URI associated to the request
	 * @return an URI
	 */
	public String getAppUri();
	
	/**
	 * Set the ApplicationVM URI associated to the request
	 * @param VMUri
	 */
	public void setVMUri(String VMUri);
	
	/**
	 * Set the Application URI associated to the request
	 * @param appUri
	 */
	public void setAppUri(String appUri);
	
}
