package genlab.core.exec;

import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.IGenlabWorkflowInstance;

public class GenlabExecution {

	public static IAlgoExecution runBackground(IGenlabWorkflowInstance workflow) {
		return runBackground(workflow, false);
	}

	public static IAlgoExecution runBackground(IGenlabWorkflowInstance workflow, boolean forceExec) {

		AsynchronousWorkflowRunner runnable = new AsynchronousWorkflowRunner(workflow, forceExec);
		/*
		Runnable runnable = new Runnable() {
			
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
				
			}
		};
		*/
	
		(new Thread(runnable)).start();
		
		return runnable.getAlgoExecutionBlocking();
	}
	
	
	private GenlabExecution() {
		
	}

}
