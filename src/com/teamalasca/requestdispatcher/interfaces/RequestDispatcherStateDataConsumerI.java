package com.teamalasca.requestdispatcher.interfaces;

/**
 * The interface <code>RequestDispatcherStateDataConsumerI</code> defines the consumer
 * side methods used to receive dynamic state data pushed by a request dispatcher.q
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Cl�ment George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public interface RequestDispatcherStateDataConsumerI
{
	
	/**
	 * 
	 * 
	 * @param dispatcherURI the request dispatcher URI.
	 * @param currentDynamicState the current dynmaic state.
	 * @throws Exception throws an exception if an error occured..
	 */
	public void	acceptRequestDispatcherDynamicData(
			String dispatcherURI,
			RequestDispatcherDynamicStateI currentDynamicState)
					throws Exception;

}
