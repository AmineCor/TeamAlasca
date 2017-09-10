package com.teamalasca.computer.ports;

import com.teamalasca.computer.Computer;
import com.teamalasca.computer.interfaces.CoreManagementI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.processors.UnacceptableFrequencyException;

public class CoreManagementInboundPort
extends AbstractInboundPort
implements CoreManagementI
{

	/**
	 * A unique serial version identifier.
	 * @see java.io.Serializable#serialVersionUID
	 */
	private static final long serialVersionUID = 7785838464919728639L;

	/**
	 * Construct a <code>CoreManagementInboundPort</code>.
	 * 
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public CoreManagementInboundPort(ComponentI owner) throws Exception
	{
		super(CoreManagementI.class, owner);
	}

	/**
	 * Construct a <code>CoreManagementInboundPort</code>.
	 * 
	 * @param uri the uri of the port.
	 * @param owner the owner component.
	 * @throws Exception throws an exception if an error occured..
	 */
	public CoreManagementInboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, CoreManagementI.class, owner);
	}

	/**
	 * @see com.teamalasca.computer.interfaces.CoreManagementI#allocateCore()
	 */
	@Override
	public AllocatedCore allocateCore() throws Exception
	{
		final Computer c = (Computer) this.owner;
		return c.handleRequestSync(
				new ComponentI.ComponentService<AllocatedCore>() {
					@Override
					public AllocatedCore call() throws Exception {
						return c.allocateCore();
					}
				});
	}

	/**
	 * @see com.teamalasca.computer.interfaces.CoreManagementI#allocateCores(int)
	 */
	@Override
	public AllocatedCore[] allocateCores(final int nbCores) throws Exception
	{
		final Computer c = (Computer) this.owner;
		return c.handleRequestSync(
				new ComponentI.ComponentService<AllocatedCore[]>() {
					@Override
					public AllocatedCore[] call() throws Exception {
						return c.allocateCores(nbCores);
					}
				});
	}

	/**
	 * @see com.teamalasca.computer.interfaces.CoreManagementI#releaseCore(fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore)
	 */
	@Override
	public void releaseCore(final AllocatedCore core) throws Exception
	{
		final Computer c = (Computer) this.owner;
		c.handleRequestSync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						c.releaseCore(core);
						return null;

					}
				});
	}

	/**
	 * @see com.teamalasca.computer.interfaces.CoreManagementI#releaseCores(fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore[])
	 */
	@Override
	public void releaseCores(final AllocatedCore[] cores) throws Exception
	{
		final Computer c = (Computer) this.owner;
		c.handleRequestSync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						c.releaseCores(cores);
						return null;

					}
				});
	}

	/**
	 * @see com.teamalasca.computer.interfaces.CoreManagementI#changeFrequency(fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore, int)
	 */
	@Override
	public void changeFrequency(final AllocatedCore core, final int frequency) throws Exception
	{
		final Computer c = (Computer) this.owner;
		c.handleRequestSync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						c.changeFrequency(core,frequency);
						return null;	
					}
				});
	}

	/**
	 * @see com.teamalasca.computer.interfaces.CoreManagementI#getCurrentFrequency(fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore)
	 */
	@Override
	public int getCurrentFrequency(final AllocatedCore core) throws Exception
	{
		final Computer c = (Computer) this.owner;
		return c.handleRequestSync(
				new ComponentI.ComponentService<Integer>() {
					@Override
					public Integer call() throws Exception {
						return c.getCurrentFrequency(core);
					}
				});
	}

	@Override
	public void increaseFrequency(final AllocatedCore core)
			throws UnacceptableFrequencyException, Exception {
		final Computer c = (Computer) this.owner;
		c.handleRequestSync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						c.increaseFrequency(core);
						return null;
					}
				});

	}
	
	@Override
	public void decreaseFrequency(final AllocatedCore core)
			throws UnacceptableFrequencyException, Exception {
		final Computer c = (Computer) this.owner;
		c.handleRequestSync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						c.decreaseFrequency(core);
						return null;
					}
				});

	}

}
