package genlab.core.exec;

import genlab.core.usermachineinteraction.ListOfMessages;

/**
 * Identifies a unique execution, which often manages sub executions.
 * Looks like an execution context.
 * 
 * @author Samuel Thiriot
 *
 */
public interface IExecution {

	/**
	 * A list of messages that is associated with this execution
	 * @return
	 */
	public ListOfMessages getListOfMessages();
	
	/**
	 * Returns true if computations have to be driven, even
	 * if their result is not used (false by default)
	 * @return
	 */
	public boolean getExecutionForced();
	
	public void setExecutionForced(boolean f);
	
	public String getId();
	
}
