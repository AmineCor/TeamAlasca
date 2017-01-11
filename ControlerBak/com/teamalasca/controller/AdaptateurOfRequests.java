package com.teamalasca.controller;


import com.alascateam.admission.interfaces.VMRequestI;

public class AdaptateurOfRequests implements VMRequestI {

	private static final long serialVersionUID = 1L;
	
	private String vmuri;
	private String application_uri;
	
	enum Frequency {
		up,
		down
	};
	private Frequency freq;
	

	public AdaptateurOfRequests(String vmuri, String application_uri) {
		super();
		this.vmuri = vmuri;
		this.application_uri = application_uri;
	}

	@Override
	public String getVMUri() {
		
		return this.vmuri;
	}

	@Override
	public String getAppUri() {
		return this.application_uri;
	}

	@Override
	public void setVMUri(String VMUri) {
       this.vmuri = VMUri;		
	}

	@Override
	public void setAppUri(String appUri) {
		this.application_uri = appUri;
      		
	}
	
	public void setFrequency(Frequency f)
	{
		this.freq=f;
	}
}
