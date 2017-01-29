package com.teamalasca.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;


/**
 * The class <code>ApplicationVMData</code> keeps information for
 * <code>ApplicationVM</code> components.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Clément George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class ApplicationVMData
{

	/** Application virtual machine request submission inbound port URI. */
	private String applicationVmRequestSubmissionInboundPortURI;
	
	/** List of the allocated cores. */
	private List<AllocatedCore> ac;
	
	/**
	 * Construct an <code>ApplicationVMData</code>.
	 * 
	 * @param applicationVmRequestSubmissionInboundPortURI the application virtual machine request submission inbound port URI.
	 */
	public ApplicationVMData(String applicationVmRequestSubmissionInboundPortURI)
	{
		this.applicationVmRequestSubmissionInboundPortURI = applicationVmRequestSubmissionInboundPortURI;
		this.ac = new ArrayList<>();
	}
	
	/**
	 * Get the application virtual machine request submission inbound port URI.
	 * 
	 * @return the application virtual machine request submission inbound port URI.
	 */
	public String getApplicationVmRequestSubmissionInboundPortURI()
	{
		return this.applicationVmRequestSubmissionInboundPortURI;
	}
	
	/**
	 * Add a core.
	 * 
	 * @param ac the core to add.
	 * @return true if the core has been added.
	 */
	public boolean addCore(final AllocatedCore ac)
	{
		return this.ac.add(ac);
	}
	
	/**
	 * Add cores.
	 * 
	 * @param ac the cores to add.
	 * @return true if the cores have been added.
	 */
	public boolean addCores(final AllocatedCore[] ac)
	{
		return this.ac.addAll(Arrays.asList(ac));
	}
	
	/**
	 * Remove a core.
	 * @param ac the core to remove.
	 * @return true if the cores have been removed.
	 */
	public boolean removeCore(final AllocatedCore ac)
	{
		return this.ac.remove(ac);
	}
	
	/**
	 * Remove cores.
	 * 
	 * @param ac the cores to remove.
	 * @return true if the cores have been removed.
	 */
	public boolean removeCores(final AllocatedCore[] ac)
	{
		return this.ac.removeAll(Arrays.asList(ac));
	}
	
	/**
	 * Select a random core from the virtual machine.
	 * 
	 * @return a random core from the virtual machine.
	 */
	public AllocatedCore selectRandomCore()
	{
		return this.ac.get(new Random().nextInt(ac.size()));
	}
	
	/**
	 * Check if the virtual machine has only one allocated core.
	 * 
	 * @return true if the virtual machine has only one allocated core. 
	 */
	public boolean hasOnlyOneCore()
	{
		return this.ac.size() == 1;
	}
	
}
