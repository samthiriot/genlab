package genlab.core.exec;

import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgressSimpleListener;

import java.util.Collection;

public interface IRunner extends IComputationProgressSimpleListener {

	public void addTasks(Collection<IAlgoExecution> allTasks);

	/**
	 * Adds the task. If the task is raedy, it could be executed immediatly. 
	 * If the task is a container, then subtasks will be added recursively
	 * @param exec
	 */
	public void addTask(IAlgoExecution exec);
	
	/**
	 * Returns true if this task is known by this runner
	 * @param exec
	 * @return
	 */
	public boolean containsTask(IAlgoExecution exec);

	/**
	 * Returns all the tasks known by this runner
	 * @return
	 */
	public Collection<IAlgoExecution> getAllTasks();


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

	public int getCountPending();
	
	public int getCountReady();
	
	public int getCountRunning();

	public int getCountDone();

	public int getCountNotFinished();
	
	public String getHumanReadableState();
	
}