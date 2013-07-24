package genlab.core.exec;

import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.exec.IComputationProgressSimpleListener;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.WorkflowCheckResult;
import genlab.core.usermachineinteraction.GLLogger;

public class GenlabExecution {

	public static IAlgoExecution runBackground(IGenlabWorkflowInstance workflow) {

		GLLogger.debugTech("run called", GenlabExecution.class);
		
		GLLogger.debugTech("workflow: "+workflow, GenlabExecution.class);
		if (workflow == null) {
			GLLogger.warnTech("null...", GenlabExecution.class);
			return null;
		}
		
		// check workflow
		GLLogger.infoUser("checking the workflow "+workflow+"...", GenlabExecution.class);

		WorkflowCheckResult checkInfo = workflow.checkForRun();
		if (checkInfo.isReady()) {
			GLLogger.infoUser("ready :-)", GenlabExecution.class);
		} else {
			GLLogger.errorUser("problem..;", GenlabExecution.class);
			return null;
		}
	
		Execution exec = new Execution();
		exec.setExecutionForced(true);

		IAlgoExecution execution = workflow.execute(exec);

		GLLogger.infoUser("start run !", GenlabExecution.class);

		Thread t = new Thread(execution);
		t.start();

		GLLogger.infoUser("launched.", GenlabExecution.class);
		
		return execution;
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
