package genlab.core.usermachineinteraction;

import genlab.core.model.exec.IGenLabExecution;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO add a "end" or "destroyed" event for a list of messages, so we can clear it.
 * 
 * @author Samuel Thiriot
 *
 */
public class ListsOfMessages {

	private static Map<IGenLabExecution,ListOfMessages> execution2listOfMessages = new HashMap<IGenLabExecution, ListOfMessages>();
	
	private static ListOfMessages applicationListOfMessage = new ListOfMessages(MessageLevel.WARNING, MessageLevel.WARNING, 1000);

	private static Map<String,ListOfMessages> id2listOfMessages = new HashMap<String, ListOfMessages>(50);
	
	public static void registerListOfMessages(String id, ListOfMessages m) {
		id2listOfMessages.put(id, m);
	}
	
	public static ListOfMessages getListOfMessages(String id) {
		return id2listOfMessages.get(id);
	}
	
	public static ListOfMessages getGenlabMessages() {
		return applicationListOfMessage;
	}
	
	public static ListOfMessages getListOfMessagesForExecution(IGenLabExecution exec) {
		ListOfMessages m = execution2listOfMessages.get(exec);
		if (m == null)
			m = new ListOfMessages();
		return m;
	}
	
	public static void clearMessagesForExecution(IGenLabExecution exec) {
		// TODO notifications !
		execution2listOfMessages.remove(exec);
	}
	
	public static void clearAllMessages() {
		execution2listOfMessages.clear();
	}
	
	/**
	 * private constructor
	 */
	private ListsOfMessages() {};

}
