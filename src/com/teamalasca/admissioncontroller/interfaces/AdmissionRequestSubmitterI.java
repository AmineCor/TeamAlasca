package com.teamalasca.admissioncontroller.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;


/**
 * The interface <code>AdmissionRequestSubmitterI</code> defines the methods
 * that must be implemented by a component to submit an admission request.
 * 
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public interface AdmissionRequestSubmitterI
extends OfferedI,
	    RequiredI
{
	
	/**
	 * Submit an admission request.
	 * 
	 * @param a the admission request.
	 * @throws Exception throws an exception if an error occured..
	 */
	public void submitAdmissionRequest(AdmissionRequestI a) throws Exception;
	
	/**
	 * Submit an admission request and notify.
	 * 
	 * @param a the admission request.
	 * @throws Exception throws an exception if an error occured..
	 */
	public void submitAdmissionRequestAndNotify(AdmissionRequestI a) throws Exception;
	
}
