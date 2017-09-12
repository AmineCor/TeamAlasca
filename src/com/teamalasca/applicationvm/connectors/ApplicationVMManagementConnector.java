package com.teamalasca.applicationvm.connectors;

import com.teamalasca.applicationvm.interfaces.ApplicationVMManagementI;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;


/**
 * The class <code>CoreManagementConnector</code> implements 
 * a connector for ports exchanging through the interface 
 * <code>CoreManagementI</code>.
 * 
 * @author	<a href="mailto:clementyj.george@gmail.com">Cl�ment George</a>
 * @author	<a href="mailto:med.amine006@gmail.com">Mohamed Amine Corchi</a>
 * @author  <a href="mailto:victor.nea@gmail.com">Victor Nea</a>
 */
public class ApplicationVMManagementConnector
extends fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector
implements ApplicationVMManagementI
{

	@Override
	public void releaseCore(AllocatedCore ac) throws Exception {
		((ApplicationVMManagementI)this.offering).releaseCore(ac);	
	}

	@Override
	public void dispose() throws Exception {
		((ApplicationVMManagementI)this.offering).dispose();
		
	}

	

}
