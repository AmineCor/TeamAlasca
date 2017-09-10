package com.teamalasca.autonomiccontroller.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

public class AllocatedVirtualMachine {
	
	/** URI of the machine, used as equality criterion */
	private final String URI;

	/** Application virtual machine request submission inbound port URI. */
	private final String applicationVmRequestSubmissionInboundPortURI;
	
	/** List of the allocated cores. */
	private final List<AllocatedCore> cores;
	
	/**
	 * Construct an <code>ApplicationVMData</code>.
	 * 
	 * @param applicationVmRequestSubmissionInboundPortURI the application virtual machine request submission inbound port URI.
	 */
	public AllocatedVirtualMachine(String applicationVmURI,String applicationVmRequestSubmissionInboundPortURI)
	{
		this.applicationVmRequestSubmissionInboundPortURI = applicationVmRequestSubmissionInboundPortURI;
		this.URI = applicationVmURI;
		this.cores = new ArrayList<>();
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
		return this.cores.add(ac);
	}
	
	/**
	 * Add cores.
	 * 
	 * @param cores2 the cores to add.
	 * @return true if the cores have been added.
	 */
	public boolean addCores(final List<AllocatedCore> cores)
	{
		return this.cores.addAll(cores);
	}
	
	/**
	 * Remove a core.
	 * @param ac the core to remove.
	 * @return true if the cores have been removed.
	 */
	public boolean removeCore(final AllocatedCore ac)
	{
		return this.cores.remove(ac);
	}
	
	/**
	 * Remove cores.
	 * 
	 * @param ac the cores to remove.
	 * @return true if the cores have been removed.
	 */
	public boolean removeCores(final AllocatedCore[] ac)
	{
		return this.cores.removeAll(Arrays.asList(ac));
	}
	
	/**
	 * Select a random core from the virtual machine.
	 * 
	 * @return a random core from the virtual machine.
	 */
	public AllocatedCore selectRandomCore()
	{
		return this.cores.get(new Random().nextInt(cores.size()));
	}
	
	/**
	 * @return the number of cores of the current virtual machine
	 */
	public int getCoreNumber() {
		return this.cores.size();
	}

	/**
	 * return the cores of the virtual machine
	 */
	public List<AllocatedCore> getCores() {
		return this.cores;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof AllocatedVirtualMachine){
			AllocatedVirtualMachine vm = (AllocatedVirtualMachine) obj;
			return vm.URI.equals(this.URI);
		}
		
		return super.equals(obj);
	}

	public String getURI() {
		return this.URI;
	}

}
