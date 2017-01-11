package com.alascateam.distsribute.interfaces;

import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.interfaces.TimeStampingI;

public interface DataDistributeDynamicI extends DataOfferedI.DataI,
DataRequiredI.DataI,
TimeStampingI {
	
	
	public String getDistributeURI();
	
	public double getMeyenneTempsExecution();
	
	public void   getVMinformationApp();
	
	

}
