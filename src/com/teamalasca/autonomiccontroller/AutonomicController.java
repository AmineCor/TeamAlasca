package com.teamalasca.autonomiccontroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.teamalasca.autonomiccontroller.interfaces.AutonomicControllerServicesI;
import com.teamalasca.autonomiccontroller.ports.AutonomicControllerServicesInboundPort;
import com.teamalasca.computer.connectors.CoreManagementConnector;
import com.teamalasca.computer.interfaces.CoreManagementI;
import com.teamalasca.computer.ports.CoreManagementOutboundPort;
import com.teamalasca.requestdispatcher.connectors.RequestDispatcherManagementConnector;
import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherStateDataConsumerI;
import com.teamalasca.requestdispatcher.ports.RequestDispatcherDynamicStateDataOutboundPort;
import com.teamalasca.requestdispatcher.ports.RequestDispatcherManagementOutboundPort;
import com.teamalasca.utils.ApplicationVMData;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractPort;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;

/**
 * The class <code>AutonomicController</code> is a component to perform
 * adaptations depending of the execution times of the requests.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public final class AutonomicController
extends AbstractComponent
implements AutonomicControllerServicesI,
		   RequestDispatcherStateDataConsumerI
{
	/** List of averages from the request dispatcher to compute the moving average. */
	private List<Double> requestExecutionAverages;
	
	/** The "N" value to compute moving average. */
	private final static int MAX_SIZE_AVERAGE_LIST = 3;
	
	/** Default core number to allocate cores. */
	private final static int DEFAULT_CORE_NUMBER = 4;
	
	/** Threshold to do adaptation. */
	private static final double THRESHOLD = 1500.0;
	
	/** Threshold frequency to do adaptation. */
	private static final double THRESHOLD_FREQUENCY = 0.20;
	
	/** Threshold core to do adaptation. */
	private static final double THRESHOLD_CORE = 0.40;
	
	/** Threshold virtual machine to do adaptation. */
	private static final double THRESHOLD_VM = 0.80;
	
	/** Delta frequency to increase or decrease frequency of a core. */
	private static final int DELTA_FREQUENCY = 100;

	/** Do adaptation every 15 seconds. */
	private static final int PERIODIC_INTERVAL_ADAPTATION = 15000;
	
	/** Push data from the request dispatcher every 5 seconds. */
	private static final int PUSHING_INTERVAL_REQUEST_DISPATCHER = 5000;

	/** A private URI to identify this autonomic controller, for debug purpose. */
	private final String URI;

	/** URI of the dispatcher associated with this autonomic controller. */
	private String requestDispatcherURI;
	
	/** URI of the dispatcher associated with this autonomic controller. */
	private String requestDispatcherRequestNotificationInboundPortURI;

	/** Inbound port offering the management interface.	*/
	private AutonomicControllerServicesInboundPort acsip;
	
	/** Outbound port connected to the request dispatcher management. */
	private RequestDispatcherManagementOutboundPort rdmop;
	
	/** Outbound port connected to the request dispatcher component to receive its data. */
	private RequestDispatcherDynamicStateDataOutboundPort rddsdop;

	/** Outbound port connected to the computer component to receive its data. */
	private ComputerDynamicStateDataOutboundPort cdsdop;

	/** Outbound port connected to the computer to manage its cores. **/
	private CoreManagementOutboundPort cmop;

	/** Internal ports to manage the application virtual machines allocated. */
	private Map<ApplicationVMManagementOutboundPort, ApplicationVMData> avmmop;

	/**
	 * Construct an <code>AutonomicController</code>.
	 * 
	 * @param autonomicControllerURI the autonomic controller URI.
	 * @param autonomicControllerServicesInboundPortURI the autonomic controller services inbound port URI.
	 * @param computerURI the computer URI.
	 * @param computerDynamicStateDataInboundPortURI the computer dynamic state data inbound port URI.
	 * @param manageCoreInboundPortURI the management core inbound port URI.
	 * @param requestDispatcherURI the request dispatcher URI.
	 * @param requestDispatcherManagementInboundPortURI the request dispatcher management inbound port URI.
	 * @param requestDispatcherDynamicStateDataInboundPortURI the request dispatcher dynamic state data inbound port URI.
	 * @param requestDispatcherRequestNotificationInboundPortURI the request dispatcher request notification inbound port URI.
	 * @throws Exception throws an exception if an error occured..
	 */
	public AutonomicController(
			final String autonomicControllerURI,
			final String autonomicControllerServicesInboundPortURI,
			final String computerURI,
			final String computerDynamicStateDataInboundPortURI,
			final String manageCoreInboundPortURI,
			final String requestDispatcherURI,
			final String requestDispatcherManagementInboundPortURI,
			final String requestDispatcherDynamicStateDataInboundPortURI,
			final String requestDispatcherRequestNotificationInboundPortURI)
					throws Exception
	{
		super(1, 1);

		// Preconditions
		assert autonomicControllerURI != null;
		assert autonomicControllerServicesInboundPortURI != null;
		assert computerURI != null;
		assert manageCoreInboundPortURI != null;
		assert requestDispatcherURI != null;
		assert requestDispatcherManagementInboundPortURI != null;
		assert requestDispatcherDynamicStateDataInboundPortURI != null;
		assert requestDispatcherRequestNotificationInboundPortURI != null;
		
		this.URI = autonomicControllerURI;
		this.requestDispatcherRequestNotificationInboundPortURI = requestDispatcherRequestNotificationInboundPortURI;
		this.requestExecutionAverages = Collections.synchronizedList(new ArrayList<Double>());
		this.avmmop = new HashMap<>();
		
		// Create port to use the autonomic controller
		this.addOfferedInterface(AutonomicControllerServicesI.class) ;
		this.acsip = new AutonomicControllerServicesInboundPort(autonomicControllerServicesInboundPortURI, this);
		this.addPort(this.acsip);
		this.acsip.publishPort();
		
		// create port to manage the request dispatcher
		this.rdmop = new RequestDispatcherManagementOutboundPort(AbstractPort.generatePortURI(), this);
		this.rdmop.publishPort();
		this.rdmop.doConnection(requestDispatcherManagementInboundPortURI, RequestDispatcherManagementConnector.class.getCanonicalName());

		// create port to receive computer data
		this.cdsdop = new ComputerDynamicStateDataOutboundPort(this, computerURI);
		this.addPort(cdsdop) ;
		this.cdsdop.publishPort() ;
		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class) ;

		// create port to allocate and manage computer cores
		this.cmop = new CoreManagementOutboundPort(this);
		this.addRequiredInterface(CoreManagementI.class);
		this.addPort(this.cmop);
		this.cmop.publishPort();

		// create port to receive requests execution time from the request dispatcher
		this.rddsdop = new RequestDispatcherDynamicStateDataOutboundPort(this, requestDispatcherURI);
		this.addPort(rddsdop);
		this.rddsdop.publishPort();
		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class) ;

		// Connect the Request Dispatcher and the Autonomic Controller
		doConnectionWithRequestDispatcher(requestDispatcherURI, requestDispatcherDynamicStateDataInboundPortURI);

		// create port to receive computer data
		doConnectionWithComputer(computerURI, computerDynamicStateDataInboundPortURI,manageCoreInboundPortURI);	

		// Allocate cores to application vm
		allocateVm();
	}

	/**
	 * Construct an <code>AutonomicController</code>.
	 * 
	 * @param autonomicControllerServicesInboundPortURI the autonomic controller services inbound port URI.
	 * @param computerURI the computer URI.
	 * @param computerDynamicStateDataInboundPortURI the computer dynamic state data inbound port URI.
	 * @param manageCoreInboundPortURI the management core inbound port URI.
	 * @param requestDispatcherURI the request dispatcher URI.
	 * @param requestDispatcherManagementInboundPortURI the request dispatcher management inbound port URI.
	 * @param requestDispatcherDynamicStateDataInboundPortURI the request dispatcher dynamic state data inbound port URI.
	 * @param requestDispatcherRequestNotificationInboundPortURI the request dispatcher request notification inbound port URI.
	 * @throws Exception throws an exception if an error occured..
	 */
	public AutonomicController(
			final String autonomicControllerServicesInboundPortURI,
			final String computerURI,
			final String computerDynamicStateDataInboundPortURI,
			final String manageCoreInboundPortURI,
			final String requestDispatcherURI,
			final String requestDispatcherManagementInboundPortURI,
			final String requestDispatcherDynamicStateDataInboundPortURI,
			final String requestDispatcherRequestNotificationInboundPortURI)
					throws Exception
	{
		this(
				AbstractPort.generatePortURI(),
				autonomicControllerServicesInboundPortURI,
				computerURI,
				computerDynamicStateDataInboundPortURI,
				manageCoreInboundPortURI,
				requestDispatcherURI,
				requestDispatcherManagementInboundPortURI,
				requestDispatcherDynamicStateDataInboundPortURI,
				requestDispatcherRequestNotificationInboundPortURI);
	}

	/**
	 * Connect the autonomic controller with the request dispatcher.
	 * 
	 * @param requestDispatcherURI the request dispatcher URI.
	 * @param requestDispatcherDynamicStateDataInboundPortURI the request dispatcher dynamic state data inbound port URI.
	 * @throws Exception throws an exception if an error occured..
	 */
	public void doConnectionWithRequestDispatcher(
			final String requestDispatcherURI,
			final String requestDispatcherDynamicStateDataInboundPortURI)
					throws Exception
	{
		this.requestDispatcherURI = requestDispatcherURI;
		this.rddsdop.doConnection(
				requestDispatcherDynamicStateDataInboundPortURI,
				ControlledDataConnector.class.getCanonicalName());
		
		// Push average from request dispatcher
		this.rddsdop.startUnlimitedPushing(PUSHING_INTERVAL_REQUEST_DISPATCHER);
	}

	/**
	 * Connect the autonomic controller with ports of a computer component.
	 * 
	 * @param computerURI the computer URI.
	 * @param computerDynamicStateDataInboundPortURI the computer dynamic state data inbound port URI.
	 * @param manageCoreInboundPortURI the management core inbound port URI.
	 * @throws Exception throws an exception if an error occured..
	 */
	private void doConnectionWithComputer(
			final String computerURI,
			final String computerDynamicStateDataInboundPortURI,
			final String manageCoreInboundPortURI)
					throws Exception
	{
		this.cmop.doConnection(manageCoreInboundPortURI, CoreManagementConnector.class.getCanonicalName());
	}

	/** 
	 * @see com.teamalasca.requestdispatcher.interfaces.RequestDispatcherStateDataConsumerI#acceptRequestDispatcherDynamicData(java.lang.String, com.teamalasca.requestdispatcher.interfaces.RequestDispatcherDynamicStateI)
	 */
	@Override
	public void acceptRequestDispatcherDynamicData(
			String dispatcherURI,
			RequestDispatcherDynamicStateI currentDynamicState)
					throws Exception
	{
		this.logMessage(this.toString() + "received a message from a request dispatcher");

		// data received from an unknown dispatcher
		if (dispatcherURI != requestDispatcherURI) {
			return;
		}

		synchronized (requestExecutionAverages) {
			// Add received average to compute moving average later
			requestExecutionAverages.add(currentDynamicState.getRequestExecutionTimeAverage());

			// For moving average, we only need 3 values
			while (requestExecutionAverages.size() > MAX_SIZE_AVERAGE_LIST) {
				requestExecutionAverages.remove(0);
			}
		}		
	}

	/**
	 * Allocate a new virtual machine.
	 * 
	 * @throws Exception throws an exception if an error occured..
	 */
	private void allocateVm() throws Exception
	{
		//------------- Create the application virtual machine ------------------/
		final String AVMApplicationVMManagementInboundPortURI = AbstractPort.generatePortURI();
		final String AVMRequestSubmissionInboundPortURI = AbstractPort.generatePortURI();
		final String AVMRequestNotificationOutboundPortURI = AbstractPort.generatePortURI();
	
		final ApplicationVM vm = new ApplicationVM(AbstractPort.generatePortURI(),
				AVMApplicationVMManagementInboundPortURI,
				AVMRequestSubmissionInboundPortURI,
				AVMRequestNotificationOutboundPortURI);
		vm.toggleTracing();
		vm.toggleLogging();
		
		// create port to manage the application vm associated with the request dispatcher
		final ApplicationVMManagementOutboundPort AVMManagementOutboundPort = new ApplicationVMManagementOutboundPort(AbstractPort.generatePortURI(), this);
		this.avmmop.put(AVMManagementOutboundPort, new ApplicationVMData(AVMRequestSubmissionInboundPortURI));
		this.addPort(AVMManagementOutboundPort);
		AVMManagementOutboundPort.publishPort();
		AVMManagementOutboundPort.doConnection(AVMApplicationVMManagementInboundPortURI, ApplicationVMManagementConnector.class.getCanonicalName());
		
		// ------- Connect the request dispatcher with the application virtual machine ------/
		this.rdmop.associateVirtualMachine(AVMRequestSubmissionInboundPortURI);
		vm.findPortFromURI(AVMRequestNotificationOutboundPortURI).doConnection(
				this.requestDispatcherRequestNotificationInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName());
		
		this.allocateCores(AVMManagementOutboundPort, DEFAULT_CORE_NUMBER);
	}
	
	/**
	 * Release a virtual machine.
	 * 
	 * @param AVMManagementOutboundPort the application virtual machine management outbound port.
	 * @throws Exception throws an exception if an error occured..
	 */
	private void releaseVm(final ApplicationVMManagementOutboundPort AVMManagementOutboundPort) throws Exception
	{
		// dissociate vm from the request dispatcher
		this.rdmop.dissociateVirtualMachine(this.avmmop.get(AVMManagementOutboundPort).getApplicationVmRequestSubmissionInboundPortURI());
		
		// remove the vm
	}
	
	/**
	 * Allocate cores.
	 * 
	 * @param AVMManagementOutboundPort the application virtual machine management outbound port.
	 * @param nbCores the number of cores to allocate.
	 * @throws Exception throws an exception if an error occured..
	 */
	private void allocateCores(final ApplicationVMManagementOutboundPort AVMManagementOutboundPort, int nbCores) throws Exception
	{
		// allocate its cores
		AllocatedCore[] ac = this.cmop.allocateCores(DEFAULT_CORE_NUMBER);
		AVMManagementOutboundPort.allocateCores(ac);
				
		// keep cores in memory for later adaptation on cores
		this.avmmop.get(AVMManagementOutboundPort).addCores(ac);
	}

	/**
	 * Allocate one core.
	 * 
	 * @param AVMManagementOutboundPort the application virtual machine management outbound port.
	 * @throws Exception throws an exception if an error occured..
	 */
	private void allocateCore(final ApplicationVMManagementOutboundPort AVMManagementOutboundPort) throws Exception
	{
		this.allocateCores(AVMManagementOutboundPort, 1);
	}
	
	/**
	 * Release a core.
	 * 
	 * @param AVMManagementOutboundPort the application virtual machine management outbound port.
	 * @param ac the allocated core to release.
	 * @throws Exception throws an exception if an error occured..
	 */
	private void releaseCore(final ApplicationVMManagementOutboundPort AVMManagementOutboundPort, final AllocatedCore ac) throws Exception
	{
		// release core
		this.cmop.releaseCore(ac);
		
		// release core from memory for this vm
		this.avmmop.get(AVMManagementOutboundPort).removeCore(ac);
	}
	
	/**
	 * Select a random virtual machine.
	 * 
	 * @return a random virtual machine.
	 */
	private ApplicationVMManagementOutboundPort selectRandomVm()
	{
		// Select a random index
		int index = new Random().nextInt(avmmop.size());
		int i = 0;
		for (ApplicationVMManagementOutboundPort entry : avmmop.keySet()) {
			// Return a random vm
			if (i == index) {
				return entry;
			}
			++i;
		}

		// We should never reach this point
		return null;
	}
	
	/**
	 * Compute the moving average.
	 * 
	 * @return the moving average.
	 */
	public Double computeMovingAverage()
	{
		synchronized (requestExecutionAverages) {
			// Need to check if we at least got an average
			if (requestExecutionAverages.isEmpty()) {
				return null;
			}

			// Compute moving average
			double result = 0;
			for (double i : requestExecutionAverages) {
				result += i;
			}
			result = result / requestExecutionAverages.size();

			logMessage(this.toString() + " Moving average = " + result);
			return result;
		}
	}
	
	/**
	 * @see com.teamalasca.autonomiccontroller.interfaces.AutonomicControllerServicesI#doPeriodicAdaptation()
	 */
	@Override
	public void doPeriodicAdaptation() throws Exception
	{
		this.scheduleTask(
				new ComponentI.ComponentTask() {
					@Override
					public void run() {
						try {
							// Compute moving average
							final Double movingAvg = computeMovingAverage();
									
							// Check this value it's not null, it can happen at the beginning of the execution
							if (movingAvg != null) {
								// We are above the THRESHOLD
								if (movingAvg > THRESHOLD) {
									// Allocate VM
									if (movingAvg > THRESHOLD * (1 + THRESHOLD_VM)) {
										logMessage(this.toString() + " : Do adaptation - Allocate VM");
										allocateVm();		
									}
									// Allocate core
									else if (movingAvg > THRESHOLD * (1 + THRESHOLD_CORE)) {
										logMessage(this.toString() + " : Do adaptation - Allocate core");
										allocateCore(selectRandomVm());
									}
									// Increase frequency
									else if (movingAvg > THRESHOLD * (1 + THRESHOLD_FREQUENCY)) {
										logMessage(this.toString() + " : Do adaptation - Increase frequency");
										// Select a random vm
										final ApplicationVMManagementOutboundPort vm = selectRandomVm();
										
										// Select a random core
										final AllocatedCore ac = avmmop.get(vm).selectRandomCore();
										
										// Increase its frequency
										cmop.changeFrequency(ac, cmop.getCurrentFrequency(ac) + DELTA_FREQUENCY);
									}
									// In other case do nothing
									else {
										logMessage(this.toString() + " : No adaptation needed");
									}
								}
								else {
									// Release VM
									if (movingAvg <= THRESHOLD * (1 - THRESHOLD_VM)) {
										if (rdmop.hasOnlyOneVirtualMachine()) {
											logMessage(this.toString() + " : Do adaptation - Can't release VM because only one VM is available");
										}
										else {
											logMessage(this.toString() + " : Do adaptation - Release VM");
											releaseVm(selectRandomVm());
										}
									}
									// Release core
									else if (movingAvg <= THRESHOLD * (1 - THRESHOLD_CORE)) {
										// Select a random vm
										final ApplicationVMManagementOutboundPort vm = selectRandomVm();
										final ApplicationVMData vmData = avmmop.get(vm);
										if (vmData.hasOnlyOneCore()) {
											logMessage(this.toString() + " : Do adaptation - Can't release core because only one core is available");
										}
										else {
											logMessage(this.toString() + " : Do adaptation - Release core");
											// Release the core
											releaseCore(vm, vmData.selectRandomCore());
										}
									}
									// Decrease frequency
									else if (movingAvg <= THRESHOLD * (1 - THRESHOLD_FREQUENCY)) {
										logMessage(this.toString() + " : Do adaptation - Decrease frequency");
										// Select a random vm
										final ApplicationVMManagementOutboundPort vm = selectRandomVm();
										
										// Select a random core
										final AllocatedCore ac = avmmop.get(vm).selectRandomCore();
										
										// Decrease its frequency
										cmop.changeFrequency(ac, cmop.getCurrentFrequency(ac) - DELTA_FREQUENCY);
									}
									// In other case do nothing
									else {
										logMessage(this.toString() + " : No adaptation needed");
									}
								}
							}
						}
						catch (Exception e) {
							e.printStackTrace();
							throw new RuntimeException(e);
						}
						try {
							// Schedule another adaptation
							doPeriodicAdaptation();
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, PERIODIC_INTERVAL_ADAPTATION, TimeUnit.MILLISECONDS);
	}

	/** 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "autonomic controller '" + this.URI + "'";
	}
	
}
