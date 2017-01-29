package com.teamalasca.admissioncontroller.interfaces;

/**
 * The interface <code>AdmissionNotificationHandlerI</code> defines the methods
 * that must be implemented by a component to handle admission request notifications
 * received through an inboud port <code>AdmissionNotificationInboundPort</code>.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public interface AdmissionNotificationHandlerI
{
	
	/**
	 * Process the termination notification of an admission request.
	 *
	 * @param a the admission request.
	 * @throws Exception throws an exception if an error occured..
	 */
	public void acceptAdmissionNotification(AdmissionRequestI a) throws Exception;

}
