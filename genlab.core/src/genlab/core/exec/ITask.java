package genlab.core.exec;

import genlab.core.model.exec.IComputationProgress;

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

}
