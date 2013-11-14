package genlab.core.exec;

import genlab.core.model.exec.IAlgoExecution;

/**
 * Most tasks are provided in a quiet static way: when the user starts the execution of a workflow, 
 * then the tasks are created, and consumed by the Runner when possible.
 * Sometimes however, a too large number of tasks has to be created. 
 * Thats' the case of long loops with thousands of iterations. Creating all the tasks at startup
 * does not makes sense. In this case, the algo has to register himself as a dynamic tasks producer.
 * When there is room for novel tasks, then the Runner will call dynamic tasks producers and 
 * propose them to add their tasks.
 * 
 * @author Samuel Thiriot
 *
 */
public interface ITasksDynamicProducer {

	/**
	 * Gives a task producer the opportunity to create more tasks.
	 * Should return null when the tasks producer has finished.
	 * In this case, it will no more be called.
	 * @return
	 */
	public IAlgoExecution provideMoreTasks();
	
	/**
	 * Once this producer returns no, it will not be called any more.
	 * @return
	 */
	public boolean willMoreTasks();
	
	/**
	 * If this is true, then producer will not be asked a task right now, 
	 * but it will be proposed to act later.
	 * @return
	 */
	public boolean cannotSendTasksNow();

	
}
