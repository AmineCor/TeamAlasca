package com.teamalasca.requestdispatcher;

import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;

/**
 * The class <code>RequestDispatcherDynamicState</code> implements objects representing
 * a snapshot of the dynamic state of a request dispatcher component to be pulled or
 * pushed through the dynamic state data interface.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class RequestDispatcherDynamicState
implements RequestDispatcherDynamicStateI
{
	
	/**
	 * A unique serial version identifier.
	 * @see java.io.Serializable#serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** URI of the request dispatcher to which this dynamic state relates. */
	protected final String requestDispatcherURI;
	
	/** Average */
	protected final double average;
	
	/**
	 * Construct a <code>RequestDispatcherDynamicState</code>.
	 * 
	 * @param requestDispatcherURI the request dispatcher URI.
	 * @param average the average.
	 * @throws Exception throws an exception if an error occured..
	 */
	public RequestDispatcherDynamicState(String requestDispatcherURI, double average) throws Exception
	{
		super();

		this.requestDispatcherURI = requestDispatcherURI;
		this.average = average;
	}
	
	/**
	 * @see com.teamalasca.requestdispatcher.interfaces.RequestDispatcherDynamicStateI#getRequestExecutionTimeAverage()
	 */
	@Override
	public double getRequestExecutionTimeAverage()
	{
		return average;
	}

	/**
	 * Get the request dispatcher URI.
	 * 
	 * @return the request dispatcher URI.
	 */
	public String getRequestDispatcherURI()
	{
		return requestDispatcherURI;
	}

}
