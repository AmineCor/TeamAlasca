package com.teamalasca.requestdispatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherManagementI;
import com.teamalasca.requestdispatcher.ports.RequestDispatcherDynamicStateDataInboundPort;
import com.teamalasca.requestdispatcher.ports.RequestDispatcherManagementInboundPort;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ComponentI;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.ports.AbstractPort;
import fr.upmc.datacenter.interfaces.ControlledDataOfferedI;
import fr.upmc.datacenter.interfaces.PushModeControllerI;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;

/**
 * The class <code>RequestDispatcher</code> is a component receiving request submissions from
 * a given application, and dispatching these requests to the different virtual machines
 * allocated for this application.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Cl�ment George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class RequestDispatcher
extends AbstractComponent 
implements RequestDispatcherManagementI,
RequestSubmissionHandlerI,
RequestNotificationHandlerI,
PushModeControllerI
{

	/** A private URI to identify this request dispatcher, for debug purpose. */
	private final String URI;

	/** URIs of the virtual machines inbound ports allocated to this request dispatcher. */
	private final List<String> virtualMachinesRequestSubmissionsInboundPortURIs;

	/** Outbound ports of the request dispatcher connected with the virtual machines allocated for execute the application.
	 * A linked list is used in order to deal with our dispatching policy. */
	private final List<RequestSubmissionOutboundPort> rsops;

	/** Index of the next VM to be used **/
	private int vmCursor = 0;

	/** Inbound port offering the management interface.	*/
	protected RequestDispatcherManagementInboundPort rdmip ;

	/** Inbound port of the request dispatcher receiving notifications from the virtual machines. */
	private final RequestNotificationInboundPort rnip;

	/** Inbound port of the request dispatcher connected with the application. */
	private final RequestSubmissionInboundPort rsip;

	/** Outbound port  of the request dispatcher sending notifications to the application. */
	protected final RequestNotificationOutboundPort rnop;

	/** Request dispatcher data inbound port through which it pushes its dynamic data. */
	private RequestDispatcherDynamicStateDataInboundPort rddsdip;

	/** Map for storing starts of request executions */
	private Map<String,Long> requestExecutionStartingTimes;

	/** Number of execution times we store for calculating the execution time average */
	private final static int EXECUTION_TIME_HISTORY_SIZE = 20;

	/** Number of instructions defining the unit of the calculated execution average (ms/X) */
	public final static int EXECUTION_TIME_UNIT = 1_000_000_000;

	/** Array storing the last request execution times average */
	private final Double[] executionTimeAveragesHistory;

	/** Cursor for handling purge of last request execution times */
	private int executionTimeAveragesHistoryCursor;

	/** Future of the task scheduled to push dynamic data. */
	private ScheduledFuture<?> pushingFuture;

	/**
	 * Construct a <code>RequestDispatcher</code>.
	 * 
	 * @param requestDispatcherURI the request dispatcher URI.
	 * @param requestDispatcherManagementInboundPortURI the request dispatcher management inbound port URI.
	 * @param requestSubmissionInboundPortURI the request submission inbound port URI.
	 * @param requestNotificationInboundPortURI the request notification inbound port URI.
	 * @param requestNotificationOutboundPortURI the request notification outbound port URI.
	 * @param requestDispatcherDynamicStateDataInboundPortURI the request dispatcher dynamic state data inbound port URI.
	 * @throws Exception throws an exception if an error occured..
	 */
	public RequestDispatcher(
			final String requestDispatcherURI,
			final String requestDispatcherManagementInboundPortURI,
			final String requestSubmissionInboundPortURI,
			final String requestNotificationInboundPortURI,
			final String requestNotificationOutboundPortURI,
			final String requestDispatcherDynamicStateDataInboundPortURI)
					throws Exception
	{
		super(1,1);

		// Pre-conditions
		assert requestDispatcherURI != null;
		assert requestDispatcherManagementInboundPortURI != null ;
		assert requestSubmissionInboundPortURI != null;
		assert requestNotificationInboundPortURI != null;
		assert requestNotificationOutboundPortURI != null;
		assert requestDispatcherDynamicStateDataInboundPortURI != null;

		this.URI = requestDispatcherURI;

		// for now, no vm is allocated to this request dispatcher
		this.virtualMachinesRequestSubmissionsInboundPortURIs = new ArrayList<>();

		// and no outbound port is initialized
		this.rsops = Collections.synchronizedList(new ArrayList<RequestSubmissionOutboundPort>());
		this.addRequiredInterface(RequestSubmissionI.class);

		// whenever the other ports are initialized
		this.addOfferedInterface(RequestDispatcherManagementI.class) ;
		this.rdmip = new RequestDispatcherManagementInboundPort(requestDispatcherManagementInboundPortURI, this);
		this.addPort(this.rdmip);
		this.rdmip.publishPort();

		this.rsip = new RequestSubmissionInboundPort(requestSubmissionInboundPortURI, this);
		this.addPort(rsip);
		this.rsip.publishPort();
		this.addOfferedInterface(RequestSubmissionI.class);

		this.rnop = new RequestNotificationOutboundPort(requestNotificationOutboundPortURI, this);
		this.addPort(this.rnop);
		this.rnop.publishPort();
		this.addRequiredInterface(RequestNotificationI.class);

		this.rnip = new RequestNotificationInboundPort(requestNotificationInboundPortURI, this);
		this.addPort(this.rnip);
		this.rnip.publishPort();
		this.addOfferedInterface(RequestNotificationI.class);

		this.rddsdip = new RequestDispatcherDynamicStateDataInboundPort(requestDispatcherDynamicStateDataInboundPortURI,this);
		this.addPort(this.rddsdip);
		this.rddsdip.publishPort();
		this.addOfferedInterface(ControlledDataOfferedI.ControlledPullI.class);

		// Initialize elements to compute average
		requestExecutionStartingTimes = Collections.synchronizedMap(new HashMap<String,Long>());
		this.executionTimeAveragesHistory = new Double[EXECUTION_TIME_HISTORY_SIZE];
		executionTimeAveragesHistoryCursor = 0;
	}

	/**
	 * Construct a <code>RequestDispatcher</code>.
	 * 
	 * @param requestDispatcherManagementInboundPortURI the request dispatcher management inbound port URI.
	 * @param requestSubmissionInboundPortURI the request submission inbound port URI.
	 * @param requestNotificationInboundPortURI the request notification inbound port URI.
	 * @param requestNotificationOutboundPortURI the request notification outbound port URI.
	 * @param requestDispatcherDynamicStateDataInboundPortURI the request dispatcher dynamic state data inbound port URI.
	 * @throws Exception throws an exception if an error occured..
	 */
	public RequestDispatcher(
			final String requestDispatcherManagementInboundPortURI,
			final String requestSubmissionInboundPortURI ,
			final String requestNotificationInboundPortURI,
			final String requestNotificationOutboundPortURI,
			final String requestDispatcherDynamicStateDataInboundPortURI)
					throws Exception
	{
		this(
				AbstractPort.generatePortURI(),
				requestDispatcherManagementInboundPortURI,
				requestSubmissionInboundPortURI,
				requestNotificationInboundPortURI,
				requestNotificationOutboundPortURI,
				requestDispatcherDynamicStateDataInboundPortURI);
	}

	/**
	 * @see fr.upmc.components.AbstractComponent#shutdown()
	 */
	@Override
	public void shutdown() throws ComponentShutdownException
	{
		try {
			for (RequestSubmissionOutboundPort rsop : rsops) {
				rsop.doDisconnection();
				rsop.unpublishPort();
			}

			rnop.doDisconnection();
			rnop.unpublishPort();
		}
		catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
	}

	/**
	 * Get the average of the time execution request.
	 * @return the average of the time execution request in milli seconds
	 */
	private Double getRequestExecutionAverage()
	{	
		int i = 0;
		Double sum = 0D;
		for(;i<this.executionTimeAveragesHistory.length;i++){
			if(this.executionTimeAveragesHistory[i] == null)
				break;
			sum += this.executionTimeAveragesHistory[i];
		}
		if(sum == 0D)
			return null;
		return sum / i;
	}

	/**
	 * @see com.teamalasca.requestdispatcher.interfaces.RequestDispatcherManagementI#associateVirtualMachine(java.lang.String)
	 */
	@Override
	public void associateVirtualMachine(final String virtualMachineRequestSubmissionInboundPortURI) throws Exception
	{
		if (this.virtualMachinesRequestSubmissionsInboundPortURIs.contains(virtualMachineRequestSubmissionInboundPortURI)) {
			return;
		}

		// adding the new virtual machine to our internal vm list
		this.virtualMachinesRequestSubmissionsInboundPortURIs.add(virtualMachineRequestSubmissionInboundPortURI);

		// creating a new outbound port for the VM
		final String URI = AbstractPort.generatePortURI();
		RequestSubmissionOutboundPort rsop = new RequestSubmissionOutboundPort(URI,this);
		this.addPort(rsop);
		rsop.publishPort();

		// creating the connection from the new port to the VM
		rsop.doConnection(virtualMachineRequestSubmissionInboundPortURI, RequestSubmissionConnector.class.getCanonicalName());

		// adding the port to our internal outbound port list
		this.rsops.add(vmCursor, rsop);

		logMessage("a new virtual machine (submission input port:'"+ virtualMachineRequestSubmissionInboundPortURI + "') has been associated to " + this.toString());
	}

	/**
	 * @see com.teamalasca.requestdispatcher.interfaces.RequestDispatcherManagementI#dissociateVirtualMachine(java.lang.String)
	 */
	@Override
	public void dissociateVirtualMachine(final String virtualMachineRequestSubmissionInboundPortURI) throws Exception
	{
		if (!this.virtualMachinesRequestSubmissionsInboundPortURIs.contains(virtualMachineRequestSubmissionInboundPortURI)) {
			return;
		}

		synchronized (rsops) {
			for (Iterator<RequestSubmissionOutboundPort> it = rsops.iterator(); it.hasNext();) {
				RequestSubmissionOutboundPort rsop = it.next();
				if (rsop.getServerPortURI().equals(virtualMachineRequestSubmissionInboundPortURI)) {
					it.remove();
					rsop.unpublishPort();
					rsop.doDisconnection();
				}
			}
		}

		this.virtualMachinesRequestSubmissionsInboundPortURIs.remove(virtualMachineRequestSubmissionInboundPortURI);
		logMessage("virtual machine (submission input port:'" + virtualMachineRequestSubmissionInboundPortURI + "') has been dissociated to " + this.toString());
	}

	/**
	 * @see com.teamalasca.requestdispatcher.interfaces.RequestDispatcherManagementI#hasOnlyOneVirtualMachine()
	 */
	@Override
	public boolean hasOnlyOneVirtualMachine() throws Exception
	{
		return this.rsops.size() == 1;
	}

	/**
	 * @see fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI#acceptRequestSubmission(fr.upmc.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void acceptRequestSubmission(final RequestI r) throws Exception
	{
	}

	/**
	 * @see fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI#acceptRequestSubmissionAndNotify(fr.upmc.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception
	{
		if (rsops.isEmpty()) {
			throw new Exception("request '" + r.getRequestURI() + "' cant be handled because no vm is connected to the " + this.toString());
		}

		synchronized (rsops){

			RequestSubmissionOutboundPort rsop = rsops.get(vmCursor);
			vmCursor = (vmCursor + 1) % rsops.size();

			if (!rsop.connected()) {
				throw new Exception("port '" + rsop.getPortURI()+"' of " + this.toString() + " is disconnected, that should not happen");
			}

			requestExecutionStartingTimes.put(r.getRequestURI(), System.currentTimeMillis());
			// Send request to VM
			rsop.submitRequestAndNotify(r);
			logMessage("request '" + r.getRequestURI() + "' submitted to " + this.toString());
		}
	}

	/**
	 * @see fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI#acceptRequestTerminationNotification(fr.upmc.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception
	{

		// Add execution time to the history
		long requestExecutionTime = System.currentTimeMillis() - requestExecutionStartingTimes.remove(r.getRequestURI());
		double weight = ((double)r.getPredictedNumberOfInstructions()) / EXECUTION_TIME_UNIT;
		if(weight == 0D){ // avoid division by 0
			this.executionTimeAveragesHistory[executionTimeAveragesHistoryCursor] = 0D;
		}
		else{
			this.executionTimeAveragesHistory[executionTimeAveragesHistoryCursor] = ((double)requestExecutionTime) / weight;
		}
		System.out.println(this.executionTimeAveragesHistory[executionTimeAveragesHistoryCursor]);
		executionTimeAveragesHistoryCursor = (executionTimeAveragesHistoryCursor + 1) % executionTimeAveragesHistory.length;
		rnop.notifyRequestTermination(r);
	}

	/**
	 * Get a request dispatcher dynamic state.
	 * 
	 * @return a request dispatcher dynamic state.
	 * @throws Exception throws an exception if an error occured..
	 */
	public RequestDispatcherDynamicStateI getDynamicState() throws Exception
	{
		final Double executionAverage = getRequestExecutionAverage();
		// Average is null at startup
		if (executionAverage == null) {
			return null;
		}

		RequestDispatcherDynamicState rdds = new RequestDispatcherDynamicState(this.URI, executionAverage);
		return rdds;
	}

	/**
	 * Push the dynamic state of the request dispatcher through its notification data
	 * inbound port.
	 * 
	 *
	 * @throws Exception throws an exception if an error occured..
	 */
	public void	sendDynamicState() throws Exception
	{
		if (this.rddsdip.connected()) {
			RequestDispatcherDynamicStateI rdds = this.getDynamicState();

			if (rdds != null) {
				this.rddsdip.send(rdds) ;
			}
		}
	}

	/**
	 * Push the dynamic state of the request dispatcher through its notification data
	 * inbound port at a specified time interval in ms and for a specified
	 * number of times.
	 * 
	 * @param interval the interval in ms.
	 * @param numberOfRemainingPushes the number of remaining pushes.
	 * @throws Exception throws an exception if an error occured..
	 */
	public void	sendDynamicState(final int interval, int numberOfRemainingPushes) throws Exception
	{
		this.sendDynamicState();

		final int fNumberOfRemainingPushes = numberOfRemainingPushes - 1;
		if (fNumberOfRemainingPushes > 0) {
			final RequestDispatcher rd = this;
			this.pushingFuture =
					this.scheduleTask(
							new ComponentI.ComponentTask() {
								@Override
								public void run() {
									try {
										rd.sendDynamicState(
												interval,
												fNumberOfRemainingPushes);
									} catch (Exception e) {
										throw new RuntimeException(e);
									}
								}
							}, interval, TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * @see fr.upmc.datacenter.interfaces.PushModeControllingI#startUnlimitedPushing(int)
	 */
	@Override
	public void startUnlimitedPushing(int interval) throws Exception
	{
		final RequestDispatcher rd = this;
		this.pushingFuture =
				this.scheduleTaskAtFixedRate(
						new ComponentI.ComponentTask() {
							@Override
							public void run() {
								try {
									rd.sendDynamicState();
								}
								catch (Exception e) {
									throw new RuntimeException(e);
								}
							}
						}, interval, interval, TimeUnit.MILLISECONDS);
	}

	/**
	 * @see fr.upmc.datacenter.interfaces.PushModeControllingI#startLimitedPushing(int, int)
	 */
	@Override
	public void startLimitedPushing(final int interval, final int n) throws Exception
	{
		assert n > 0;

		this.logMessage(this.URI + " startLimitedPushing with interval "
				+ interval + " ms for " + n + " times.");

		final RequestDispatcher rd = this;
		this.pushingFuture =
				this.scheduleTask(
						new ComponentI.ComponentTask() {
							@Override
							public void run() {
								try {
									rd.sendDynamicState(interval, n);
								}
								catch (Exception e) {
									throw new RuntimeException(e);
								}
							}
						}, interval, TimeUnit.MILLISECONDS);
	}

	/**
	 * @see fr.upmc.datacenter.interfaces.PushModeControllingI#stopPushing()
	 */
	@Override
	public void stopPushing() throws Exception
	{
		if (this.pushingFuture != null && !(this.pushingFuture.isCancelled() ||
				this.pushingFuture.isDone())) {
			this.pushingFuture.cancel(false) ;
		}		
	}

	/** 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "request dispatcher '" + URI + "'";
	}

}
