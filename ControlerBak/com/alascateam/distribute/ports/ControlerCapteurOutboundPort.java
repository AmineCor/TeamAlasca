package com.alascateam.distribute.ports;

import com.teamalasca.distribute.DataDistributeDynamic;
import com.teamalasca.distribute.DataDistributeDynamicReceptionI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataRequiredI.DataI;
import fr.upmc.datacenter.ports.AbstractControlledDataOutboundPort;

public class ControlerCapteurOutboundPort extends AbstractControlledDataOutboundPort {

	private static final long serialVersionUID = 1L;

	public ControlerCapteurOutboundPort(ComponentI owner) throws Exception {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void receive(DataI d) throws Exception {
		((DataDistributeDynamicReceptionI)this.owner).receptionOfDynamicDataDispatcher((DataDistributeDynamic)d);
		
	}

}
