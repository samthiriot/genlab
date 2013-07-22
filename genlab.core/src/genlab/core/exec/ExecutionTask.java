package genlab.core.exec;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class ExecutionTask implements IExecutionTask {

	private Set<ITask> prerequires = new HashSet<ITask>();
	
	private IContainerTask parent = null;
	
	public ExecutionTask() {

	}
	
	@Override
	public Collection<ITask> getPrerequires() {
		return prerequires;
	}

	@Override
	public void addPrerequire(ITask task) {
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



}
