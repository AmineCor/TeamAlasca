package com.teamalasca.applicationvm;

import java.util.ArrayList;
import java.util.List;

import com.teamalasca.applicationvm.interfaces.ApplicationVMManagementI;
import com.teamalasca.applicationvm.ports.ApplicationVMManagementInboundPort;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;

/**
 * This is a new version for the component @see {@link fr.upmc.datacenter.software.applicationvm.ApplicationVM}.
 */
public class ApplicationVM extends fr.upmc.datacenter.software.applicationvm.ApplicationVM implements ApplicationVMManagementI{
	
	private List<AllocatedCore> releasedCores = new ArrayList<>();

	public ApplicationVM(String vmURI,
			String vmApplicationVMManagementInboundPortURI,
			String vmRequestSubmissionInboundPortURI,
			String vmRequestNotificationOutboundPortURI) throws Exception {

		super(vmURI,vmApplicationVMManagementInboundPortURI,vmRequestSubmissionInboundPortURI,vmRequestNotificationOutboundPortURI);

		this.applicationVMManagementInboundPort.unpublishPort();
		this.applicationVMManagementInboundPort.destroyPort();

		//Adding required and offered interfaces
		this.addOfferedInterface(ApplicationVMManagementI.class);
		this.applicationVMManagementInboundPort = new ApplicationVMManagementInboundPort(vmApplicationVMManagementInboundPortURI, this);
		this.addPort(this.applicationVMManagementInboundPort);
		this.applicationVMManagementInboundPort.publishPort();
	}

	/**
	 * Release a core. It will release the core state in the computer too.
	 */
	@Override
	public void releaseCore(AllocatedCore ac) throws Exception {
		Boolean idle = this.allocatedCoresIdleStatus.remove(ac);
		if(!idle){
			releasedCores.add(ac);
		}
		else{
		}
	}

	@Override
	public void			endTask(TaskI t) throws Exception
	{
		assert	t != null && this.isRunningTask(t) ;

		this.logMessage(this.vmURI + " terminates request " +
				t.getRequest().getRequestURI()) ;
		AllocatedCore ac = this.runningTasks.remove(t.getTaskURI()) ;
		this.allocatedCoresIdleStatus.remove(ac) ;
		if(!releasedCores.contains(ac)){
			this.allocatedCoresIdleStatus.put(ac, true);
		}
		else{
			releasedCores.remove(ac);
		}
			
		if (this.tasksToNotify.contains(t.getTaskURI())) {
			this.tasksToNotify.remove(t.getTaskURI()) ;
			this.requestNotificationOutboundPort.
			notifyRequestTermination(t.getRequest()) ;
		}
		if (!this.taskQueue.isEmpty()) {
			this.startTask() ;
		}
	}

	@Override
	public void dispose() throws Exception {
		this.shutdown();	
	}

}
