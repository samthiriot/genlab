package genlab.core.exec.client;

import genlab.core.exec.IAlgoExecutionRemotable;
import genlab.core.exec.Runner;
import genlab.core.model.exec.IAlgoExecution;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RunnerWithDistant extends Runner {

	final BlockingQueue<IAlgoExecutionRemotable> readyToComputeRemotable = new LinkedBlockingQueue<IAlgoExecutionRemotable>();

	public RunnerWithDistant(int availableLocalThreads) {
		super(availableLocalThreads);
		
	}
	
	public void addRunnerDistant(String name, String hostname, int port) {
		
		try {
			WorkingRunnerDistanceThread thread = new WorkingRunnerDistanceThread(
					name, 
					readyToComputeRemotable, 
					hostname, 
					port
					);
			
			addWorkingThread(thread);
		} catch (RuntimeException e) {
			e.printStackTrace();
			// TODO error !
		}
	}
	

	/**
	 * Directs a task to the queue of distant execution or falls back to 
	 * standard behaviour.
	 * 
	 * @param exec
	 */
	@Override
	protected void submitTaskToWorkerThreads(IAlgoExecution exec) {
		
		if (exec instanceof IAlgoExecutionRemotable) {
			readyToComputeRemotable.add((IAlgoExecutionRemotable) exec);
		} else {
			super.submitTaskToWorkerThreads(exec);
		}
		
	}

}
