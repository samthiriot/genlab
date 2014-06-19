package genlab.core.exec;

import genlab.core.commons.ProgramException;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Tracks the tasks currently running and the runners currently referenced.
 * 
 * @author Samuel Thiriot
 */
public class TasksManager {

	/**
	 * list of listeners willing updates about tasks
	 */
	private final LinkedList<ITaskManagerListener> listeners = new LinkedList<ITaskManagerListener>();
	
	/**
	 * list of runners declared currently
	 */
	private final LinkedList<IRunner> runners = new LinkedList<IRunner>();
	
	
	public static TasksManager singleton = new TasksManager();
	
	public void addRunner(IRunner runner) {
		synchronized (runners) {
			if (!runners.contains(runner))
				runners.add(runner);
		}
	}
	
	public void removeRunner(IRunner runner) {
		synchronized (runners) {
			runners.remove(runner);
		}
	}
	
	public Collection<IRunner> getRunners() {
		synchronized (runners) {
			return (Collection<IRunner>) runners.clone();
		}
	}
	
	public TasksManager() {

	}
	
	
	public void addListener(ITaskManagerListener listener) {
		synchronized (listeners) {
			if (!listeners.contains(listener))
				listeners.add(listener);
		}
	}

	public void removeListener(ITaskManagerListener listener) {
		synchronized (listeners) {
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
