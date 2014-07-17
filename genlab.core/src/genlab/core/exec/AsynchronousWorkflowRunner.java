package genlab.core.exec;

import genlab.core.model.exec.ExecutionHooks;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.WorkflowCheckResult;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.ListsOfMessages;
import genlab.core.usermachineinteraction.MessageLevel;

/**
 * Once ran, this Runnable will start a workflow computation in background; 
 * and will publish the corresponding IAlgoExecution so the client can listen to it.
 * 
 * @author Samuel Thiriot
 *
 */
public class AsynchronousWorkflowRunner implements Runnable {

	private final IGenlabWorkflowInstance workflow;
	private final boolean forceExec;
	
	private final Object waiterExec = new Object();
	private IAlgoExecution execution = null;
	
	
	public AsynchronousWorkflowRunner(IGenlabWorkflowInstance workflow, boolean forceExec) {
		
		this.workflow = workflow;
		this.forceExec = forceExec;
	}

	@Override
	public void run() {

		//GLLogger.debugTech(" workflow: "+workflow, GenlabExecution.class);
		if (workflow == null) {
			GLLogger.warnTech("asked for the computation of a null workflow...", GenlabExecution.class);
			return;
		}
		
		// check workflow
		GLLogger.infoUser("checking the workflow "+workflow+"...", GenlabExecution.class);

		WorkflowCheckResult checkInfo = workflow.checkForRun();
		ListsOfMessages.getGenlabMessages().addAll(checkInfo.messages); // report errors somewhere they can be viewed !
		
		if (checkInfo.isReady()) {
			ListsOfMessages.getGenlabMessages().infoUser("the workflow is ready for execution :-)", GenlabExecution.class);
			checkInfo.messages.infoUser("the workflow is ready for execution :-)", GenlabExecution.class);
		} else {
			ListsOfMessages.getGenlabMessages().errorUser("the workflow is not ready for execution: please report to previous errors to solve this issue.", GenlabExecution.class);
			checkInfo.messages.errorUser("the workflow is not ready for execution: please report to previous errors to solve this issue.", GenlabExecution.class);
			return;
		}
	
		IRunner r = LocalComputationNode.getSingleton().getRunner();

		Execution exec = new Execution(r);
		exec.setExecutionForced(forceExec);
		exec.getListOfMessages().addAll(checkInfo.messages);
		exec.getListOfMessages().setFilterIgnoreBelow(MessageLevel.INFO);

		ExecutionHooks.singleton.notifyParentTaskAdded(exec);	// TODO something clean...
		IAlgoExecution execution = workflow.execute(exec);
		TasksManager.singleton.notifyListenersOfTaskAdded(execution); // TODO something clean...

		
		GLLogger.infoUser("start run !", GenlabExecution.class);
		r.addTask(execution);

		GLLogger.infoUser("launched.", GenlabExecution.class);
		
		this.execution = execution;		
	
		synchronized (waiterExec) {
			waiterExec.notify();
		}
	}
	
	/**
	 * will return the algo execution if already available, or null
	 * @return
	 */
	public IAlgoExecution getAlgoExecution() {
		return execution;
	}
	
	/**
	 * Returns the algo execution; might wait if it is not available now.
	 * @return
	 */
	public IAlgoExecution getAlgoExecutionBlocking() {
		synchronized (waiterExec) {
			if (this.execution == null) {
				synchronized (waiterExec) {
					// wait for it to be available
					try {
						this.waiterExec.wait();
					} catch (InterruptedException e) {
					}	
				}
				
			}
			return this.execution;
		}
	}

}
