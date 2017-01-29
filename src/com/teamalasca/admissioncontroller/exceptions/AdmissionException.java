package com.teamalasca.admissioncontroller.exceptions;


public class AdmissionException extends Exception
{

	private static final long serialVersionUID = 1L;

	public AdmissionException(Exception e)
	{
		super(e);
	}
	
	public AdmissionException(String msg)
	{
		super(msg);
	}

}
