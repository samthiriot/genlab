package genlab.core.exec;



/**
 * A task
 * 
 * @author Samuel Thiriot
 *
 */
public interface IExecutionTask extends ITask, Runnable  {


	/**
	 * Actually run this task
	 */
	public void run();
	
	/**
	 * Returns true if the execution task is so costless than it would be more costly to create a 
	 * thread to run it than running it directly. This case is rare.
	 * @return
	 */
	public boolean isCostless();
	
	/**
	 * May return 0, if this is just a task which waits for subtasks;
	 * typically returns 1; may return more if several cores
	 * are used by this task.
	 * Should be constant.
	 * @return
	 */
	public int getThreadsUsed();
	

}
