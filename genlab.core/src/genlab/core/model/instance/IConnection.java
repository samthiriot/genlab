package genlab.core.model.instance;

import genlab.core.model.IGenlabResource;

public interface IConnection extends IGenlabResource {

	public IInputOutputInstance getFrom();
	
	public IInputOutputInstance getTo();
	
	/**
	 * Convenience method that relies on from data
	 * @return
	 */
	public IGenlabWorkflowInstance getWorkflow();

	
	/**
	 * compute the precomputed value by asking it to the source algo
	 * @return
	 */
	public Object getPrecomputedValue();
	
	
}
