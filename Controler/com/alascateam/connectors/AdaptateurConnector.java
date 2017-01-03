package com.alascateam.connectors;

import com.alascateam.admission.interfaces.VMRequestI;
import com.alascateam.controler.interfaces.ControlerI;

import fr.upmc.components.connectors.AbstractConnector;

public class AdaptateurConnector extends AbstractConnector implements ControlerI {

	public void allocateCore(VMRequestI r) throws Exception {
		 ((ControlerI)this.offering).allocateCore(r);
		
	}

	@Override
	public void allocateVM(VMRequestI r) throws Exception {
		 ((ControlerI)this.offering).allocateVM(r);
		
	}

	@Override
	public void releaseCore(VMRequestI r) throws Exception {
		 ((ControlerI)this.offering).releaseCore(r);
		
	}

	@Override
	public void releaseVM(VMRequestI r) throws Exception {
		 ((ControlerI)this.offering).releaseVM(r);
		
	}

	@Override
	public void changeFrequency(VMRequestI r) throws Exception {
		 ((ControlerI)this.offering).changeFrequency(r);
		
	}

	
	
	
	

}
