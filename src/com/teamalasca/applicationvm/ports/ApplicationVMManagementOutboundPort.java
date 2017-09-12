package com.teamalasca.applicationvm.ports;

import com.teamalasca.applicationvm.connectors.ApplicationVMManagementConnector;
import com.teamalasca.applicationvm.interfaces.ApplicationVMManagementI;

import fr.upmc.components.ComponentI;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

public class ApplicationVMManagementOutboundPort extends fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort implements ApplicationVMManagementI {


	public ApplicationVMManagementOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, owner);
	}

	@Override
	public void releaseCore(AllocatedCore ac) throws Exception {
		((ApplicationVMManagementConnector) this.connector).releaseCore(ac);
	}

	@Override
	public void dispose() throws Exception {
		((ApplicationVMManagementConnector) this.connector).dispose();
		
	}

}

