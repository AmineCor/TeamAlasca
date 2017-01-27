package com.teamalasca.autonomiccontroller;

import java.util.ArrayList;
import java.util.List;

import com.teamalasca.computer.connectors.ManageCoreConnector;
import com.teamalasca.computer.interfaces.CoreManager;
import com.teamalasca.computer.ports.ManageCoreOutboundPort;
import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import com.teamalasca.requestdispatcher.interfaces.RequestDispatcherStateDataConsumerI;
import com.teamalasca.requestdispatcher.ports.RequestDispatcherDynamicStateDataOutboundPort;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ports.AbstractPort;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStateDataConsumerI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;

public final class AutonomicController extends AbstractComponent
implements RequestDispatcherStateDataConsumerI, ComputerStateDataConsumerI {

	private final static int MAX_SIZE_AVERAGE_LIST = 3;

	/** A private URI to identify this autonomic controller, for debug purpose */
	private final String URI;

	/** URI of the dispatcher associated with this autonomic controller */
	private String requestDispatcherURI;

	/** Outbound port connected to the request dispatcher component to receive its data */
	private RequestDispatcherDynamicStateDataOutboundPort rddsdop;

	/** Outbound port connected to the computer component to receive its data */
	private ComputerDynamicStateDataOutboundPort cdsdop ;

	/** Outbound port connected to the computer to manage its cores **/
	private ManageCoreOutboundPort mcop ;

	private List<Double> requestExecutionAverages;
	
	private Actuator actuator;

	private boolean[][] ressources;

	public AutonomicController(String autonomicControllerURI,
			final String computerURI,
			final String computerDynamicStateDataInboundPortURI,
			final String manageCoreInboundPortURI,
			final String requestDispatcherURI,
			final String requestDispatcherDynamicStateDataInboundPortURI) throws Exception
	{
		super(1, 1);

		this.URI = autonomicControllerURI;
		requestExecutionAverages = new ArrayList<>();
		actuator = new Actuator();

		/*// create port to access computer services
		this.csop = new ComputerServicesOutboundPort(this) ;
		this.addRequiredInterface(ComputerServicesI.class);
		this.addPort(this.csop);
		this.csop.publishPort() ;
		this.csop.doConnection(computerServiceInboundPortURI, ComputerServicesConnector.class.getCanonicalName());*/

		// Connect the Request Dispatcher and the Autonomic Controller
		doConnectionWithRequestDispatcher(requestDispatcherURI, requestDispatcherDynamicStateDataInboundPortURI);

		// create port to receive computer data
		doConnectionWithComputer(computerURI, computerDynamicStateDataInboundPortURI,manageCoreInboundPortURI);		
	}
	
	public AutonomicController(
			final String computerURI,
			final String computerDynamicStateDataInboundPortURI,
			final String manageCoreInboundPortURI,
			final String requestDispatcherURI,
			final String requestDispatcherDynamicStateDataInboundPortURI) throws Exception{
		this(AbstractPort.generatePortURI(), computerURI, computerDynamicStateDataInboundPortURI, manageCoreInboundPortURI, requestDispatcherURI, requestDispatcherDynamicStateDataInboundPortURI);
	}

	/** Connecting the autonomic controller with the request dispatcher */
	public void doConnectionWithRequestDispatcher(final String requestDispatcherURI,final String requestDispatcherDynamicStateDataInboundPortURI) throws Exception
	{
		this.requestDispatcherURI = requestDispatcherURI;
		this.rddsdop = new RequestDispatcherDynamicStateDataOutboundPort(this, requestDispatcherURI);
		this.addPort(rddsdop);
		this.rddsdop.publishPort();
		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class) ;
		this.rddsdop.doConnection(
				requestDispatcherDynamicStateDataInboundPortURI,
				ControlledDataConnector.class.getCanonicalName());
		this.rddsdop.startUnlimitedPushing(200);
	}

	/** Connecting the admission controller with ports of a computer component */
	private void doConnectionWithComputer(final String computerURI, final String computerDynamicStateDataInboundPortURI,final String manageCoreInboundPortURI) throws Exception
	{

		// create port to receive computer data
		this.cdsdop = new ComputerDynamicStateDataOutboundPort(this, computerURI);
		this.addPort(cdsdop) ;
		this.cdsdop.publishPort() ;
		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class) ;
		this.cdsdop.
		doConnection(
				computerDynamicStateDataInboundPortURI,
				ControlledDataConnector.class.getCanonicalName()) ;
		this.cdsdop.startUnlimitedPushing(200);

		// create port to allocate and manage computer cores
		this.mcop = new ManageCoreOutboundPort(this);
		this.addRequiredInterface(CoreManager.class);
		this.addPort(this.mcop);
		this.mcop.publishPort();
		this.mcop.doConnection(manageCoreInboundPortURI, ManageCoreConnector.class.getCanonicalName());
	}

	@Override
	public String toString()
	{
		return "autonomic controller '"+this.URI+"'";
	}

	@Override
	public void acceptComputerStaticData(String computerURI,
			ComputerStaticStateI staticState) throws Exception
	{		
	}

	@Override
	public void acceptComputerDynamicData(String computerURI,
			ComputerDynamicStateI currentDynamicState) throws Exception
	{
		this.logMessage(this.toString() + "received a message from a computer");

		this.ressources = currentDynamicState.getCurrentCoreReservations();
	}

	@Override
	public void acceptRequestDispatcherDynamicData(String dispatcherURI,
			RequestDispatcherDynamicStateI currentDynamicState)
					throws Exception
	{
		this.logMessage(this.toString() + "received a message from a request dispatcher");

		if(dispatcherURI != requestDispatcherURI) // data received from an unknown dispatcher
			return;

		synchronized (requestExecutionAverages) {
			requestExecutionAverages.add(currentDynamicState.getRequestExecutionTimeAverage());

			// For moving average, we only need 3 values
			while (requestExecutionAverages.size() > MAX_SIZE_AVERAGE_LIST) {
				requestExecutionAverages.remove(0);
			}
		}
		
		this.actuator.adaptRessources(mcop, computeMovingAverage());
		
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
			for (double i : requestExecutionAverages)
			{
				result += i;
			}
			result = result / requestExecutionAverages.size();
			
			logMessage(this.toString() + " Moving average = " + result);
			return result;
		}
	}

}
