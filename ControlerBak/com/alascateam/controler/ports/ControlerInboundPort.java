package com.alascateam.controler.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataOfferedI.DataI;
import fr.upmc.components.ports.AbstractDataInboundPort;
import com.alascateam.admission.interfaces.VMRequestI;
import com.alascateam.controler.interfaces.ControlerI;


public class ControlerInboundPort extends AbstractDataInboundPort implements ControlerI {

	private static final long serialVersionUID = 1L;

	public ControlerInboundPort(Class<?> implementedPullInterface,
			Class<?> implementedPushInterface, ComponentI owner)
			throws Exception {
		super(implementedPullInterface, implementedPushInterface, owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public DataI get() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void allocateCore(final VMRequestI r) throws Exception {
		// TODO Auto-generated method stub
		
		final ControlerI Controler=(ControlerI) this.owner;
		this.owner.handleRequestSync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						 Controler.allocateCore(r);
							return null;

					}
				}) ;
		
	}

	@Override
	public void allocateVM(final VMRequestI r) throws Exception {
		// TODO Auto-generated method stub
		final ControlerI Controler=(ControlerI) this.owner;
		this.owner.handleRequestSync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						 Controler.allocateVM(r);
							return null;

					}
				}) ;
		
	}

	@Override
	public void releaseCore(final VMRequestI r) throws Exception {
		// TODO Auto-generated method stub
		final ControlerI adapter = (ControlerI) this.owner;
		 this.owner.handleRequestSync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							 adapter.releaseCore(r);
								return null;

						}
					}) ;	
		
	}

	@Override
	public void releaseVM(final VMRequestI r) throws Exception {
		// TODO Auto-generated method stub
		final ControlerI adapter = (ControlerI) this.owner;
		 this.owner.handleRequestSync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							 adapter.releaseVM(r);
								return null;

						}
					}) ;
		
	}

	@Override
	public void changeFrequency(final VMRequestI r) throws Exception {
		// TODO Auto-generated method stub
		final ControlerI adapter = (ControlerI) this.owner;
		 this.owner.handleRequestSync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							 adapter.changeFrequency(r);
								return null;

						}
					}) ;	
		
	}

}
