
package Tests;


import comRequestDispatcher.RequestDispatcher;

import Connectors.AdmissionNotificationConnector;
import Interface.AdmissionI;
import Interface.AdmissionNotificationI;
import Interface.AdmissionSubmissionHandlerI;
import Interface.AdmissionSubmissionI;
import ports.AdmissionNotificationOutboundPort;
import ports.AdmissionSubmissionInboundPort;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStateDataConsumerI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;


public class AdmissionControler extends AbstractComponent
implements AdmissionSubmissionHandlerI,ComputerStateDataConsumerI {

	private static final int CORES_NUMBER_THRESHOLD = 4;

	private AdmissionNotificationOutboundPort anobp;

	private AdmissionSubmissionInboundPort asibp;

	protected ComputerDynamicStateDataOutboundPort		cdsPort ;

	protected AbstractCVM acvm;

	protected String ac_uri;

	protected boolean[][] ressources;

	Object ressources_verrou;

	boolean computerStateInitialized;



	/** Port connected to the  computer component to access its services.	*/
	protected ComputerServicesOutboundPort				csPort ;


	public AdmissionControler(AbstractCVM acvm, String ac_uri, 
			String csPortURI, String cdsPortURI, String computerURI) throws Exception {
		super();
		this.ac_uri=ac_uri;

		// Create Admissions ports
		this.anobp = new AdmissionNotificationOutboundPort(ac_uri+"_anobp",this);
		this.asibp = new AdmissionSubmissionInboundPort(ac_uri+"_asibp",this);
		this.addOfferedInterface(AdmissionSubmissionI.class);
		this.addRequiredInterface(AdmissionNotificationI.class);
		this.addPort(anobp);
		this.addPort(asibp);
		this.anobp.publishPort();
		this.asibp.publishPort();

		// Create Computer ports outbound ports
		this.csPort = new ComputerServicesOutboundPort(
				ac_uri+"_csop",
				new AbstractComponent() {}) ;
		this.addPort(this.csPort);
		this.csPort.publishPort() ;
		this.csPort.doConnection(
				csPortURI,
				ComputerServicesConnector.class.getCanonicalName()) ;

		// Create Computer Dynamic Data State Port 
		this.cdsPort = new ComputerDynamicStateDataOutboundPort(
				ac_uri+"_cdsdop",
				this,
				computerURI) ;
		this.addPort(cdsPort) ;
		this.cdsPort.publishPort() ;	
		this.cdsPort.
		doConnection(
				cdsPortURI,
				ControlledDataConnector.class.getCanonicalName()) ;
		this.cdsPort.startUnlimitedPushing(1000);

		this.acvm=acvm;
		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class) ;

		ressources = null;
		ressources_verrou = new Object();
		computerStateInitialized = false;

	}


	@Override
	public void acceptComputerStaticData(String computerURI, ComputerStaticStateI staticState) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void acceptComputerDynamicData(String computerURI, ComputerDynamicStateI currentDynamicState)
			throws Exception 
			{

		synchronized (ressources_verrou)
		{
			ressources = currentDynamicState.getCurrentCoreReservations();
			ressources_verrou.notify();
		}

		StringBuffer sb = new StringBuffer("");
		for (int p = 0 ; p < ressources.length ; p++) {
			if (p == 0) {
				sb.append("  reserved cores           : ") ;
			} else {
				sb.append("                             ") ;
			}
			for (int c = 0 ; c < ressources[p].length ; c++) {
				if (ressources[p][c]) {
					sb.append("t ") ;
				} else {
					sb.append("f ") ;
				}
			}
		}

		logMessage(sb.toString());


			}

	@Override
	public void handleAdmissionSubmission(AdmissionI a) throws Exception {
		handleAdmissionSubmissionAndNotify(a);	}

	@Override
	public void handleAdmissionSubmissionAndNotify(AdmissionI a) throws Exception {
		// TODO Auto-generated method stub
		logMessage("admission submission handled");


		if( canCreateVM())
		{

			allocateRessources(a);

			anobp.doConnection(a.getAppUri()+"_anibp",AdmissionNotificationConnector.class.getCanonicalName());			

			anobp.acceptAdmissionNotification(a);

			anobp.doDisconnection();	


		}
		else
		{
			a.setAllowed(false);
			logMessage("Admission Controller : "+a.getAppUri()+" not allowed");
			anobp.doConnection(a.AdmissionNotificationInboundPort(),AdmissionNotificationConnector.class.getCanonicalName());			

			anobp.acceptAdmissionNotification(a);

			anobp.doDisconnection();
		}

	}

	public boolean canCreateVM()
	{
		try 
		{
			synchronized (ressources_verrou)
			{
				ressources_verrou.wait();
			}
		}
		catch (Exception e)
		{

		}

		int compte = 0;
		for (int i=0; i< ressources.length; i++)
		{
			for (int j=0; j<ressources[i].length; j++)
			{
				if (!ressources[i][j]) compte++;
			}
		}
		return (compte>=CORES_NUMBER_THRESHOLD);

	}

	public ApplicationVM get_vm(String AppUri) throws Exception
	{
		ApplicationVM vm =
				new ApplicationVM(AppUri+"_vm",	// application vm component URI
						AppUri+"_vm_apmip",
						AppUri+"_vm_rsibp",
						AppUri+"_vm_rnobp") ;
		acvm.addDeployedComponent(vm) ;

		// Create a mock up port to manage the AVM component (allocate cores).
		ApplicationVMManagementOutboundPort avmPort = new ApplicationVMManagementOutboundPort(
				ac_uri+"amop",
				new AbstractComponent() {}) ;

		avmPort.publishPort() ;
		avmPort.
		doConnection(
				AppUri+"_vm_apmip",
				ApplicationVMManagementConnector.class.getCanonicalName()) ;

		AllocatedCore[] ac = this.csPort.allocateCores(4) ;
		avmPort.allocateCores(ac) ;
		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		vm.toggleTracing() ;
		vm.toggleLogging() ;

		return vm;

	}

	public RequestDispatcher get_rr (String AppUri) throws Exception
	{
		RequestDispatcher rr = new RequestDispatcher("_rr_rsobp","_rr_rsibp", AppUri+"_rr", "rr_rnibp","rr_outbound1");
		acvm.addDeployedComponent(rr);

		rr.toggleTracing();
		rr.toggleLogging();

		return rr;

	}

	protected void allocateRessources(AdmissionI a) throws Exception
	{
		logMessage("Admission Controller : "+a.getAppUri()+" allowed"+ a.getAppUri()+"_vm");

		a.setRsibp_vm(a.getAppUri()+"_vm_rsibp");
		a.setAllowed(true);
		a.setRsibp_vm(a.getAppUri()+"_rr_rsibp");

		a.setRsibp("_rr_rsibp");


		ApplicationVM vm = get_vm(a.getAppUri());
		RequestDispatcher rr = get_rr (a.getAppUri());
		rr.associate(a.getAppUri()+"_vm_rsibp");

		vm.findPortFromURI(a.getAppUri()+"_vm_rnobp")
		.doConnection
		("rr_rnibp",
				RequestNotificationConnector.class.getCanonicalName());

		rr.findPortFromURI("rr_outbound1")
		.doConnection
		(a.getAppUri()+"_rg_rnibp",
				RequestNotificationConnector.class.getCanonicalName());
	}
}
