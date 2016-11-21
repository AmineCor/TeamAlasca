package ports;

import Interface.AdmissionI;
import Interface.AdmissionSubmissionHandlerI;
import Interface.AdmissionSubmissionI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;


public class AdmissionSubmissionInboundPort extends AbstractInboundPort implements AdmissionSubmissionI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public AdmissionSubmissionInboundPort(ComponentI owner) throws Exception {
		super(AdmissionSubmissionI.class, owner);
		// TODO Auto-generated constructor stub
	}
	public AdmissionSubmissionInboundPort(String URI, ComponentI owner) throws Exception {
		super(URI,AdmissionSubmissionI.class, owner);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void submitAdmissionRequest(final AdmissionI a) throws Exception {
		// TODO Auto-generated method stub
		final AdmissionSubmissionHandlerI ah =
				(AdmissionSubmissionHandlerI) this.owner ;
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						ah.handleAdmissionSubmissionAndNotify(a) ;
						return null ;
					}
				}) ;	
		}
	

	@Override
	public void submitAdmissionRequestAndNotify(final AdmissionI a) throws Exception {
		// TODO Auto-generated method stub
		final AdmissionSubmissionHandlerI ah =
				(AdmissionSubmissionHandlerI) this.owner ;
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						ah.handleAdmissionSubmissionAndNotify(a) ;
						return null ;
					}
				}) ;	}



}

