package genlab.core.exec;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class ExecutionTask implements IExecutionTask {

	private Set<IExecutionTask> prerequires = new HashSet<IExecutionTask>();
	
	public ExecutionTask() {
		
	}
	
	@Override
	public Collection<IExecutionTask> getPrerequires() {
		return prerequires;
	}

	@Override
	public void addPrerequire(IExecutionTask task) {
		prerequires.add(task);
	}
	

	@Override
	public boolean isCostless() {
		return false;
	}
	

}
