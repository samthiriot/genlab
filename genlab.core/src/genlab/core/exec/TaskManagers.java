package genlab.core.exec;

/**
 * Standard, minimal task managers
 * @author Samuel Thiriot
 *
 */
public class TaskManagers {


	protected static final TaskManagers singleton = new TaskManagers();
	
	public static TaskManagers getTaskManagers() {
		return singleton;
	}
	
	/**
	 * The task manager that references startup tasks.
	 * Could be listen by a GUI in order to display the progress
	 * status.
	 */
	public final TasksManager startupTasks = new TasksManager();
	
	
	private TaskManagers() {

	}
	
	
	
}
