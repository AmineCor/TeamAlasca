package com.teamalasca.computer.interfaces;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;


/**
 * The interface <code>CoreManagementI</code> defines the services offered by
 * <code>Computer</code> components.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public interface CoreManagementI
{

	/**
	 * Allocate a new core.
	 * 
	 * @return the new allocated core.
	 * @throws Exception throws an exception if an error occured..
	 */
	public AllocatedCore allocateCore() throws Exception;
	
	/**
	 * Allocate new cores.
	 * 
	 * @param nbCores the number of cores to allocate.
	 * @return the new allocated cores.
	 * @throws Exception throws an exception if an error occured..
	 */
	public AllocatedCore[] allocateCores(int nbCores) throws Exception;
	
	/**
	 * Release a core.
	 * 
	 * @param core the allocated core to release.
	 * @throws Exception throws an exception if an error occured..
	 */
	public void releaseCore(AllocatedCore core) throws Exception;
	
	/**
	 * Release cored.
	 * 
	 * @param cores the allocated cores to release.
	 * @throws Exception throws an exception if an error occured..
	 */
	public void releaseCores(AllocatedCore[] cores) throws Exception;
	
	/**
	 * Change the frequency of a core.
	 * 
	 * @param core the related core to change its frequency.
	 * @param frequency the new frequency to set.
	 * @throws Exception throws an exception if an error occured..
	 */
	public void changeFrequency(AllocatedCore core, int frequency) throws Exception;
	
	/**
	 * Get the current frequency of a core.
	 * 
	 * @param core the relate core to get its frequency.
	 * @return the current frequency of the core.
	 * @throws Exception throws an exception if an error occured..
	 */
	public int getCurrentFrequency(AllocatedCore core) throws Exception;
	
}
