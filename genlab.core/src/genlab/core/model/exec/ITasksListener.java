package genlab.core.model.exec;

import genlab.core.exec.IExecution;

/**
 * Listens for tasks: task queued, started, finished...
 * 
 * @author Samuel Thiriot
 *
 */
public interface ITasksListener {

	public void notifyParentTaskAdded(IExecution task);
	
}
