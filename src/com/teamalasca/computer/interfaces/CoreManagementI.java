package com.teamalasca.computer.interfaces;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

public interface CoreManagementI
{

	public AllocatedCore allocateCore() throws Exception;
	
	public AllocatedCore[] allocateCores(int nbCores) throws Exception;
	
	public void releaseCore(AllocatedCore core) throws Exception;
	
	public void releaseCores(AllocatedCore[] cores) throws Exception;
	
	public void changeFrequency(AllocatedCore core, int frequency) throws Exception;
	
}
