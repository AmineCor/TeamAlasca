package com.teamalasca.autonomiccontroller;

import com.teamalasca.computer.ports.ManageCoreOutboundPort;

final class Actuator {

	private static final double THRESHOLD = 3000.0;
	private static final double THRESHOLD_FREQUENCY = 0.20;
	private static final double THRESHOLD_CORE = 0.40;
	private static final double THRESHOLD_VM = 0.80;

	final void adaptRessources(ManageCoreOutboundPort manageCoreOutboundPort,Double executionTime){

		if(executionTime > THRESHOLD)
			incresaseRessources(manageCoreOutboundPort,executionTime);
		else
			decreaseRessources(manageCoreOutboundPort,executionTime);
	}

	private void incresaseRessources(
			ManageCoreOutboundPort manageCoreOutboundPort, Double executionTime) {
		double delta = THRESHOLD - executionTime;

		if(delta > THRESHOLD_VM){
	
		}
		else if(delta > THRESHOLD_CORE){

		}
		else if(delta > THRESHOLD_FREQUENCY){
			manageCoreOutboundPort.changeFrequency(core, frequency);
		}

	}

	private void decreaseRessources(
			ManageCoreOutboundPort manageCoreOutboundPort, Double executionTime) {
		double delta = executionTime - THRESHOLD;
		if(delta > THRESHOLD_VM){

		}
		else if(delta > THRESHOLD_CORE){

		}
		else if(delta > THRESHOLD_FREQUENCY){

		}

	}

}
