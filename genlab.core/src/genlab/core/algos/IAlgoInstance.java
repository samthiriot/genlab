package genlab.core.algos;

import java.util.Map;

public interface IAlgoInstance {

	public String getId();
	
	public IAlgo getAlgo();
	
	/**
	 * Start the execution.
	 * @return
	 */
	public IAlgoExecution execute(Map<IInputOutput,Object> inputs);
	
}
