package com.teamalasca.computer.ports;

import com.teamalasca.computer.interfaces.CoreManagementI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.processors.UnacceptableFrequencyException;

public class CoreManagementOutboundPort
extends AbstractOutboundPort
implements CoreManagementI
{

	/**
	 * Construct a <code>CoreManagementOutboundPort</code>.
	 * 
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public CoreManagementOutboundPort(ComponentI owner) throws Exception
	{
		super(CoreManagementI.class, owner);
	}
	
	/**
	 * Construct a <code>CoreManagementOutboundPort</code>.
	 * 
	 * @param uri the URI of the port.
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public CoreManagementOutboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, CoreManagementI.class, owner);
	}

	/**
	 * @see com.teamalasca.computer.interfaces.CoreManagementI#allocateCore()
	 */
	@Override
	public AllocatedCore allocateCore() throws Exception
	{
		return ((CoreManagementI) this.connector).allocateCore();
	}
	
	/**
	 * @see com.teamalasca.computer.interfaces.CoreManagementI#allocateCores(int)
	 */
	@Override
	public AllocatedCore[] allocateCores(int nbCores) throws Exception
	{
		return ((CoreManagementI) this.connector).allocateCores(nbCores);
	}
	
	/**
	 * @see com.teamalasca.computer.interfaces.CoreManagementI#releaseCore(fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore)
	 */
	@Override
	public void releaseCore(AllocatedCore core) throws Exception
	{
		((CoreManagementI) this.connector).releaseCore(core);
	}
	
	/**
	 * @see com.teamalasca.computer.interfaces.CoreManagementI#releaseCores(fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore[])
	 */
	@Override
	public void releaseCores(AllocatedCore[] cores) throws Exception
	{
		((CoreManagementI) this.connector).releaseCores(cores);
	}

	/**
	 * @see com.teamalasca.computer.interfaces.CoreManagementI#changeFrequency(fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore, int)
	 */
	@Override
	public void changeFrequency(AllocatedCore core, int frequency) throws Exception
	{
		((CoreManagementI) this.connector).changeFrequency(core, frequency);
	}

	/**
	 * @see com.teamalasca.computer.interfaces.CoreManagementI#getCurrentFrequency(fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore)
	 */
	@Override
	public int getCurrentFrequency(AllocatedCore core) throws Exception
	{
		return ((CoreManagementI) this.connector).getCurrentFrequency(core);
	}

	@Override
	public void increaseFrequency(AllocatedCore core)
			throws UnacceptableFrequencyException, Exception {
		((CoreManagementI) this.connector).increaseFrequency(core);
		
	}
	
	@Override
	public void decreaseFrequency(AllocatedCore core)
			throws UnacceptableFrequencyException, Exception {
		((CoreManagementI) this.connector).decreaseFrequency(core);
		
	}

}
