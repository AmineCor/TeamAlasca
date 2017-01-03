package com.teamalasca.controler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import com.alascateam.connectors.AdaptateurConnector;
import com.alascateam.controler.interfaces.ControlerI;
import com.alascateam.controler.ports.ControlerOutboundPort;
import com.alascateam.distribute.ports.ControlerCapteurOutboundPort;
import com.teamalasca.controler.Adaptateurofrequests.Frequency;
import com.teamalasca.distribute.DataDistributeDynamique;
import com.teamalasca.distribute.DataDistributeDynamiqueReceptionI;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;

public class Controler extends AbstractComponent implements DataDistributeDynamiqueReceptionI {
	
	public static ControlerCapteurOutboundPort cobp;
	private String uri;
	private List<Double> Average;
	private static final int N = 3;
	private static final double  s1 = 5000;
	private static final double  sfrequence = 0.20;
	private static final double  score = 0.40;
	private static final double  s_vm = 0.80;
    private static final int interval = 30000;
    protected ScheduledFuture<?> pushFuture ;
    public static Controler singleton;
	private ControlerOutboundPort obp;
	private String app_uri;
	private String applicationuri;
	
	public Controler(String uri, String ac_uri, String applicationuri) throws Exception
	{
		super(1,1);
		this.uri = uri;
		this.cobp = new ControlerCapteurOutboundPort (this);
		this.addPort(cobp);
		cobp.publishPort();
		this.addRequiredInterface(DataRequiredI.PullI.class) ;
		this.addOfferedInterface(DataRequiredI.PushI.class);
		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class);
		singleton = this;
		Average = new ArrayList<Double>();
		this.obp = new ControlerOutboundPort(null, this);
		this.obp.publishPort();
		this.addPort(obp);
		this.obp.doConnection(ac_uri + "_obp", AdaptateurConnector.class.getCanonicalName());
		this.addRequiredInterface(ControlerI.class);
		this.applicationuri=applicationuri;
	}
	
	@Override
	public void receptionOfDynamiqueDataDispatcher(DataDistributeDynamique Data) {
		
		// TODO Auto-generated method stub
		this.Average.add(Data.getMeyenneTempsExecution());
		logMessage("Trigger : Moy "+Data.getMeyenneTempsExecution());
			if (Average.size()>N) 
				Average.remove(0);
		
	}
	
	public Double Calcul_de_la_moyenneM ()
	{
		
		double result = 0;
		for(double i : Average)
		{
			result+=i;
		}
		
		result = result/Average.size();
		logMessage("Controler : La Moyenne est "+result);
		return result;
	}
	
	public void scheduleComputeMoyenne() throws Exception
	{
		
		this.pushFuture =
				this.scheduleTask(
						new ComponentI.ComponentTask() {
							@Override
							public void run() {
								try {
									double m = Calcul_de_la_moyenneM();
									String msg = "Controler "+uri+" : "+m;
									if (m>s1)
									{
										if (m > s1*(1+s_vm)) 
										{
										    msg+="This VM have to be saved ";
										    Adaptateurofrequests ar = new Adaptateurofrequests("", app_uri);
											obp.allocateVM(ar);																
																					  
										}
									else if (m > s1*(1+score))
										{
										     msg+="SAVE ME !! Add Core";
										     Adaptateurofrequests ar = new Adaptateurofrequests("", app_uri);
										     obp.allocateCore(ar);															

										}
									else if (m > s1*(1+sfrequence)) 
										{
										      msg+="SAVE ME !! Up Frequency";
										      Adaptateurofrequests ar = new Adaptateurofrequests("", app_uri);
									          ar.setFrequency(Frequency.up);
									          obp.changeFrequency(ar);
										}
									}
									
									else
									{
										if (m <= s1*(1-s_vm)) 
										{
										      msg+="SAVE ME !! delete VM";
										      Adaptateurofrequests ar = new Adaptateurofrequests("", app_uri);
									          obp.releaseVM(ar);
										
										  
										}
									else if (m <= s1*(1-score)) 
										{
										       msg+="SAVE ME !! delete Core";
										       Adaptateurofrequests ar = new Adaptateurofrequests("", app_uri);
										       obp.releaseCore(ar);
										}
									else if (m <= s1*(1-sfrequence)) 
										{
										        msg+="SAVE ME !! Down Frequency";
										        Adaptateurofrequests ar = new Adaptateurofrequests("", app_uri);
										        ar.setFrequency(Frequency.down);
										        obp.changeFrequency(ar);
										}
									}
									msg+="SAVE ME !! Or Not !";
									
									logMessage(msg);

								} catch (Exception e) {
									e.printStackTrace();
									throw new RuntimeException(e) ;
								}
								try {
									scheduleComputeMoyenne();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}, interval, TimeUnit.MILLISECONDS) ;
		}
	
	

}
