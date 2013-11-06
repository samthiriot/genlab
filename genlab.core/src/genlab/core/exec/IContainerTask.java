package genlab.core.exec;

import java.util.Collection;

public interface IContainerTask extends ITask, ITaskLifecycleListener {

	public void addTask(ITask t);

	public Collection<ITask> getTasks();
	
}
