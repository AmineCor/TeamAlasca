package com.teamalasca.computer.connectors;

import com.teamalasca.computer.interfaces.CoreManagementI;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;


/**
 * The class <code>CoreManagementConnector</code> implements 
 * a connector for ports exchanging through the interface 
 * <code>CoreManagementI</code>.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class CoreManagementConnector
extends AbstractConnector
implements CoreManagementI
{

	/**
	 * @see com.teamalasca.computer.interfaces.CoreManagementI#allocateCore()
	 */
	@Override
	public AllocatedCore allocateCore() throws Exception
	{
		return ((CoreManagementI) this.offering).allocateCore();
	}

	/**
	 * @see com.teamalasca.computer.interfaces.CoreManagementI#allocateCores(int)
	 */
	@Override
	public AllocatedCore[] allocateCores(int nbCores) throws Exception
	{
		return ((CoreManagementI) this.offering).allocateCores(nbCores);
	}

	/**
	 * @see com.teamalasca.computer.interfaces.CoreManagementI#releaseCore(fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore)
	 */
	@Override
	public void releaseCore(AllocatedCore core) throws Exception
	{
		((CoreManagementI) this.offering).releaseCore(core);
	}

	/**
	 * @see com.teamalasca.computer.interfaces.CoreManagementI#releaseCores(fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore[])
	 */
	@Override
	public void releaseCores(AllocatedCore[] cores) throws Exception
	{
		((CoreManagementI) this.offering).releaseCores(cores);	
	}

	/**
	 * @see com.teamalasca.computer.interfaces.CoreManagementI#changeFrequency(fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore, int)
	 */
	@Override
	public void changeFrequency(AllocatedCore core, int frequency) throws Exception
	{
		((CoreManagementI) this.offering).changeFrequency(core, frequency);
	}

	/**
	 * @see com.teamalasca.computer.interfaces.CoreManagementI#getCurrentFrequency(fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore)
	 */
	@Override
	public int getCurrentFrequency(AllocatedCore core) throws Exception
	{
		return ((CoreManagementI) this.offering).getCurrentFrequency(core);
	}

}
