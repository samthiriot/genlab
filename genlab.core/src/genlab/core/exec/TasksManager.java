package genlab.core.exec;

import genlab.core.commons.ProgramException;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.LinkedList;

/**
 * Tracks the tasks currently running.
 * 
 * @author Samuel Thiriot
 */
public class TasksManager {

	private final LinkedList<ITaskManagerListener> listeners = new LinkedList<ITaskManagerListener>();
	
	public static TasksManager singleton = new TasksManager();
	
	public TasksManager() {

	}
	
	
	public void addListener(ITaskManagerListener listener) {
		synchronized (listener) {
			if (!listeners.contains(listener))
				listeners.add(listener);
		}
	}

	public void removeListener(ITaskManagerListener listener) {
		synchronized (listener) {
			listeners.remove(listener);
		}
	}
	
	public void notifyListenersOfTaskAdded(ITask t) {
		
		if (t == null)
			throw new ProgramException("task should not be null");
			
		synchronized (listeners) {
			for (ITaskManagerListener l : listeners) {
				try {
					l.notifyTaskAdded(t);
				} catch (RuntimeException e) {
					GLLogger.errorTech("catched an error while dispatching event 'task added': "+e.getMessage(), getClass(), e);
				}
			}
		}
	}


	protected void notifyListenersOfTaskRemoved(ITask t) {
	
		if (t == null)
			throw new ProgramException("task should not be null");
		
		synchronized (listeners) {
			for (ITaskManagerListener l : listeners) {
				try {
					l.notifyTaskRemoved(t);
				} catch (RuntimeException e) {
					GLLogger.errorTech("catched an error while dispatching event 'task removed': "+e.getMessage(), getClass(), e);
				}
			}
		}
	}

}
