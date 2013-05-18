package genlab.core.algos;

import genlab.core.IStaticMessagesEmitter;

import java.util.Map;

/**
 * Result of a computation. Notably transmits warnings and info, 
 * and of course results
 * 
 * TODO type of the result
 * 
 * @author Samuel Thiriot
 */
public interface IComputationResult extends IStaticMessagesEmitter {

	/**
	 * Returns the algo which generated this result
	 * @return
	 */
	public IAlgo getOriginalAlgo();
	
	/**
	 * Returns the progress of this computation result.
	 * Enables to know how long it took to run, for instance;
	 * @return
	 */
	public IComputationProgress getProgress();
	
	public Map<IInputOutput<?>,Object> getResults();
	
}
