package com.alascateam.dispatcher.ports;

import com.teamalasca.requestdispatcherPC.RequestDispatcherPC;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataOfferedI.DataI;
import fr.upmc.datacenter.ports.AbstractControlledDataInboundPort;

public class RDcapteurInboundport extends AbstractControlledDataInboundPort  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RDcapteurInboundport(String uri,ComponentI owner) throws Exception {
		super(uri,owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public DataI get() throws Exception {
		// TODO Auto-generated method stub
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
