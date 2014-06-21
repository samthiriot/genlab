package genlab.core.exec;

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
	
	public IRunner getRunner() {
		if (runner == null) {
			runner = new Runner(useCpusCount);
			runner.start();
		}
		return runner ;
	}
	
}
