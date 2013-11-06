package genlab.core.model.exec;

import genlab.core.exec.ITaskLifecycleListener;

/**
 * A listener which receives only computation states changes (big and not-so-frequent-updates)
 * 
 * @author Samuel Thiriot
 *
 */
public interface IComputationProgressSimpleListener extends ITaskLifecycleListener {

	public void computationStateChanged(IComputationProgress progress);
	
	
	
}
