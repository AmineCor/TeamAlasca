package com.teamalasca.autonomiccontroller.state;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a view of the ressources allocated for a given application
 * @author clementgeorge
 *
 */
public final class AllocatedResources {
	
	private final List<AllocatedVirtualMachine> vms;
	private final AllocatedVirtualMachine baseVm;
	
	
	/**
	 * At least one virtual machine is needed for creating a new ressource state. 
	 * @param avm : the default machine.
	 */
	public AllocatedResources(final AllocatedVirtualMachine avm) {
		vms = new ArrayList<>();
		this.baseVm = avm;
		vms.add(avm);
	}

	public void addVirtualMachine(AllocatedVirtualMachine avm) {
		vms.add(avm);
	}
	
	public void removeVirtualMachine(AllocatedVirtualMachine avm) {
		vms.remove(avm);
	}
	
	public int getVirtualMachineNumber(){
		return vms.size();
	}
	
	public final AllocatedVirtualMachine getBaseVM(){
		return this.baseVm;
	}

	/**
	 * Remove and returns the last virtual machine from the list of the allocated vms
	 * @return the removed virtual machine.
	 */
	public AllocatedVirtualMachine getLastVirtualMachine() {
		return vms.get(vms.size() - 1);
	}
	
	@Override
	public String toString() {
		return this.getVirtualMachineNumber() + " virtual machines";
	}

	public AllocatedVirtualMachine getVirtualMachine(String virtualMachineURI) {
		for(AllocatedVirtualMachine machine:vms){
			if(machine.equals(virtualMachineURI)){
				return machine;
			}
		}
		return null;
	}
	

}
