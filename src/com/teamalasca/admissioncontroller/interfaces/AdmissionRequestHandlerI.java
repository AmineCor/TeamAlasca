package com.teamalasca.admissioncontroller.interfaces;

import com.teamalasca.admissioncontroller.interfaces.AdmissionRequestI;

/**
 * The interface <code>AdmissionRequestHandlerI</code> defines the methods
 * that must be implemented by a component to handle an admission request.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public interface AdmissionRequestHandlerI
{
	
	/**
	 * Handle an admission request.
	 * 
	 * @param a the admission request.
	 * @throws Exception throws an exception if an error occured..
	 */
    public void handleAdmissionRequest(AdmissionRequestI a) throws Exception;
    
    /**
	 * Handle an admission request and notify.
     * 
	 * @param a the admission request.
	 * @throws Exception throws an exception if an error occured..
     */
	public void handleAdmissionRequestAndNotify(AdmissionRequestI a) throws Exception;
	
}
