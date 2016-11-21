package Connectors;

import Interface.AdmissionI;
import Interface.AdmissionNotificationI;
import fr.upmc.components.connectors.AbstractConnector;

public class AdmissionNotificationConnector extends AbstractConnector implements AdmissionNotificationI {

	@Override
	public void notifyAdmission(AdmissionI a) throws Exception {
		// TODO Auto-generated method stub
		
		((AdmissionNotificationI)this.offering).notifyAdmission(a);
	}
	
	
	

}
