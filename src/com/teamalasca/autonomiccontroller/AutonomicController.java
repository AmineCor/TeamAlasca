package com.teamalasca.autonomiccontroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.teamalasca.autonomiccontroller.interfaces.AutonomicControllerServicesI;
import com.teamalasca.autonomiccontroller.ports.AutonomicControllerServicesInboundPort;
import com.teamalasca.computer.connectors.ManageCoreConnector;
import com.teamalasca.computer.interfaces.CoreManager;
import com.teamalasca.computer.ports.ManageCoreOutboundPort;
import com.teamalasca.requestdispatcher.connectors.RequestDispatcherManagementConnector;
import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherStateDataConsumerI;
import com.teamalasca.requestdispatcher.ports.RequestDispatcherDynamicStateDataOutboundPort;
import com.teamalasca.requestdispatcher.ports.RequestDispatcherManagementOutboundPort;

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

public final class AutonomicController
extends AbstractComponent
implements AutonomicControllerServicesI,
		   RequestDispatcherStateDataConsumerI
{

	private final static int MAX_SIZE_AVERAGE_LIST = 3;
	private final static int DEFAULT_CORE_NUMBER = 4;
	
	private static final double THRESHOLD = 3000.0;
	private static final double THRESHOLD_FREQUENCY = 0.20;
	private static final double THRESHOLD_CORE = 0.40;
	private static final double THRESHOLD_VM = 0.80;

	private static final int PERIODIC_INTERVAL = 25000;
	

	/** A private URI to identify this autonomic controller, for debug purpose */
	private final String URI;

	/** URI of the dispatcher associated with this autonomic controller */
	private String requestDispatcherURI;
	
	/** URI of the dispatcher associated with this autonomic controller */
	private String requestDispatcherRequestNotificationInboundPortURI;

	/** Inbound port offering the management interface.	*/
	private AutonomicControllerServicesInboundPort acsip;
	
	/** Outbound port connected to the request dispatcher management */
	private RequestDispatcherManagementOutboundPort rdmop;
	
	/** Outbound port connected to the request dispatcher component to receive its data */
	private RequestDispatcherDynamicStateDataOutboundPort rddsdop;

	/** Outbound port connected to the computer component to receive its data */
	private ComputerDynamicStateDataOutboundPort cdsdop;

	/** Outbound port connected to the computer to manage its cores **/
	private ManageCoreOutboundPort mcop;

	/** Internal port to manage the application virtual machines allocated. In specially, allocate its cores */
	private ApplicationVMManagementOutboundPort avmmop;

	/** List of averages from the request dispatcher to compute the moving average */
	private List<Double> requestExecutionAverages;

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
		this.mcop = new ManageCoreOutboundPort(this);
		this.addRequiredInterface(CoreManager.class);
		this.addPort(this.mcop);
		this.mcop.publishPort();

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
		initCores();
	}

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

	/** Connecting the autonomic controller with the request dispatcher */
	public void doConnectionWithRequestDispatcher(
			final String requestDispatcherURI,
			final String requestDispatcherDynamicStateDataInboundPortURI)
					throws Exception
	{
		this.requestDispatcherURI = requestDispatcherURI;
		this.rddsdop.doConnection(
				requestDispatcherDynamicStateDataInboundPortURI,
				ControlledDataConnector.class.getCanonicalName());
		
		// Push average from request dispatcher every 10 seconds
		this.rddsdop.startUnlimitedPushing(300);
	}

	/** Connecting the admission controller with ports of a computer component */
	private void doConnectionWithComputer(final String computerURI, final String computerDynamicStateDataInboundPortURI,final String manageCoreInboundPortURI) throws Exception
	{
		this.mcop.doConnection(manageCoreInboundPortURI, ManageCoreConnector.class.getCanonicalName());
	}

	@Override
	public String toString()
	{
		return "autonomic controller '" + this.URI + "'";
	}

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
			requestExecutionAverages.add(currentDynamicState.getRequestExecutionTimeAverage());

			// For moving average, we only need 3 values
			while (requestExecutionAverages.size() > MAX_SIZE_AVERAGE_LIST) {
				requestExecutionAverages.remove(0);
			}
		}
	}

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
		this.avmmop = new ApplicationVMManagementOutboundPort(AbstractPort.generatePortURI(), this);
		this.addPort(avmmop);
		this.avmmop.publishPort();
		this.avmmop.doConnection(AVMApplicationVMManagementInboundPortURI, ApplicationVMManagementConnector.class.getCanonicalName());
		
		// ------- Connect the request dispatcher with the application virtual machine ------/
		this.rdmop.associateVirtualMachine(AVMRequestSubmissionInboundPortURI);
		vm.findPortFromURI(AVMRequestNotificationOutboundPortURI).doConnection(
				this.requestDispatcherRequestNotificationInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName());
	}
	
	private void initCores() throws Exception
	{
		// allocate its cores
		AllocatedCore[] ac = this.mcop.allocateCores(DEFAULT_CORE_NUMBER);
		this.avmmop.allocateCores(ac);
	}

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
										logMessage(this.toString() + " : Allocate VM");

										//   allocateVM();			
									}
									// Allocate core
									else if (movingAvg > THRESHOLD * (1 + THRESHOLD_CORE)) {
										logMessage(this.toString() + " : Allocate core");

										//   allocateCore(getLessEffectiveVM(infos));
									}
									// Increase frequency
									else if (movingAvg > THRESHOLD * (1 + THRESHOLD_FREQUENCY)) {
										logMessage(this.toString() + " : Increase frequency");

										// changeFrequency(getLessEffectiveVM(infos),Frequency.up);
									}
									// In other case do nothing
									else {
										logMessage(this.toString() + " : No adaptation needed");
									}
								}
								else {
									// Release VM
									if (movingAvg <= THRESHOLD * (1 - THRESHOLD_VM)) {
										logMessage(this.toString() + " : Release VM");

										//releaseVM(getMostEffectiveVM(infos));
									}
									// Release core
									else if (movingAvg <= THRESHOLD * (1 - THRESHOLD_CORE)) {
										logMessage(this.toString() + " : Release core");

										//releaseCore(getMostEffectiveVM(infos));
									}
									// Decrease frequency
									else if (movingAvg <= THRESHOLD * (1 - THRESHOLD_FREQUENCY)) {
										logMessage(this.toString() + " : Decrease frequency");

										//changeFrequency(getMostEffectiveVM(infos),Frequency.down);
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
				}, PERIODIC_INTERVAL, TimeUnit.MILLISECONDS);
	}

}