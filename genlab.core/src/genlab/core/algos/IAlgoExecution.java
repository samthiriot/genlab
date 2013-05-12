package genlab.core.algos;

/**
 * Represents an atom execution. 
 * Once created, it can run of its own, meaning it stored its parameters.
 * 
 * @author Samuel Thiriot
 *
 * @param <ResultType>
 */
public interface IAlgoExecution {

	public IAlgoInstance getAlgoInstance();
	
	public IComputationProgress getProgress();
	
	public IComputationResult getResult();
	
	
	public void run();
	
}
