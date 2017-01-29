package com.teamalasca.computer.connectors;

import com.teamalasca.computer.interfaces.CoreManagementI;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

public class CoreManagementConnector
extends AbstractConnector
implements CoreManagementI
{

	@Override
	public AllocatedCore allocateCore() throws Exception
	{
		return ((CoreManagementI) this.offering).allocateCore();
	}

	@Override
	public AllocatedCore[] allocateCores(int nbCores) throws Exception
	{
		return ((CoreManagementI) this.offering).allocateCores(nbCores);
	}

	@Override
	public void releaseCore(AllocatedCore core) throws Exception
	{
		((CoreManagementI) this.offering).releaseCore(core);
	}

	@Override
	public void releaseCores(AllocatedCore[] cores) throws Exception
	{
		((CoreManagementI) this.offering).releaseCores(cores);	
	}

	@Override
	public void changeFrequency(AllocatedCore core, int frequency) throws Exception
	{
		((CoreManagementI) this.offering).changeFrequency(core, frequency);
	}

	@Override
	public int getCurrentFrequency(AllocatedCore core) throws Exception
	{
		return ((CoreManagementI) this.offering).getCurrentFrequency(core);
	}

}
