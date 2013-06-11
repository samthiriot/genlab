package genlab.core.usermachineinteraction;

public interface IListOfMessagesListener {

	/**
	 * Notifies that a message was added. 
	 * @param list
	 * @param message
	 */
	public void messageAdded(ListOfMessages list, ITextMessage message);
	
	/**
	 * More general signal: something changed (occurs when any of the other signals is raised).
	 * @param list
	 */
	public void contentChanged(ListOfMessages list);
	
	
}
