package genlab.core.model.exec;

import java.util.Set;

import genlab.core.exec.ITask;
import genlab.core.exec.ITaskLifecycleListener;

/**
 * A listener which receives only computation states changes (big and not-so-frequent-updates)
 * 
 * @author Samuel Thiriot
 *
 */
public interface IComputationProgressSimpleListener extends ITaskLifecycleListener {

	public void computationStateChanged(IComputationProgress progress);
	
	public void propagateRank(Integer rank, Set<ITask> visited);
	
	
}
