package genlab.core.exec;

import genlab.core.exec.client.RunnerWithDistant;
import genlab.core.exec.server.GenlabComputationServer;

public class LocalComputationNode {

	private Runner runner = null;
	
	private int useCpusCount = Runtime.getRuntime().availableProcessors();
	
	private static final LocalComputationNode singleton = new LocalComputationNode();
	
	public static final LocalComputationNode getSingleton() {
		return singleton; 
	}
	
	public void setCpusCount(int c) {
		this.useCpusCount = c;
	}
	
	private IRunner getRunnerDistance() {
		
		RunnerWithDistant runner = new RunnerWithDistant(2);
				
		runner.addRunnerDistant("gl_worker_distant_1", "localhost", 20000);
				
		return runner;

	}

	public IRunner getRunner() {
		if (runner == null) {
			
			/*
			runner = new Runner(useCpusCount);
			runner.start();
			*/
			// start the server...
			GenlabComputationServer.start();
			
			// create a runner connected as a client
			runner = (Runner) getRunnerDistance();
			
		}
		return runner ;
	}
	
	
}
