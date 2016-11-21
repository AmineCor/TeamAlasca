package Application;

import Tests.Admission;
import Connectors.AdmissionSubmissionConnector;
import Interface.AdmissionNotificationI;
import Interface.AdmissionSubmissionI;
import ports.AdmissionSubmissionOutboundPort;
import ports.AdmissionNotificationInboundPort;
import Interface.AdmissionI;
import Interface.AdmissionNotificationHandlerI;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;


/**
 * 
 * @author Corchi-George
 *
 */
public class RGApplication  extends AbstractComponent implements AdmissionNotificationHandlerI, RequestNotificationHandlerI
{
    
	RequestGenerator rg ;
	String URI;
	AdmissionNotificationInboundPort anibp;
	AdmissionSubmissionOutboundPort asobp;
	
	
	
	public RGApplication(String uri,AbstractCVM acvm) throws Exception {
		super();
		// TODO Auto-generated constructor stub
		
		URI = uri;
		rg =
				new RequestGenerator(
						URI+"_rg",			// generator component URI
						500.0,			// mean time between two requests
						6000000000L,	// mean number of instructions in requests
						URI+"_rg_mip",
						URI+"_rg_rsobp",
						URI+"_rg_rnibp");
		acvm.addDeployedComponent(rg);
		rg.toggleLogging();
		rg.toggleTracing();
		this.addOfferedInterface(AdmissionNotificationI.class);
		this.anibp = new AdmissionNotificationInboundPort(URI+"_anibp", this) ;
		this.addPort(this.anibp) ;
		this.anibp.publishPort() ;
		
		this.addRequiredInterface(AdmissionSubmissionI.class);
		this.asobp = new AdmissionSubmissionOutboundPort(URI+"_asobp", this) ;
		this.addPort(this.asobp) ;
		this.asobp.publishPort() ;
		
		
		
	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r)
			throws Exception 
			{
				rg.acceptRequestTerminationNotification(r);
			}

	public void	startApp() throws Exception
	{
		logMessage("Start Application "+URI);
		Admission a = new Admission(URI,URI+"_rg_rnip",URI+"_anip");
		this.asobp.handleAdmissionSubmissionAndNotify(a);
	}

	public void doConnectionAdmissionControler(String asibp) throws Exception
	{
		this.asobp.doConnection(asibp, AdmissionSubmissionConnector.class.getCanonicalName());
	}

	@Override
	public void acceptAdmissionNotification(AdmissionI a) throws Exception {
		boolean response = a.isAllowed();
		
		if(response){
			logMessage("Access authorized");
			String rqURI = a.requestSubmissionInboundPortURIRep();
			rg.findPortFromURI(URI+"_rg_rsobp").doConnection(rqURI, RequestSubmissionConnector.class.getCanonicalName());
			rg.startGeneration();
		}
		else{
			logMessage("Access Denied");
		}
	}

	
		
	}

	

