package Interface;


import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface AdmissionSubmissionI extends OfferedI,RequiredI {
	
	public void submitAdmissionRequest(AdmissionI a) throws Exception;
	
	public void submitAdmissionRequestAndNotify(AdmissionI a) throws Exception;
}

