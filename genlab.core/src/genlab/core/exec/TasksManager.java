package genlab.core.exec;

import genlab.core.usermachineinteraction.GLLogger;

import java.util.LinkedList;

/**
 * Tracks the tasks currently running.
 * 
 * @author Samuel Thiriot
 */
public class TasksManager {

	private LinkedList<ITaskManagerListener> listeners = new LinkedList<ITaskManagerListener>();
	
	
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
	
	protected void notifyListenersOfTaskAdded(ITask t) {
		
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
