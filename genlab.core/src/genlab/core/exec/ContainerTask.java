package genlab.core.exec;

import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class ContainerTask implements IContainerTask {

	private LinkedList<ITask> tasks = new LinkedList<ITask>();
	private Set<ITask> prerequires = new HashSet<ITask>();

	private IContainerTask parent = null;
	
	private final String name;

	public ContainerTask(String name) {
		this.name = name;
	}

	@Override
	public void cancel() {

		// cancel contained subtasks
		for (ITask t: tasks) {
			try {
				t.cancel();
			} catch (RuntimeException e) {
				GLLogger.errorTech("error while cancelling task "+t+": "+e.getMessage(), getClass(), e);
			}
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IComputationProgress getProgress() {
		// TODO change
		return new ComputationProgressWithSteps();
	}


	@Override
	public IContainerTask getParent() {
		return parent;
	}

	@Override
	public void addTask(ITask t) {
		tasks.add(t);
		t.setParent(this);
	}

	@Override
	public Collection<ITask> getTasks() {
		return tasks;
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
	


}
