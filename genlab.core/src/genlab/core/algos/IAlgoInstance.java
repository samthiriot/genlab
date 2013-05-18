package genlab.core.algos;

import genlab.core.IGenlabResource;

import java.util.Map;

public interface IAlgoInstance extends IGenlabResource {

	public String getId();
	
	public IAlgo getAlgo();
	
	/**
	 * Start the execution.
	 * @return
	 */
	public IAlgoExecution execute(Map<IInputOutput,Object> inputs);
	
	public IGenlabWorkflow getWorkflow();
	
	
}
