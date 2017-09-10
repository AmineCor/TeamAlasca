package com.teamalasca.autonomiccontroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.teamalasca.autonomiccontroller.interfaces.AutonomicControllerNotificationI;
import com.teamalasca.autonomiccontroller.interfaces.AutonomicControllerServicesI;
import com.teamalasca.autonomiccontroller.ports.AutonomicControllerServicesInboundPort;
import com.teamalasca.autonomiccontroller.state.AllocatedResources;
import com.teamalasca.autonomiccontroller.state.AllocatedVirtualMachine;
import com.teamalasca.computer.connectors.CoreManagementConnector;
import com.teamalasca.computer.interfaces.CoreManagementI;
import com.teamalasca.computer.ports.CoreManagementOutboundPort;
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
import fr.upmc.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;

/**
 * The class <code>AutonomicController</code> is a component to perform
 * adaptations depending of the execution times of the requests.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Cl�ment George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public final class AutonomicController
extends AbstractComponent
implements AutonomicControllerServicesI, AutonomicControllerNotificationI,
RequestDispatcherStateDataConsumerI
{
	/** List of execution time (in milliseconds) averages from the request dispatcher to compute the moving average. */
	private List<Double> requestExecutionAverages;

	/** The "N" value to compute moving average. */
	private final static int MAX_SIZE_AVERAGE_LIST = 3;

	/** Default core number per VM */
	public final static int DEFAULT_CORE_NUMBER = 4;

	/** The aimed time we want to reach */
	private static final double AIMED_AVERAGE  = 1000D;

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

	/** Local information about the allocated resources */
	private final AllocatedResources state;
	
	/** This list stores references to the machines in waiting to be destroyed */
	private final List<AllocatedVirtualMachine> machinesToDestroy = new ArrayList<>();

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

		// Allocate a first VM to the request dispatcher.
		AllocatedVirtualMachine avm = this.allocateVm(DEFAULT_CORE_NUMBER);
		this.state = new AllocatedResources(avm);
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
		this.logMessage(this.toString() + "received a message from a request dispatcher: average=" + currentDynamicState.getRequestExecutionTimeAverage());

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
	 * Allocate a new virtual machine with as much cores as specified
	 * @return an image of the virtual machine, null if an error occurred during
	 * the allocation.
	 */
	private AllocatedVirtualMachine allocateVm(int coreNumber){

		try{

			//------------- Create the application virtual machine ------------------/
			final String vmApplicationVMManagementInboundPortURI = AbstractPort.generatePortURI();
			final String vmRequestSubmissionInboundPortURI = AbstractPort.generatePortURI();
			final String vmRequestNotificationOutboundPortURI = AbstractPort.generatePortURI();
			final String vmURI = AbstractPort.generatePortURI();

			final ApplicationVM vm = new ApplicationVM(vmURI,
					vmApplicationVMManagementInboundPortURI,
					vmRequestSubmissionInboundPortURI,
					vmRequestNotificationOutboundPortURI);

			//vm.toggleTracing();
			//vm.toggleLogging();

			// create port to manage the application vm associated with the request dispatcher
			final ApplicationVMManagementOutboundPort appVMManagementOutboundPort = new ApplicationVMManagementOutboundPort(AbstractPort.generatePortURI(), this);
			this.addPort(appVMManagementOutboundPort);
			appVMManagementOutboundPort.publishPort();
			appVMManagementOutboundPort.doConnection(vmApplicationVMManagementInboundPortURI, ApplicationVMManagementConnector.class.getCanonicalName());

			//Try to allocate cores to the new virtual machine
			List<AllocatedCore> cores = this.allocateCores(appVMManagementOutboundPort, coreNumber);

			if(cores == null){
				return null; //return null if no core has been allocated.
			}

			// ------- Connect the request dispatcher with the application virtual machine ------/
			this.rdmop.associateVirtualMachine(vmURI,vmRequestSubmissionInboundPortURI);
			vm.findPortFromURI(vmRequestNotificationOutboundPortURI).doConnection(
					this.requestDispatcherRequestNotificationInboundPortURI,
					RequestNotificationConnector.class.getCanonicalName());

			//--- Create the view of allocated resources ---------/
			AllocatedVirtualMachine virtualMachine = new AllocatedVirtualMachine(vmURI,vmRequestSubmissionInboundPortURI);
			virtualMachine.addCores(cores);
			return virtualMachine;

		}catch(Exception e){
			return null;
		}
	}

	/**
	 * Release a virtual machine.
	 * 
	 * @param the virtual machine to be released
	 * @throws Exception throws an exception if an error occured..
	 */
	private void releaseVirtualMachine(final AllocatedVirtualMachine machine)
	{
		
		// store the reference to the machine in a specific list
		this.machinesToDestroy.add(machine);
		
		// dissociate vm from the request dispatcher,
		// the request dispatcher will notify the autonomic controller when the virtual machine will end 
		// executing its last requests, thus will be ready to be destroyed
		try {
			this.rdmop.dissociateVirtualMachine(machine.getURI());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		
		// clear the machine in the local information about allocated resources
		this.state.removeVirtualMachine(machine);
	}

	/**
	 * Liberate the resources tied with the virtual machine given in parameter
	 * @param machine : a machine to get free.
	 */
	private void freeResources(AllocatedVirtualMachine machine) {
		List<AllocatedCore> cores = machine.getCores();
		for(AllocatedCore core:cores){
			this.releaseCore(core);
		}
	}

	/**
	 * Allocate cores.
	 * 
	 * @param AVMManagementOutboundPort the application virtual machine management outbound port.
	 * @param nbCores the number of cores to allocate.
	 * @return null if an error occurred.
	 */
	private List<AllocatedCore> allocateCores(final ApplicationVMManagementOutboundPort AVMManagementOutboundPort, int nbCores)
	{
		try{
			// allocate its cores
			AllocatedCore[] cores = this.cmop.allocateCores(DEFAULT_CORE_NUMBER);
			AVMManagementOutboundPort.allocateCores(cores);

			List<AllocatedCore> res = new ArrayList<>();
			for(AllocatedCore core:cores){
				if(core != null){
					res.add(core);
				}
			}
			if(res.isEmpty())
				return null;

			return res;
		}
		catch(Exception e){
			return null;
		}
	}

	/**
	 * Release a core.
	 * @param ac the allocated core to release.
	 */
	private void releaseCore(final AllocatedCore ac)
	{
		// release core
		try {
			this.cmop.releaseCore(ac);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Compute the moving average.
	 * 
	 * @return the moving average.
	 */
	public Long computeMovingAverage()
	{
		synchronized (requestExecutionAverages) {
			// Need to check if we at least got an average
			if (requestExecutionAverages.isEmpty()) {
				return null;
			}

			// Compute moving average
			long result = 0L;
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
	public void doPeriodicAdaptation()
	{
		this.scheduleTask(
				new ComponentI.ComponentTask() {
					@Override
					public void run() {

						logMessage("state before adaptation: " + state.toString());

						// Compute moving average
						final Long movingAvg = computeMovingAverage();

						// Check this value it's not null, it can happen at the beginning of the execution
						if (movingAvg != null) {

							if (movingAvg > AIMED_AVERAGE) { // We are above the THRESHOLD

								int vmNeeded = (int) ((movingAvg * state.getVirtualMachineNumber()) / AIMED_AVERAGE);
								System.out.println(String.format("movingAvg : %d, vmNeeded: %d", movingAvg,vmNeeded));

								if(vmNeeded > 0){ // add as much virtual machines as needed.
									int i = 0;
									for(;i<vmNeeded;i++){
										AllocatedVirtualMachine avm = allocateVm(state.getBaseVM().getCoreNumber());
										if(avm == null){
											break;
										}
										state.addVirtualMachine(avm);
									}
									logMessage(String.format("%s: %d virtual machines added",this.toString(), i));
								}
							}
							else {

								int extraVms = (int) ((movingAvg * state.getVirtualMachineNumber()) / AIMED_AVERAGE);
								System.out.println(String.format("movingAvg : %d, extraVms: %d", movingAvg,extraVms));

								if(extraVms > 0){
									int i = 0;
									for(;i<extraVms && state.getVirtualMachineNumber() > 0;i++){
										AllocatedVirtualMachine avm = state.getLastVirtualMachine();
										if(avm == null){
											break;
										}
										releaseVirtualMachine(avm);
									}
									logMessage(String.format("%s: %d virtual machines removed",this.toString(), i));
								}
							}
						}

						logMessage("state after adaptation:" + state.toString());
						// Schedule another adaptation
						doPeriodicAdaptation();

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

	/**
	 * Increase the frequency of a random core, returns true if success.
	 */
	private boolean increaseFrequency() throws Exception {
		// Select a random vm
		final ApplicationVMManagementOutboundPort vm = null;

		// Select a random core
		final AllocatedCore ac = null;

		// Increase its frequency
		try{
			cmop.increaseFrequency(ac);
			return true;
		}
		catch(UnacceptableFrequencyException ex){
			System.err.println("unaceptable");
			return false;
		}

	}

	/**
	 * Decrease the frequency of a random core, returns true if success.
	 */
	private boolean decreaseFrequency() throws Exception {
		// Select a random vm
		final ApplicationVMManagementOutboundPort vm = null;

		// Select a random core
		final AllocatedCore ac = null;

		// Decrease its frequency
		try{
			cmop.decreaseFrequency(ac);
			return true;
		}
		catch(UnacceptableFrequencyException ex){
			System.err.println("unaceptable");
			return false;
		}

	}

	@Override
	public void notifyVirtualMachineUsageTermination(String aVirtualMachineURI) {
		for(AllocatedVirtualMachine machine:machinesToDestroy){
			if(machine.equals(aVirtualMachineURI)){
				freeResources(machine);
			}
		}	
		
	}

}
