package genlab.core.exec;


import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public abstract class ExecutionTask implements IExecutionTask {

	/**
	 * lazy construction
	 */
	private Set<ITask> prerequires = null;
	
	private IContainerTask parent = null;
	
	private Collection<ITaskLifecycleListener> lifecycleListeners = null;

	
	public ExecutionTask() {

	}
	
	@Override
	public Collection<ITask> getPrerequires() {
		if (prerequires == null)
			return Collections.EMPTY_LIST;
		else
			return prerequires;
	}

	@Override
	public void addPrerequire(ITask task) {
		if (prerequires == null)
			prerequires = new HashSet<ITask>();
		prerequires.add(task);
	}
	

	@Override
	public boolean isCostless() {
		return false;
	}
	

	@Override
	public IContainerTask getParent() {
		return parent;
	}


	@Override
	public void setParent(IContainerTask parent) {
		this.parent = parent;
	}

	@Override
	public ITask getTopParent() {
		if (parent == null)
			return this;
		else 
			return parent.getTopParent();
	}
	
	@Override
	public void addLifecycleListener(ITaskLifecycleListener list) {
		
		if (lifecycleListeners == null)
			lifecycleListeners = new LinkedList<ITaskLifecycleListener>();
		else if (lifecycleListeners.contains(list))
			return;
		
		lifecycleListeners.add(list);
		
	}

	@Override
	public void clean() {
		
		// warn the parent (if any)
		if (parent != null)
			parent.taskCleaning(this);
		
		// warn the lifecycle listeners (if any)
		if (lifecycleListeners != null) {
			for (ITaskLifecycleListener l: lifecycleListeners) {
				l.taskCleaning(this);
			}
		}
		
		// clean local data
		if (prerequires != null)
			prerequires.clear();
		if (lifecycleListeners != null)
			lifecycleListeners.clear();
		parent = null;
				
	}

}
