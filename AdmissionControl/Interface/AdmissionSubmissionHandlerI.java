package Interface;

import Interface.AdmissionI;

public interface AdmissionSubmissionHandlerI {
	
    public void handleAdmissionSubmission(AdmissionI a) throws Exception;
	
	public void handleAdmissionSubmissionAndNotify(AdmissionI a) throws Exception;
}
