package com.teamalasca;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherStateDataConsumerI;

import fr.upmc.components.AbstractComponent;

public class AutonomicController
extends AbstractComponent
implements RequestDispatcherStateDataConsumerI
{
	
	private String acURI;
	private String acServicesInboundPortURI;
	private List<Double> avgList;
	private final static int LIMIT_SIZE_AVG_LIST = 3;
	private ScheduledFuture<?> pushingFuture;
	
	
	
	

	public AutonomicController(String acURI, String acServicesInboundPortURI) throws Exception
	{
		super(1, 1);
		
		// Preconditions
		assert acURI != null;
		assert acServicesInboundPortURI != null;
		
		this.acURI = acURI;
		this.acServicesInboundPortURI = acServicesInboundPortURI;
		this.avgList = new ArrayList<>();
	}


	@Override
	public void acceptRequestDispatcherDynamicData(String dispatcherURI,
			RequestDispatcherDynamicStateI currentDynamicState)
			throws Exception
	{
		synchronized (this){
			this.avgList.add(currentDynamicState.getExecutionTimeAvg());
			
			if (avgList.size() > LIMIT_SIZE_AVG_LIST) {
				avgList.remove(0);
			}	
		}
	}
	
	public Double computeMovingAverage()
	{
		// Need to check if we at least got an average
		if (avgList.isEmpty()) {
			return null;
		}
		
		double result = 0;
		for (double i : avgList)
		{
			result += i;
		}
		
		result = result / avgList.size();
		logMessage("AutonomicControler : Moving average = " + result);
		return result;
	
	}
	
	/**public void scheduleComputeAverage() throws Exception
	{
		this.pushingFuture =
				this.scheduleTask(
						new ComponentI.ComponentTask() {
							@Override
							public void run() {
								try {
									double m = computeMovingAverage();
									String msg = "Autonomic Controler  "+uri+" : "+m;
									if (m > s1)
									{
										if (m > s1 * ( 1 + s_vm)) 
										{
											msg += "This VM have to be saved ";
											AdaptateurOfRequests ar = new AdaptateurOfRequests("VM", app_uri);
											obp.allocateVM(ar);																

										}
										else if (m > s1 * (1 + score))
										{
											msg += "SAVE ME !! Add Core";
											AdaptateurOfRequests ar = new AdaptateurOfRequests("VM", app_uri);
											obp.allocateCore(ar);															

										}
										else if (m > s1 * ( 1 + sfrequence)) 
										{
											msg += "SAVE ME !! Up Frequency";
											AdaptateurOfRequests ar = new AdaptateurOfRequests("VM", app_uri);
											ar.setFrequency(Frequency.up);
											obp.changeFrequency(ar);
										}
									}

									else
									{
										if (m <= s1 * (1 - s_vm)) 
										{
											msg += "SAVE ME !! delete VM";
											AdaptateurOfRequests ar = new AdaptateurOfRequests("VM", app_uri);
											obp.releaseVM(ar);
										}
										else if (m <= s1 * (1 - score)) 
										{
											msg += "SAVE ME !! delete Core";
											AdaptateurOfRequests ar = new AdaptateurOfRequests("VM", app_uri);
											obp.releaseCore(ar);
										}
										else if (m <= s1 * (1 - sfrequence)) 
										{
											msg += "SAVE ME !! Down Frequency";
											AdaptateurOfRequests ar = new AdaptateurOfRequests("VM", app_uri);
											ar.setFrequency(Frequency.down);
											obp.changeFrequency(ar);
										}
									}
									msg += "SAVE ME !! Or Not !";

									logMessage(msg);

								} catch (Exception e) {
									e.printStackTrace();
									throw new RuntimeException(e) ;
								}
								try {
									scheduleComputeAverage();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}, interval, TimeUnit.MILLISECONDS) ;
	}**/

	
	

}
