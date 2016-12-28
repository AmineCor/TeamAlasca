package com.teamalasca.distribute;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.alascateam.distsribute.interfaces.DataDistributeDynamiqueI;

public class DataDistributeDynamique implements DataDistributeDynamiqueI {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String TimeStamp;
	String URI;
	Double MOYENNE;
	

	public DataDistributeDynamique(String spintedtimeId, String uRI,
			Double mOYENNE) throws UnknownHostException {
		super();
		TimeStamp = InetAddress.getLocalHost().getHostAddress();
		this.URI = uRI;
		this.MOYENNE = mOYENNE;
	}

	@Override
	public long getTimeStamp() {
		// TODO Auto-generated method stub
		return System.currentTimeMillis();
	}

	@Override
	public String getTimeStamperId() {
		// TODO Auto-generated method stub
		return TimeStamp;
	}

	@Override
	public String getDistributeURI() {
		// TODO Auto-generated method stub
		return URI;
	}

	@Override
	public double getMeyenneTempsExecution() {
		// TODO Auto-generated method stub
		return MOYENNE ;
	}

	@Override
	public void getVMinformationApp() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	

}
