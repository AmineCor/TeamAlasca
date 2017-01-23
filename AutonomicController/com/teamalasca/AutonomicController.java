package com.teamalasca;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherStateDataConsumerI;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ComponentI;

public class AutonomicController
extends AbstractComponent
implements RequestDispatcherStateDataConsumerI
{
	
	private String acURI;
	private String acServicesInboundPortURI;
	
	private List<Double> avgList;
	private final static int LIMIT_SIZE_AVG_LIST = 3;
	
	private static final double THRESHOLD = 3000.0;
	private static final double THRESHOLD_FREQUENCY = 0.20;
	private static final double THRESHOLD_CORE = 0.40;
	private static final double THRESHOLD_VM = 0.80;

	private static final int interval = 30000;
	
	protected ScheduledFuture<?> pushingFuture;

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
		synchronized (avgList) {
			// Add the average we just received
			this.avgList.add(currentDynamicState.getExecutionTimeAvg());
			
			// For moving average, we only need 3 values
			if (avgList.size() > LIMIT_SIZE_AVG_LIST) {
				avgList.remove(0);
			}	
		}
	}
	
	public Double computeMovingAverage()
	{
		synchronized (avgList) {
			// Need to check if we at least got an average
			if (avgList.isEmpty()) {
				return null;
			}
			
			// Compute moving average
			double result = 0;
			for (double i : avgList)
			{
				result += i;
			}
			result = result / avgList.size();
			
			logMessage("AutonomicControler : Moving average = " + result);
			return result;
		}
	}
	
	public void scheduleComputeAverage() throws Exception
	{
		this.pushingFuture =
				this.scheduleTask(
						new ComponentI.ComponentTask() {
							@Override
							public void run() {
								try {
									Double movingAvg = computeMovingAverage();
									//LinkedList<InfoVM> infos  = null;
									
									// 
									if (movingAvg != null) {
										//infos = (LinkedList<InfoVM>)infosVM.clone();
										
										
	//									for (InfoVM info : infos) {
	//										info.print();
	//									}
	
										// We are above the THRESHOLD
										if (movingAvg > THRESHOLD) {
											// Allocate VM
											if (movingAvg > THRESHOLD * (1 + THRESHOLD_VM)) {
												logMessage("AutonomicController : Allocate VM");

											 //   allocateVM();			
											}
											// Allocate core
											else if (movingAvg > THRESHOLD * (1 + THRESHOLD_CORE)) {
												logMessage("AutonomicController : Allocate core");

											  //   allocateCore(getLessEffectiveVM(infos));
											}
											// Increase frequency
											else if (movingAvg > THRESHOLD * (1 + THRESHOLD_FREQUENCY)) {
												logMessage("AutonomicController : Increase frequency");

										        // changeFrequency(getLessEffectiveVM(infos),Frequency.up);
											}
											// In other case do nothing
											else {
												logMessage("AutonomicController : no adaptation");
											}
										}
										else {
											// Release VM
											if (movingAvg <= THRESHOLD * (1 - THRESHOLD_VM)) {
												logMessage("AutonomicController : Release VM");

										          //releaseVM(getMostEffectiveVM(infos));
											}
											// Release core
											else if (movingAvg <= THRESHOLD * (1 - THRESHOLD_CORE)) {
												logMessage("AutonomicController : Release core");

												  //releaseCore(getMostEffectiveVM(infos));
											}
											// Decrease frequency
											else if (movingAvg <= THRESHOLD * (1 - THRESHOLD_FREQUENCY)) {
												logMessage("AutonomicController : Decrease frequency");

												  //changeFrequency(getMostEffectiveVM(infos),Frequency.down);
											}
											/* In other case do nothing
											else {
											}
											*/
										}
									}
								}
								catch (Exception e) {
									e.printStackTrace();
									throw new RuntimeException(e);
								}
								try {
									scheduleComputeAverage();
								}
								catch (Exception e) {
									e.printStackTrace();
								}
							}
						}, interval, TimeUnit.MILLISECONDS) ;
	}

}
