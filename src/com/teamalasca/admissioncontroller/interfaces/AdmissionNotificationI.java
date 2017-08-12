package com.teamalasca.admissioncontroller.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * The interface <code>AdmissionNotificationI</code> defines the methods
 * that must be implemented by a component to notify an admission request.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public interface AdmissionNotificationI
extends OfferedI,
	    RequiredI
{
	/**
	 * Notification an admission request.
	 * 
	 * @param a the admission request.
	 * @throws Exception throws an exception if an error occured..
	 */
	public void notifyAdmission(AdmissionRequestI a) throws Exception;

}
