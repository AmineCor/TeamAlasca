package com.teamalasca.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

public class ApplicationVMData
{

	private String applicationVmRequestSubmissionInboundPortURI;
	private List<AllocatedCore> ac;
	
	public ApplicationVMData(String applicationVmRequestSubmissionInboundPortURI)
	{
		this.applicationVmRequestSubmissionInboundPortURI = applicationVmRequestSubmissionInboundPortURI;
		this.ac = new ArrayList<>();
	}
	
	public String getApplicationVmRequestSubmissionInboundPortURI()
	{
		return this.applicationVmRequestSubmissionInboundPortURI;
	}
	
	public boolean addCore(final AllocatedCore ac)
	{
		return this.ac.add(ac);
	}
	
	public boolean addCores(final AllocatedCore[] ac)
	{
		return this.ac.addAll(Arrays.asList(ac));
	}
	
	public boolean removeCore(final AllocatedCore ac)
	{
		return this.ac.remove(ac);
	}
	
	public boolean removeCores(final AllocatedCore[] ac)
	{
		return this.ac.removeAll(Arrays.asList(ac));
	}
	
	public AllocatedCore selectRandomCore()
	{
		return this.ac.get(new Random().nextInt(ac.size()));
	}
	
	public boolean hasOnlyOneCore()
	{
		return this.ac.size() == 1;
	}
	
}
