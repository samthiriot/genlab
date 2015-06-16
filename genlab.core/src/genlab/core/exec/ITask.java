package genlab.core.exec;

import genlab.core.model.exec.IComputationProgress;

import java.util.Collection;
import java.util.Set;

/**
 * A task that may be running somewhere
 * 
 * TODO manage rank to be able to display tasks in a more nice way, 
 * and to detect loops
 * 
 * @author Samuel Thiriot
 *
 */
public interface ITask {

	/**
	 * Cancels the task, even if it is currently running.
	 */
	public void cancel();
	
	
	/**
	 * Kills the task. Cancellation should be tried first.
	 */
	public void kill();
	
		
	/**
	 * Returns a human readable name (one short line)
	 * @return
	 */
	public String getName(); 
	
	/**
	 * Returns a human readable description or null. 
	 * @return
	 */
	public String getDescription();
	
	
	/**
	 * Returns a progress able to describe the total progress,
	 * the current task, etc.
	 * @return
	 */
	public IComputationProgress getProgress();

	public void addPrerequire(ITask task);


	/**
	 * returns the set of execution tasks that should be ran before this one.
	 * @return
	 * 
	 */
	public Collection<ITask> getPrerequires();
	
	/**
	 * Returns the parent task (if any), or null. 
	 * A parent task is like a container.
	 * @return
	 */
	public IContainerTask getParent();
	
	public ITask getTopParent();

	
	public void setParent(IContainerTask parent);
	
	public void addLifecycleListener(ITaskLifecycleListener list);
	
	/**
	 * Ask this task to clean itself: forget internal values, etc.
	 * Note that the task may not be used after this
	 */
	public void clean();
	
	/**
	 * returns true if this task is ready to be cleaned.
	 * @return
	 */
	public boolean isCleanable();
	
	public void propagateRank(Integer rank, Set<ITask> visited);
	
	public Integer getRank();

	/**
	 * Returns the number of parents (depth in the tree)
	 * @return
	 */
	public int getDepth();
	
}

