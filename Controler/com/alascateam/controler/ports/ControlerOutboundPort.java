package com.alascateam.controler.ports;

import com.alascateam.admission.interfaces.VMRequestI;
import com.alascateam.controler.interfaces.ControlerI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;


public class ControlerOutboundPort extends AbstractOutboundPort implements ControlerI  {

	

	public ControlerOutboundPort(Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(implementedInterface, owner);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void allocateCore(VMRequestI r) throws Exception {
		// TODO Auto-generated method stub
		((ControlerI)this.connector).allocateCore(r) ;
		
	}

	@Override
	public void allocateVM(VMRequestI r) throws Exception {
		// TODO Auto-generated method stub
		((ControlerI)this.connector).allocateVM(r) ;
		
	}

	@Override
	public void releaseCore(VMRequestI r) throws Exception {
		// TODO Auto-generated method stub
		((ControlerI)this.connector).releaseCore(r) ;
		
	}

	@Override
	public void releaseVM(VMRequestI r) throws Exception {
		// TODO Auto-generated method stub
		((ControlerI)this.connector).releaseVM(r) ;
		
	}

	@Override
	public void changeFrequency(VMRequestI r) throws Exception {
		// TODO Auto-generated method stub
		((ControlerI)this.connector).changeFrequency(r) ;
		
	}

}
