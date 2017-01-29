package com.teamalasca.computer;

import java.util.Map;
import java.util.Set;

import com.teamalasca.computer.interfaces.CoreManagementI;
import com.teamalasca.computer.ports.CoreManagementInboundPort;

import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorManagementConnector;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorManagementI;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;

public class Computer
extends fr.upmc.datacenter.hardware.computers.Computer
implements CoreManagementI
{

	final private CoreManagementInboundPort cmip;
	final private ProcessorManagementOutboundPort pmop;
	
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
		
		this.cmip = new CoreManagementInboundPort(coreManagerInboundPortURI,this);
		this.addPort(cmip);
		this.cmip.publishPort();
		this.addOfferedInterface(CoreManagementI.class);
		
		this.pmop = new ProcessorManagementOutboundPort(this);
		this.addPort(this.pmop);
		this.pmop.publishPort();
		this.addRequiredInterface(ProcessorManagementI.class);
	}

	@Override
	public void changeFrequency(final AllocatedCore core, final int frequency) throws Exception {
		final String portUri = core.processorInboundPortURI.get(Processor.ProcessorPortTypes.MANAGEMENT);
		pmop.doConnection(portUri, ProcessorManagementConnector.class.getCanonicalName());
		pmop.setCoreFrequency(core.coreNo, frequency);
		pmop.doDisconnection();
	}

}
