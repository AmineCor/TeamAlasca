package com.teamalasca.requestdispatcher;

import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;

public class RequestDispatcherDynamicState
implements RequestDispatcherDynamicStateI
{

	private static final long serialVersionUID = 1L;

	/** URI of the request dispatcher to which this dynamic state relates.			*/
	protected final String requestDispatcherURI ;
	
	/** Average */
	protected final double average;
	
	public RequestDispatcherDynamicState(String requestDispatcherURI, double average) throws Exception
	{
		super() ;

		this.requestDispatcherURI = requestDispatcherURI ;
		this.average = average;
	}
	
	@Override
	public double getExecutionTimeAvg()
	{
		return average;
	}

	public String getRequestDispatcherURI()
	{
		return requestDispatcherURI;
	}

}
