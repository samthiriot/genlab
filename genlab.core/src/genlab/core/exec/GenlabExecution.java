package genlab.core.exec;

import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.ExecutionHooks;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.exec.IComputationProgressSimpleListener;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.WorkflowCheckResult;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.ListsOfMessages;

public class GenlabExecution {

	public static void runBackground(final IGenlabWorkflowInstance workflow) {

		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				GLLogger.debugTech("run called", GenlabExecution.class);
				
				GLLogger.debugTech("workflow: "+workflow, GenlabExecution.class);
				if (workflow == null) {
					GLLogger.warnTech("null...", GenlabExecution.class);
					return;
				}
				
				// check workflow
				GLLogger.infoUser("checking the workflow "+workflow+"...", GenlabExecution.class);

				WorkflowCheckResult checkInfo = workflow.checkForRun();
				ListsOfMessages.getGenlabMessages().addAll(checkInfo.messages); // report errors somewhere they can be viewed !
				
				if (checkInfo.isReady()) {
					GLLogger.infoUser("ready :-)", GenlabExecution.class);
				} else {
					GLLogger.errorUser("problem..;", GenlabExecution.class);
					return;
				}
			
				IRunner r = LocalComputationNode.getSingleton().getRunner();

				Execution exec = new Execution(r);
				exec.setExecutionForced(true);

				ExecutionHooks.singleton.notifyParentTaskAdded(exec);	// TODO something clean...
				IAlgoExecution execution = workflow.execute(exec);
				TasksManager.singleton.notifyListenersOfTaskAdded(execution); // TODO something clean...

				
				GLLogger.infoUser("start run !", GenlabExecution.class);
				r.addTask(execution);

				GLLogger.infoUser("launched.", GenlabExecution.class);
				
			}
		};
	
		(new Thread(runnable)).start();
		
	}
	
	public static IComputationProgress runBlocking(IGenlabWorkflowInstance workflow, final boolean writeProgress) {
	
		IAlgoExecution exec = runBackground(workflow);
		
		final Object block = new Object();
		
		exec.getProgress().addListener(new IComputationProgressSimpleListener() {
			
			protected double previousProgress = -1;
			
			@Override
			public void computationStateChanged(IComputationProgress progress) {
				if (progress.getComputationState().isFinished()) {
					
					synchronized (block) {
						block.notifyAll();
					}
				} else if (
						writeProgress 
						&& 
						(progress.getComputationState() == ComputationState.STARTED) 
						&&
						progress.getProgressPercent() != null
						&&
						(previousProgress - progress.getProgressPercent() > 0.05)
						) {

					previousProgress = progress.getProgressPercent();
					System.err.println("progress: "+previousProgress+"%");
				
				}
				
			}
		});
		
		while (!exec.getProgress().getComputationState().isFinished()) {
			synchronized (block) {
				try {
					block.wait();
				} catch (InterruptedException e) {
				}
			}
		}
		
		return exec.getProgress();
	}
	
	private GenlabExecution() {
		
	}

}
