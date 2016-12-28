package com.teamalasca.controler;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import com.alascateam.distribute.ports.ControlerCapteurOutboundPort;
import com.teamalasca.distribute.DataDistributeDynamique;
import com.teamalasca.distribute.DataDistributeDynamiqueReceptionI;

import fr.upmc.components.AbstractComponent;

public class Controler extends AbstractComponent implements DataDistributeDynamiqueReceptionI {
	
	public static ControlerCapteurOutboundPort cobp;
	private String uri;
	private List<Double> Average;
	private static final int N = 3;
	private static final double  seuil = 5000;
	private static final double  seuil_frequency = 0.20;
	private static final double  seuil_core = 0.40;
	private static final double  seuil_vm = 0.80;
    private static final int interval = 30000;
    protected ScheduledFuture<?> pushingFuture ;
    public static Controler singleton;
	private ControlerCapteurOutboundPort aop;
	private String app_uri;
	
	
	@Override
	public void receptionOfDynamiqueDataDispatcher(DataDistributeDynamique Data) {
		// TODO Auto-generated method stub
		
	}
	
	
	

}
