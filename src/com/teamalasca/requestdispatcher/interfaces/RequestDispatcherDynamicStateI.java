package com.teamalasca.requestdispatcher.interfaces;

import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataRequiredI;

/**
 * The interface <code>RequestDispatcherDynamicStateI</code> implements objects
 * representing the dynamic state information of request dipatchers transmitted
 * through the <code>RequestDispatcherDynamicStateI</code> interface of
 * <code>RequestDispatcher</code> components.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Cl�ment George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public interface RequestDispatcherDynamicStateI
extends DataOfferedI.DataI,
		DataRequiredI.DataI
{
	
	/**
	 * Get the request execution time average of the requests.
	 * 
	 * @return the request execution time average of the requests.
	 */
	public double getRequestExecutionTimeAverage();
	
}
