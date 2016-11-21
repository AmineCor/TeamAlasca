package ports;


import Interface.AdmissionNotificationHandlerI;
import Interface.AdmissionI;
import Interface.AdmissionNotificationI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

public class AdmissionNotificationInboundPort extends AbstractInboundPort implements AdmissionNotificationI {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public AdmissionNotificationInboundPort(ComponentI owner) throws Exception {
		super(AdmissionNotificationI.class, owner);
	}
	public AdmissionNotificationInboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, AdmissionNotificationI.class,owner);
	}
	@Override
	public void notifyAdmission(final AdmissionI a) throws Exception {
		final AdmissionNotificationHandlerI anh =
				(AdmissionNotificationHandlerI) this.owner ;
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						anh.acceptAdmissionNotification(a) ;
						return null;
					}
				}) ;
		
		
		
		
	}
	

}
