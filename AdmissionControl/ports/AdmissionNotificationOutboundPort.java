package ports;

import Interface.AdmissionI;
import Interface.AdmissionNotificationI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import Interface.AdmissionNotificationHandlerI;

public class AdmissionNotificationOutboundPort extends AbstractOutboundPort implements AdmissionNotificationHandlerI {

	public AdmissionNotificationOutboundPort(ComponentI owner) throws Exception
	{
		super(AdmissionNotificationI.class, owner);
	}

	public AdmissionNotificationOutboundPort(String uri,ComponentI owner) throws Exception
	{
		super(uri, AdmissionNotificationI.class, owner) ;
	}

	@Override
	public void acceptAdmissionNotification(AdmissionI a) throws Exception {
		((AdmissionNotificationI)this.connector).notifyAdmission(a) ;
	}
}

