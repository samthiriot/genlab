package genlab.core.model.exec;

import genlab.core.model.instance.IConnection;

public interface IConnectionExecution extends IComputationProgressSimpleListener {

	/**
	 * Used by the target algo exec to retrieve the value
	 * @return
	 */
	public Object getValue();

	public void computationStateChanged(IComputationProgress progress);

	public void forceValue(Object value);

	public IConnection getConnection();
	
	public IAlgoExecution getFrom();
	public IAlgoExecution getTo();
	
	/**
	 * Ask the connection to clean itself (meaning: its internal value)
	 */
	public void clean();
	
}