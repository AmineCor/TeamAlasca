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
	
	public int getTotalCoreNumber(){
		int count = 0;
		for(AllocatedVirtualMachine machine:vms)
			count += machine.getCoreNumber();
		return count;
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
		StringBuilder builder = new StringBuilder();
		builder.append(this.getVirtualMachineNumber() + " virtual machines for a total of " + this.getTotalCoreNumber() + " cores.\n");
		builder.append("--------------------------------------------\n");
		for(AllocatedVirtualMachine machine:vms){
			builder.append(machine.toString());
		}
		builder.append("\n--------------------------------------------");
		return builder.toString();
	}

	public AllocatedVirtualMachine getVirtualMachine(String virtualMachineURI) {
		for(AllocatedVirtualMachine machine:vms){
			if(machine.equals(virtualMachineURI)){
				return machine;
			}
		}
		return null;
	}

	public AllocatedVirtualMachine getMachine(int i) {
		return vms.get(i);
	}
	

}
