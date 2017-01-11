package com.alascateam.distribute.ports;

import com.teamalasca.distribute.DataDistributeDynamic;
import com.teamalasca.fonction.RequestDispatcher;
import com.teamalasca.requestdispatcherPC.RequestDispatcherPC;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataOfferedI.DataI;
import fr.upmc.datacenter.ports.AbstractControlledDataInboundPort;

public class ControlerCapteurInboundPort extends AbstractControlledDataInboundPort {

	public ControlerCapteurInboundPort(String string, ComponentI owner) throws Exception {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public DataI get() throws Exception {
		final RequestDispatcherPC rd = (RequestDispatcherPC) this.owner ;
		return rd.handleRequestSync(
					new ComponentI.ComponentService<DataOfferedI.DataI>() {
						@Override
						public DataOfferedI.DataI call() throws Exception {
							return rd.getDynamicData();
						}
					});
	}

}
