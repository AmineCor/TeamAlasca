package com.teamalasca.computer.connectors;

import com.teamalasca.computer.interfaces.CoreManager;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

public class ManageCoreConnector extends AbstractConnector implements CoreManager {

	@Override
	public AllocatedCore allocateCore() throws Exception {
		return ((CoreManager)this.offering).allocateCore();
	}

	@Override
	public AllocatedCore[] allocateCores(int nbCores) throws Exception {
		return ((CoreManager)this.offering).allocateCores(nbCores);
	}

	@Override
	public void releaseCore(AllocatedCore core) throws Exception {
		((CoreManager)this.offering).releaseCore(core);
	}

	@Override
	public void releaseCores(AllocatedCore[] cores) throws Exception {
		((CoreManager)this.offering).releaseCores(cores);	
	}

	@Override
	public void changeFrequency(AllocatedCore core, int frequency) throws Exception {
		((CoreManager)this.offering).changeFrequency(core, frequency);
	}

}
