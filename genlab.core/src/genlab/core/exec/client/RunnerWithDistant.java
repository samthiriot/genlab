package genlab.core.exec.client;

import genlab.core.exec.Runner;

public class RunnerWithDistant extends Runner {

	public RunnerWithDistant(int availableLocalThreads) {
		super(availableLocalThreads);
		
	}
	
	public void addRunnerDistant(String name, String hostname, int port) {
		
		try {
			WorkingRunnerDistanceThread thread = new WorkingRunnerDistanceThread(
					name, 
					readyToComputeWithThreads, 
					hostname, 
					port
					);
			
			addWorkingThread(thread);
		} catch (RuntimeException e) {
			e.printStackTrace();
			// TODO error !
		}
	}

}
