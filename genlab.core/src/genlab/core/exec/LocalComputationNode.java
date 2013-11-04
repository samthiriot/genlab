package genlab.core.exec;

public class LocalComputationNode {

	private Runner runner = null;
	
	private static final LocalComputationNode singleton = new LocalComputationNode();
	
	public static final LocalComputationNode getSingleton() {
		return singleton; 
	}
	
	public Runner getRunner() {
		if (runner == null) {
			runner = new Runner();
			runner.start();
		}
		return runner ;
	}
	
}
