package com.teamalasca.requestdispatcher;

import java.util.ArrayList;
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
 * A request dispatcher is a component receiving request submissions from
 * a given application, and dispatching these requests to the different VM
 * allocated for this application.
 */
public class RequestDispatcher
extends AbstractComponent 
implements RequestDispatcherManagementI,
		   RequestSubmissionHandlerI,
		   RequestNotificationHandlerI,
		   PushModeControllerI
{

	/** A private URI to identify this request dispatcher, for debug purpose */
	private final String URI;

	/** URIs of the virtual machines inbound ports allocated to this request dispatcher */
	private final List<String> virtualMachinesRequestSubmissionsInboundPortURIs;

	/** Outbound ports of the request dispatcher connected with the virtual machines allocated for execute the application.
	 * A linked list is used in order to deal with our dispatching policy */
	private final LinkedList<RequestSubmissionOutboundPort> rsops;

	/** Inbound port offering the management interface.	*/
	protected RequestDispatcherManagementInboundPort rdmip ;
	
	/** Inbound port of the request dispatcher receiving notifications from the virtual machines */
	private final RequestNotificationInboundPort rnip;

	/** Inbound port of the request dispatcher connected with the application */
	private final RequestSubmissionInboundPort rsip;

	/** Outbound port  of the request dispatcher sending notifications to the application */
	protected final RequestNotificationOutboundPort rnop;
	
	/** Request dispatcher data inbound port through which it pushes its dynamic data */
	private RequestDispatcherDynamicStateDataInboundPort rddsdip;
	
	/** Map to keep execution time for each request */
	private Map<String, Long> executionTimeRequest;
	
	/** Sum of all exection time of each request */
	private double executionTimeRequestSum;
	
	/** Number of requests */
	private int nbRequests;

	/** Counter of total called "getAvg" */
	private int counterAvgRequest;
	
	/** We definie a limit to avoid constant average value */
	private static final int LIMIT_RESET_COUNTER_AVG = 10;
	
	/** Future of the task scheduled to push dynamic data */
	private ScheduledFuture<?> pushingFuture;
	
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
		this.rsops = new LinkedList<>();
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
		this.executionTimeRequest = new HashMap<>();
		resetAvgCriteria();
	}

	public RequestDispatcher(
			final String managementInboundPortURI,
			final String requestSubmissionInboundPortURI ,
			final String requestNotificationInboundPortURI,
			final String requestNotificationOutboundPortURI,
			final String requestDispatcherDynamicStateDataInboundPortURI)
					throws Exception
	{
		this(
				AbstractPort.generatePortURI(),
				managementInboundPortURI,
				requestSubmissionInboundPortURI,
				requestNotificationInboundPortURI,
				requestNotificationOutboundPortURI,
				requestDispatcherDynamicStateDataInboundPortURI);
	}
	
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
		this.rsops.addFirst(rsop);

		logMessage("a new virtual machine (submission input port:'"+ virtualMachineRequestSubmissionInboundPortURI + "') has been associated to " + this.toString());
	}

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

	@Override
	public void acceptRequestSubmission(final RequestI r) throws Exception
	{
	}

	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception
	{
		if (rsops.isEmpty()) {
			throw new Exception("request '" + r.getRequestURI() + "' cant be handled because no vm is connected to the " + this.toString());
		}

		RequestSubmissionOutboundPort rsop = rsops.removeFirst(); 

		if (!rsop.connected()) {
			throw new Exception("port '" + rsop.getPortURI()+"' of " + this.toString() + " is disconnected, that should not happen");
		}
		
		// Keep start time
		executionTimeRequest.put(r.getRequestURI(), System.currentTimeMillis());
		
		// Send request to VM
		rsop.submitRequestAndNotify(r);
		logMessage("request '" + r.getRequestURI() + "' submitted to " + this.toString());

		// the port is pushed at the last position of the list, performing a good ports turnover
		rsops.addLast(rsop);
	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception
	{
		// Add execution time to the sum
		executionTimeRequestSum = (double) (System.currentTimeMillis() - executionTimeRequest.remove(r.getRequestURI()));
		++nbRequests;
	
		rnop.notifyRequestTermination(r);
	}

	public RequestDispatcherDynamicStateI getDynamicState() throws Exception
	{
		final Double avg = getAvg();
		// Average is null in the case where this.nbRequests = 0
		if (avg == null) {
			return null;
		}
		
		++counterAvgRequest;
		RequestDispatcherDynamicState rdds = new RequestDispatcherDynamicState(this.URI, getAvg());
		
		// Reset avg criteria each LIMIT_RESET_COUNTER_AVG times
		if (counterAvgRequest % LIMIT_RESET_COUNTER_AVG == 0) {
			resetAvgCriteria();
		}
		
		return rdds;
	}
	
	private void resetAvgCriteria()
	{
		this.executionTimeRequestSum = 0;
		this.nbRequests = 0;
		this.counterAvgRequest = 0;
	}
	
	private Double getAvg()
	{
		//logMessage(this.toString() + " getAvg : " + executionTimeRequestSum + " - " + nbRequests);
		
		// We can't compute the average if nbRequests is reset to 0
		return this.nbRequests <= 0 ? null : this.executionTimeRequestSum / this.nbRequests;
	}
	
	@Override
	public String toString()
	{
		return "request dispatcher '" + URI + "'";
	}

	/**
	 * push the dynamic state of the request dipatcher through its notification data
	 * inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception
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
	 * push the dynamic state of the request dipatcher through its notification data
	 * inbound port at a specified time interval in ms and for a specified
	 * number of times.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param interval
	 * @param numberOfRemainingPushes
	 * @throws Exception
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

	@Override
	public void stopPushing() throws Exception
	{
		if (this.pushingFuture != null && !(this.pushingFuture.isCancelled() ||
			this.pushingFuture.isDone())) {
			this.pushingFuture.cancel(false) ;
		}		
	}

	@Override
	public boolean hasOnlyOneVirtualMachine() throws Exception
	{
		return this.rsops.size() == 1;
	}
	
}
