package genlab.core.model.exec;

import java.util.LinkedList;

import genlab.core.exec.IExecution;
import genlab.core.exec.ITask;
import genlab.core.usermachineinteraction.GLLogger;

public class ExecutionHooks {

	public final static ExecutionHooks singleton = new ExecutionHooks();
	
	private LinkedList<ITasksListener> listeners = new LinkedList<ITasksListener>();
	
	private ExecutionHooks() {
		
	}
	
	public void addListener(ITasksListener l) {
		synchronized (listeners) {
			if (!listeners.contains(l))
				listeners.add(l);	
		}
	}
	
	public void notifyParentTaskAdded(IExecution t) {
		LinkedList<ITasksListener> lBis = null;
		synchronized (listeners) {
			lBis = (LinkedList<ITasksListener>) listeners.clone();
		}
		for (ITasksListener l: lBis) {
			try {
				l.notifyParentTaskAdded(t);
			} catch (RuntimeException e) {
				GLLogger.warnTech("catched an exception during notification of task start: "+e.getMessage(), getClass(), e);
			}
		}
		
	}

}
