package com.teamalasca.computer.ports;

import com.teamalasca.computer.Computer;
import com.teamalasca.computer.interfaces.CoreManager;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

public class ManageCoreInboundPort extends AbstractInboundPort implements CoreManager{

	private static final long serialVersionUID = 7785838464919728639L;

	public ManageCoreInboundPort(String uri,ComponentI owner) throws Exception {
		super(uri,CoreManager.class, owner);
		
	}

	@Override
	public AllocatedCore allocateCore() throws Exception {
		
		final Computer c = (Computer) this.owner ;
		return c.handleRequestSync(
					new ComponentI.ComponentService<AllocatedCore>() {
						@Override
						public AllocatedCore call() throws Exception {
							return c.allocateCore();
						}
					}) ;
	}

	@Override
	public void releaseCore(final AllocatedCore core) throws Exception {
		final Computer c = (Computer) this.owner ;
		c.handleRequestSync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							 c.releaseCore(core);
							return null;
							
						}
					}) ;
		
	}

	@Override
	public void changeFrequency(final AllocatedCore core,final int frequency) throws Exception{
		
		final Computer c = (Computer) this.owner ;
		c.handleRequestSync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							 c.changeFrequency(core,frequency);
							return null;	
						}
					}) ;
	}

	@Override
	public void releaseCores(final AllocatedCore[] cores) throws Exception {
		final Computer c = (Computer) this.owner ;
		c.handleRequestSync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							 c.releaseCores(cores);
							return null;
							
						}
					}) ;
		
	}

	@Override
	public AllocatedCore[] allocateCores(final int nbCores) throws Exception {
		final Computer c = (Computer) this.owner ;
		return c.handleRequestSync(
					new ComponentI.ComponentService<AllocatedCore[]>() {
						@Override
						public AllocatedCore[] call() throws Exception {
							return c.allocateCores(nbCores);
						}
					}) ;
	}

}
