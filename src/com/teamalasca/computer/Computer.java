package com.teamalasca.computer;

import java.util.Map;
import java.util.Set;

import com.teamalasca.computer.interfaces.CoreManager;
import com.teamalasca.computer.ports.ManageCoreInboundPort;

import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorManagementConnector;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorManagementI;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;

public class Computer extends fr.upmc.datacenter.hardware.computers.Computer implements CoreManager{

	private ManageCoreInboundPort mcip;
	private ProcessorManagementOutboundPort pmop;
	
	public Computer(String computerURI, Set<Integer> possibleFrequencies,
			Map<Integer, Integer> processingPower, int defaultFrequency,
			int maxFrequencyGap, int numberOfProcessors, int numberOfCores,
			String computerServicesInboundPortURI,
			String computerStaticStateDataInboundPortURI,
			String computerDynamicStateDataInboundPortURI,String manageCoreInboundPortURI) throws Exception {
		super(computerURI, possibleFrequencies, processingPower, defaultFrequency,
				maxFrequencyGap, numberOfProcessors, numberOfCores,
				computerServicesInboundPortURI, computerStaticStateDataInboundPortURI,
				computerDynamicStateDataInboundPortURI);
		
		
		this.mcip = new ManageCoreInboundPort(manageCoreInboundPortURI,this);
		this.addPort(mcip);
		this.mcip.publishPort();
		this.addOfferedInterface(CoreManager.class);
		
		this.pmop = new ProcessorManagementOutboundPort(this);
		this.addPort(this.pmop);
		this.pmop.publishPort();
		this.addRequiredInterface(ProcessorManagementI.class);
		
	}

	@Override
	public void changeFrequency(AllocatedCore core, int frequency) throws Exception {
		String portUri = core.processorInboundPortURI.get(Processor.ProcessorPortTypes.MANAGEMENT);
		pmop.doConnection(portUri, ProcessorManagementConnector.class.getCanonicalName());
		pmop.setCoreFrequency(core.coreNo, frequency);
		pmop.doDisconnection();
	}
	

}
