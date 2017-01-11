package com.alascateam.controler.interfaces;

import com.alascateam.admission.interfaces.VMRequestI;

public interface ControlerI {
	
	public void allocateCore(VMRequestI r) throws Exception;
	
	public void allocateVM(VMRequestI r) throws Exception;
	
	public void releaseCore(VMRequestI r) throws Exception;
	
	public void releaseVM(VMRequestI r) throws Exception;
	
	public void changeFrequency(VMRequestI r) throws Exception;

}
