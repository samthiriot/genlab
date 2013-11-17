package genlab.core.exec;

import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgressSimpleListener;

import java.util.Collection;

public interface IRunner extends IComputationProgressSimpleListener {

	public void addTasks(Collection<IAlgoExecution> allTasks);

	public void addTask(IAlgoExecution exec);

	public void cancel();
	public void kill();

	/**
	 * Register a dynamics tasks producer, which will be called when there is no more work.
	 * @param producer
	 */
	public void registerTasksDynamicProducer(
			ITasksDynamicProducer producer);

	public void taskCleaning(ITask task);

	/**
	 * Propose to the runner a task that may be cleaned
	 */
	public void possibilityOfTaskCleanup(ITask task);

}