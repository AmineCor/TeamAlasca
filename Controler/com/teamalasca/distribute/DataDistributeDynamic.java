package com.teamalasca.distribute;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.alascateam.distsribute.interfaces.DataDistributeDynamicI;

public class DataDistributeDynamic implements DataDistributeDynamicI {
	private static final long serialVersionUID = 1L;

	String timeStamp;
	String uri;
	Double average;


	public DataDistributeDynamic(String spintedtimeId, String uri,
			Double average) throws UnknownHostException {
		super();
		timeStamp = InetAddress.getLocalHost().getHostAddress();
		this.uri = uri;
		this.average = average;
	}

	@Override
	public long getTimeStamp() {
		return System.currentTimeMillis();
	}

	@Override
	public String getTimeStamperId() {
		return timeStamp;
	}

	@Override
	public String getDistributeURI() {
		return uri;
	}

	@Override
	public double getMeyenneTempsExecution() {
		return average ;
	}

	@Override
	public void getVMinformationApp() {		
	}
}
