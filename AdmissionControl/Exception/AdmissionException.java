package Exception;

public class AdmissionException extends Exception {

	public AdmissionException(Exception e)
	{
		super(e);
	}
	
	public AdmissionException(String msg)
	{
		super(msg);
	}

}
