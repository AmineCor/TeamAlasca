package com.teamalasca.computer.ports;

import com.teamalasca.computer.interfaces.CoreManagementI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

public class CoreManagementOutboundPort
extends AbstractOutboundPort
implements CoreManagementI
{

	public CoreManagementOutboundPort( String uri,ComponentI owner) throws Exception
	{
		super(uri, CoreManagementI.class, owner);
	}

	public CoreManagementOutboundPort(ComponentI owner) throws Exception
	{
		super(CoreManagementI.class, owner);
	}

	@Override
	public AllocatedCore allocateCore() throws Exception
	{
		return ((CoreManagementI) this.connector).allocateCore();
	}

	@Override
	public void releaseCore(AllocatedCore core) throws Exception
	{
		((CoreManagementI) this.connector).releaseCore(core);
	}

	@Override
	public void changeFrequency(AllocatedCore core, int frequency) throws Exception
	{
		((CoreManagementI) this.connector).changeFrequency(core, frequency);
	}

	@Override
	public void releaseCores(AllocatedCore[] cores) throws Exception
	{
		((CoreManagementI) this.connector).releaseCores(cores);
	}

	@Override
	public AllocatedCore[] allocateCores(int nbCores) throws Exception
	{
		return ((CoreManagementI) this.connector).allocateCores(nbCores);
	}

	@Override
	public int getCurrentFrequency(AllocatedCore core) throws Exception
	{
		return ((CoreManagementI) this.connector).getCurrentFrequency(core);
	}

}

