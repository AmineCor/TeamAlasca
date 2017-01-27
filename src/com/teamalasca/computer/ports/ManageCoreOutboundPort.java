package com.teamalasca.computer.ports;

import com.teamalasca.computer.interfaces.CoreManager;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

public class ManageCoreOutboundPort extends AbstractOutboundPort implements CoreManager {

	public ManageCoreOutboundPort( String uri,ComponentI owner) throws Exception {
		super(uri,CoreManager.class, owner);
	}

	public ManageCoreOutboundPort(ComponentI owner) throws Exception {
		super(CoreManager.class, owner);
	}

	@Override
	public AllocatedCore allocateCore() throws Exception {
		return ((CoreManager)this.connector).allocateCore() ;
	}

	@Override
	public void releaseCore(AllocatedCore core) throws Exception {
		((CoreManager)this.connector).releaseCore(core);
		
	}

	@Override
	public void changeFrequency(AllocatedCore core, int frequency) throws Exception {
		((CoreManager)this.connector).changeFrequency(core, frequency);
		
	}

	@Override
	public void releaseCores(AllocatedCore[] cores) throws Exception {
		((CoreManager)this.connector).releaseCores(cores);
	}

	@Override
	public AllocatedCore[] allocateCores(int nbCores) throws Exception {
		return ((CoreManager)this.connector).allocateCores(nbCores) ;

	}

}

