package genlab.core.model.exec;

import java.util.Map;

import genlab.core.exec.IExecution;
import genlab.core.exec.IExecutionTask;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IInputOutputInstance;

/**
 * Represents an atomic execution of an algo 
 * Once created, it can run of its own, meaning it stored its parameters.
 * 
 * @author Samuel Thiriot
 *
 * @param <ResultType>
 */
public interface IAlgoExecution extends IExecutionTask {

	/**
	 * Returns the Algo instance executed there
	 * @return
	 */
	public IAlgoInstance getAlgoInstance();
	
	/**
	 * Returns the result, or null if none (at this time, that is in this progress state)
	 * @return
	 */
	public IComputationResult getResult();
	
	
	//public int getPreferedCountThreads();


	/**
	 * Can be called any time before, during or after the computation
	 * @return
	 */
	public IComputationProgress getProgress();
	
	public IExecution getExecution();

	/**
	 * Called during execution when an input became available
	 * @param to
	 */
	public void notifyInputAvailable(IInputOutputInstance to);
	
	/**
	 * Initialize the algo execution: provides enough information
	 * to create the exec connections that link all the exec instances.
	 * @param instance2exec
	 */
	public void initInputs(Map<IAlgoInstance,IAlgoExecution> instance2exec);
	
}
