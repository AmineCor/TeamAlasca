package com.teamalasca.requestdispatcherPC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import com.alascateam.admission.interfaces.AjoutVMRequestI;
import com.alascateam.admission.interfaces.AjoutVMRequesthandlerI;
import com.alascateam.admission.interfaces.VMRequestI;
import com.alascateam.dispatcher.ports.AjoutVMRequestInboundport;
import com.alascateam.dispatcher.ports.RDcapteurInboundport;
import com.alascateam.distribute.ports.ControlerCapteurInboundPort;
import com.alascateam.distsribute.interfaces.DataDistributeDynamiqueI;
import com.teamalasca.distribute.DataDistributeDynamique;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ComponentI;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.datacenter.interfaces.ControlledDataOfferedI;
import fr.upmc.datacenter.interfaces.PushModeControllerI;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;

public class RequestDispatcherPC extends AbstractComponent 
implements RequestSubmissionHandlerI, RequestNotificationHandlerI,AjoutVMRequesthandlerI,PushModeControllerI {
	
	
	private int cts;
	
	/**
	 * RequestSubmissionOutboundPort : This port is used to send request to the VM of the application
	 */
    protected RequestSubmissionOutboundPort rsobp;
    
    /**
     * RequestNotificationInboundPort :  This port is used to receive notification from VM application of the termination 
     * the execution of the request 
     */
    private RequestNotificationInboundPort rnibp;
    
    /**
     * AddVMRequestInboundPort  : This port is used to associate a new VM application with the RequestDispatcher
     */
    private AjoutVMRequestInboundport avribp;
    
    /**
     * RequestNotificationOutboundPort : Port for sending request termination notification
     */
    protected  RequestNotificationOutboundPort rnobp;
    
    protected ControlerCapteurInboundPort dcibp;
    /**
     * List of associated virtual machine of the application
     */
    protected ArrayList<String> listofvms;
    
    /**
     * Component URI
     */
    protected String uri="";
    
    /**
     *  Execution average time
     */
    protected double somme;
    
    protected int RequestNbr;
    
    protected HashMap<String,Long> CurrentRequest;
    
	protected ScheduledFuture<?> Fsplist ;

    
    
    public RequestDispatcherPC(String AppUri) 
    	    throws Exception {
    			super(1,1);
    	    	this.listofvms = new ArrayList<String>();
    	    	cts = 0;
    	    	/**
    	    	 * Add offered interfaces and required interfaces
    	    	 */
    	    	this.addOfferedInterface(RequestSubmissionI.class);
    	    	this.addRequiredInterface(RequestSubmissionI.class);
    	    	this.addOfferedInterface(RequestNotificationI.class);
    			this.addRequiredInterface(RequestNotificationI.class);
    	    	this.addOfferedInterface(AjoutVMRequestI.class);
    	    	this.addOfferedInterface(ControlledDataOfferedI.ControlledPullI.class) ;
    	    	
    	    	//Create and Publish All Port
    	    	
    	    	RequestSubmissionInboundPort rsibp = new RequestSubmissionInboundPort(AppUri+"_rsibp", this);
    	    	this.addPort(rsibp);
    	    	rsibp.publishPort();
    	    	
    	    	rsobp = new RequestSubmissionOutboundPort( AppUri+"_rsop", this);
    	    	this.addPort(rsobp);
    	    	rsobp.publishPort();
    	    	
    			this.rnibp = new RequestNotificationInboundPort(AppUri+"_rnip", this) ;
    			this.addPort(this.rnibp) ;
    			this.rnibp.publishPort() ;
    			

    			this.rnobp = new RequestNotificationOutboundPort(AppUri+"_rnop", this) ;
    			this.addPort(this.rnobp) ;
    			this.rnobp.publishPort() ;
    			
    			this.avribp = new AjoutVMRequestInboundport(AppUri+"_avrip", this);
    			this.addPort(avribp);
    			this.avribp.publishPort();
    			
    			this.dcibp = new ControlerCapteurInboundPort(AppUri+"_dcip", this);
    			this.addPort(dcibp);
    			this.dcibp.publishPort();
    			
    			uri = AppUri;
    			
    			somme = 0;
    			RequestNbr = 0;
    			CurrentRequest = new HashMap<String,Long>();
    	    }
    		
    /**
	 * Associate an VM application with the RequestDispatcherPC
	 * @throws Exception
	 */
    public void associate(String URIco) throws Exception
    {
    	listofvms.add(URIco);
    }

    public DataDistributeDynamique getDynamicData() throws Exception
	{
		cts++;
		DataDistributeDynamique d = new DataDistributeDynamique(uri,uri, somme/RequestNbr);
		if(cts % 10 == 0)
		{
			cts = 0;
			somme = 0;
			RequestNbr = 0;
		}
		return d;
	}
    
    
	@Override
	public void startUnlimitedPushing(final int interval, final int n) throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void stopPushing() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveAddVMRequest(VMRequestI request) throws Exception {
		// TODO Auto-generated method stub
		
		logMessage("RequestDispatcherPC "+uri+" : Add New VM "+request.getVMUri());
		/**
		 * Accociate the vm application with the RDPC
		 */
		associate(request.getVMUri());
		
		
		
	}
	public String getURI()
	{
		return uri;
	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r, int nbRequest)
			throws Exception {
		// TODO Auto-generated method stub
        logMessage("RequestDispatcherPC "+uri+" : Request terminated "+r.getRequestURI());
		
		long timeS = CurrentRequest.get(r.getRequestURI());
		long tmp = System.currentTimeMillis() - timeS;
		CurrentRequest.remove(r.getRequestURI());
   logMessage("RequestDispatcherPC "+uri+" : end Request "+r.getRequestURI()+" t : "+tmp/1000);
		nbRequest++;
		somme +=tmp;
		rnobp.notifyRequestTermination(r);
		
		
		
		
	}

	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		// TODO Auto-generated method stub
		
		logMessage("RequestDispatcherPC "+uri+" : New Request "+r.getRequestURI());
		acceptRequestSubmissionAndNotify(r);
	}

	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		/**
		 * Get the first VM of the list of vms
		 */
				
	      String port = listofvms.get(0);
			/**
			 * Do the connection with the VM selected and send the request to this vm and ask for the termination 
			 * notification 	
			 */
				
		    	rsobp.doConnection(port,RequestSubmissionConnector.class.getCanonicalName());
		    	
				long tmp = System.currentTimeMillis();
				CurrentRequest.put(r.getRequestURI(),tmp);
		    	rsobp.submitRequestAndNotify(r);
		   logMessage("RequestDispatcherPC "+uri+" : New Request "+r.getRequestURI());
				/**
				 * Put the VM that was selected to execute the request in the end of the list
				 */
		    	listofvms.remove(port);
				listofvms.add(port);
				/**
				 * Do the disconnection with the selected vm
				 */
				rsobp.doDisconnection();

		
	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	public void	sendDynamicState() throws Exception
	{
		if (this.dcibp.connected()) {
			DataDistributeDynamiqueI DataDistribute = this.getDynamicData();
			this.dcibp.send(DataDistribute) ;
		}
	}
	
	public void	sendDynamicState(final int interval,int numberOfRemainingPushes) throws Exception
		{
			this.sendDynamicState() ;
			final int fNumberOfRemainingPushes = numberOfRemainingPushes - 1 ;
			if (fNumberOfRemainingPushes > 0) {
				final RequestDispatcherPC rd = this ;
				this.Fsplist =
				this.scheduleTask(
				new ComponentI.ComponentTask() {
				public void run() {
			try {
				rd.sendDynamicState(
				interval,
				fNumberOfRemainingPushes) ;
				} catch (Exception e) {
			throw new RuntimeException(e) ;
							          }
			}
			}, interval, TimeUnit.MILLISECONDS) ;
			}
		}

	@Override
	public void startUnlimitedPushing(int interval) throws Exception {
		// TODO Auto-generated method stub
		
		final RequestDispatcherPC rd = this ;
		this.Fsplist =
		this.scheduleTaskAtFixedRate(
		new ComponentI.ComponentTask() {
		public void run() {
		try {
		rd.sendDynamicState();} 
		catch (Exception e) {
		throw new RuntimeException(e) ;
		}
		}
		}, interval, interval, TimeUnit.MILLISECONDS) ;
		
		
	}
		public void	StopthePush() throws Exception
		{
			if (this.Fsplist != null &&
			!(this.Fsplist.isCancelled() ||
			this.Fsplist.isDone())) {
			this.Fsplist.cancel(false) ;
			}
		}
	

		
		
		public void	startLimitedPushing(final int interval, final int n)
				throws Exception
				{
					assert	n > 0 ;

					this.logMessage(this.uri + " Start the Limited Push with interval "
												+ interval + " ms for " + n + " times.") ;
					final RequestDispatcherPC rd = this ;
					this.Fsplist =
					this.scheduleTask(
					new ComponentI.ComponentTask() {
									@Override
					public void run() {
					try {
					
					rd.sendDynamicState(interval, n) ;
					} catch (Exception e) {
					throw new RuntimeException(e) ;
										}
									}
					}, interval, TimeUnit.MILLISECONDS) ;
				}
		
	
	
	
    

    
	
	

}
