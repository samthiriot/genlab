package genlab.core.model.exec;

public enum ComputationState {

	/**
	 * The task was just created
	 */
	CREATED,
	
	/**
	 * The task is still waiting for data or another task
	 */
	WAITING_DEPENDENCY,
	
	/**
	 * The task is ready and can be run.
	 */
	READY,
	
	/**
	 * The task was started and is running.
	 */
	STARTED,
	
	FINISHED_CANCEL,
	
	FINISHED_OK,
	
	FINISHED_FAILURE;
	
}
