package genlab.core.model.exec;

import genlab.core.exec.IExecution;
import genlab.core.exec.IExecutionTask;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IInputOutputInstance;

import java.util.Collection;
import java.util.Map;

/**
 * Represents an atomic execution of an algo 
 * Once created, it can run of its own, meaning it stored its parameters.
 * 
 * @author Samuel Thiriot
 *
 * @param <ResultType>
 */
public interface IAlgoExecution extends IExecutionTask, IDumpAsExecutionNetwork {

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
	public void setResult(IComputationResult res);
	
	//public int getPreferedCountThreads();


	/**
	 * Can be called any time before, during or after the computation
	 * @return
	 */
	public IComputationProgress getProgress();

	/**
	 * Returns the execution in for which this algo exec was created.
	 * @return
	 */
	public IExecution getExecution();

	/**
	 * Initialize the algo execution: provides enough information
	 * to create the exec connections that link all the exec instances.
	 * The algo exec should create there the executable connections
	 * @param instance2exec
	 */
	public void initInputs(Map<IAlgoInstance,IAlgoExecution> instance2exec);
	
	/**
	 * Returns a max for which, given the current parameters, the algo can be said to be died.
	 * Returning -1 disables watchdog; this is a bad practice.
	 * @return
	 */
	public long getTimeout();

	/**
	 * Asks the execution to reset its internal state so it will run again. 
	 */
	public void reset();
	
	/**
	 * Returns the list of executable connections for a given input.
	 * @param input
	 * @return
	 */
	public Collection<IConnectionExecution> getConnectionsForInput(IInputOutputInstance input);

	public boolean containedInto(IAlgoExecution other);
	
}
