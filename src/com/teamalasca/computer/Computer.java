package com.teamalasca.computer;

import java.util.Map;
import java.util.Set;

import com.teamalasca.computer.interfaces.CoreManagementI;
import com.teamalasca.computer.ports.CoreManagementInboundPort;

import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.upmc.datacenter.hardware.processors.UnavailableFrequencyException;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorManagementConnector;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorManagementI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStaticStateI;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;

/**
 * The class <code>Computer</code> implements a component that represents a
 * computer in a data center.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Cl�ment George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class Computer
extends fr.upmc.datacenter.hardware.computers.Computer
implements CoreManagementI
{
	/** Core management inbound port. */
	final private CoreManagementInboundPort cmip;

	/** Processor management outbound port. */
	final private ProcessorManagementOutboundPort pmop;

	/** Current frequencies for each core. */
	private int[] currentCoreFrequencies;
	private final Set<Integer> possibleFrequencies;

	/**
	 * Construct a <code>Computer</code>.
	 * 
	 * @param computerURI the computer URI.
	 * @param possibleFrequencies the possible frequencies for cores.
	 * @param processingPower the Mips for the different possible frequencies.
	 * @param defaultFrequency the default frequency at which the cores run.
	 * @param maxFrequencyGap the max frequency gap among cores of the same processor.
	 * @param numberOfProcessors the number of processors in the computer.
	 * @param numberOfCores the number of cores per processor (homogeneous).
	 * @param computerServicesInboundPortURI the URI of the computer service inbound port.
	 * @param computerStaticStateDataInboundPortURI the URI of the computer static data notification inbound port.
	 * @param computerDynamicStateDataInboundPortURI the URI of the computer dynamic data notification inbound port.
	 * @param coreManagerInboundPortURI the core management inbound port URI.
	 * @throws Exception throws an exception if an error occured..
	 */
	public Computer(
			final String computerURI,
			final Set<Integer> possibleFrequencies,
			final Map<Integer, Integer> processingPower,
			final int defaultFrequency,
			final int maxFrequencyGap,
			final int numberOfProcessors,
			final int numberOfCores,
			final String computerServicesInboundPortURI,
			final String computerStaticStateDataInboundPortURI,
			final String computerDynamicStateDataInboundPortURI,
			final String coreManagerInboundPortURI)
					throws Exception {
		super(
				computerURI,
				possibleFrequencies,
				processingPower,
				defaultFrequency,
				maxFrequencyGap,
				numberOfProcessors,
				numberOfCores,
				computerServicesInboundPortURI,
				computerStaticStateDataInboundPortURI,
				computerDynamicStateDataInboundPortURI);

		this.possibleFrequencies = possibleFrequencies;

		// connect ports
		this.cmip = new CoreManagementInboundPort(coreManagerInboundPortURI,this);
		this.addPort(cmip);
		this.cmip.publishPort();
		this.addOfferedInterface(CoreManagementI.class);

		this.pmop = new ProcessorManagementOutboundPort(this);
		this.addPort(this.pmop);
		this.pmop.publishPort();
		this.addRequiredInterface(ProcessorManagementI.class);

		// get data from processors
		for (final Processor p : processors) {
			p.startUnlimitedPushing(500);
		}
	}

	/** 
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStateDataConsumerI#acceptProcessorStaticData(java.lang.String, fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStaticStateI)
	 */
	@Override
	public void acceptProcessorStaticData(String processorURI, ProcessorStaticStateI ss) throws Exception
	{
	}

	/** 
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStateDataConsumerI#acceptProcessorDynamicData(java.lang.String, fr.upmc.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI)
	 */
	@Override
	public void acceptProcessorDynamicData(String processorURI, ProcessorDynamicStateI cds) throws Exception
	{
		currentCoreFrequencies = cds.getCurrentCoreFrequencies();
	}

	/**
	 * @throws Exception 
	 * @see com.teamalasca.computer.interfaces.CoreManagementI#changeFrequency(fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore, int)
	 */
	@Override
	public void changeFrequency(final AllocatedCore core, final int frequency) throws Exception
	{
		// connect
		pmop.doConnection(
				core.processorInboundPortURI.get(Processor.ProcessorPortTypes.MANAGEMENT),
				ProcessorManagementConnector.class.getCanonicalName());

		// update frequency
		pmop.setCoreFrequency(core.coreNo, frequency);

		// disconnect
		pmop.doDisconnection();
	}

	/**
	 * @see com.teamalasca.computer.interfaces.CoreManagementI#getCurrentFrequency(fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore)
	 */
	@Override
	public int getCurrentFrequency(AllocatedCore core) throws Exception
	{
		return currentCoreFrequencies[core.coreNo];
	}

	/** 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "computer '" + this.computerURI + "'";
	}

	@Override
	public void increaseFrequency(AllocatedCore core) throws UnacceptableFrequencyException,Exception {
		// connect
		pmop.doConnection(
				core.processorInboundPortURI.get(Processor.ProcessorPortTypes.MANAGEMENT),
				ProcessorManagementConnector.class.getCanonicalName());

		
		int currentFrequency = currentCoreFrequencies[core.coreNo];
		int frequency = this.getUpperFrequency(currentFrequency);
		
		
		// update frequency
		pmop.setCoreFrequency(core.coreNo, frequency);

		// disconnect
		pmop.doDisconnection();

	}
	
	@Override
	public void decreaseFrequency(AllocatedCore core) throws UnacceptableFrequencyException,Exception {
		// connect
		pmop.doConnection(
				core.processorInboundPortURI.get(Processor.ProcessorPortTypes.MANAGEMENT),
				ProcessorManagementConnector.class.getCanonicalName());

		
		int currentFrequency = currentCoreFrequencies[core.coreNo];
		int frequency = this.getLowerFrequency(currentFrequency);
		
		System.out.println("frequency: " + frequency);
		
		// update frequency
		pmop.setCoreFrequency(core.coreNo, frequency);

		// disconnect
		pmop.doDisconnection();

	}
	
	/**
	 * Given a frequency, returns the next possible frequency from the range of the possible upper frequencies.
	 * @param currentFrequency : the current frequency
	 * @return the next possible frequency, -1 if the current frequency is already the greatest.
	 */
	private int getUpperFrequency(int currentFrequency) {
		int res = Integer.MAX_VALUE;
		for(Integer possibleFrequency:possibleFrequencies){
			if(possibleFrequency > currentFrequency && possibleFrequency < res)
				res = possibleFrequency;
		}
		if(res == Integer.MAX_VALUE)
			return -1;
		return res;
	}
	
	/**
	 * Given a frequency, returns the next possible frequency from the range of the possible lower frequencies.
	 * @param currentFrequency : the current frequency
	 * @return the next possible frequency, -1 if the current frequency is already the lowest.
	 */
	private int getLowerFrequency(int currentFrequency) {
		int res = Integer.MIN_VALUE;
		for(Integer possibleFrequency:possibleFrequencies){
			if(possibleFrequency < currentFrequency && possibleFrequency > res)
				res = possibleFrequency;
		}
		if(res == Integer.MIN_VALUE)
			return -1;
		return res;
	}

}
