package com.alascateam.distribute.ports;

import com.teamalasca.fonction.RequestDispatcher;
import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataOfferedI.DataI;
import fr.upmc.datacenter.ports.AbstractControlledDataInboundPort;

public class ControlerCapteurInboundPort extends AbstractControlledDataInboundPort {

	public ControlerCapteurInboundPort(ComponentI owner) throws Exception {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public DataI get() throws Exception {
		final RequestDispatcher rd = (RequestDispatcher) this.owner ;
		return rd.handleRequestSync(
					new ComponentI.ComponentService<DataOfferedI.DataI>() {
						@Override
						public DataOfferedI.DataI call() throws Exception {
							return ((Object) rd).getDynamicData();
						}
					});
	}

}
