package genlab.core.model.exec;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IInputOutputInstance;

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
	public IAlgoInstance getOriginalAlgo();
	
	/**
	 * Returns the progress of this computation result.
	 * Enables to know how long it took to run, for instance;
	 * @return
	 */
	public IComputationProgress getProgress();
	
	public Map<IInputOutputInstance,Object> getResults();
	
	/**
	 * Cleans the data (possibly, a large space in memory).
	 */
	public void clean();

	/**
	 * Returns something which, in the case of repeated updates of 
	 * the same result, helps to match results sent at the same time / step / logical step.
	 * @return
	 */
	public Object getWave();
}
