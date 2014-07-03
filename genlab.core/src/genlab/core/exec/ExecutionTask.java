package genlab.core.exec;


import genlab.core.commons.LoopGraphException;
import genlab.core.commons.WrongParametersException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public abstract class ExecutionTask implements IExecutionTask {

	/**
	 * lazy construction
	 */
	private HashSet<ITask> prerequires = null;
	
	private IContainerTask parent = null;
	
	private Collection<ITaskLifecycleListener> lifecycleListeners = null;

	protected Integer rank = null;
	
	public ExecutionTask() {

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<ITask> getPrerequires() {
		if (prerequires == null)
			return Collections.EMPTY_LIST;
		else
			return (Collection<ITask>) prerequires.clone();
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
		
		if (prerequires != null)
			prerequires.clear();
			
		
		// clean local data
		if (lifecycleListeners != null)
			lifecycleListeners.clear();
		parent = null;
				
	}
	

	@Override
	public final Integer getRank() {
		return rank;
	}

	public void propagateRank(Integer rank, Set<ITask> visited) {
		if (this.rank != null) {
			// suspicious: maybe there is a loop ?
			if (visited.contains(this))
				throw new LoopGraphException("loop detected in "+this+" (rank received "+rank+", previous rank "+this.rank+")");
			if (rank > this.rank)
				this.rank = rank;
		} else {
			visited.add(this);
			this.rank = rank;	
		}
		
	}
	
	@Override
	public boolean isCleanable() {
	
		return true;
	}

}
