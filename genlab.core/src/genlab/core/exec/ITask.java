package genlab.core.exec;

import genlab.core.model.exec.IComputationProgress;

import java.util.Collection;

/**
 * A task that may be running somewhere
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
	
}

