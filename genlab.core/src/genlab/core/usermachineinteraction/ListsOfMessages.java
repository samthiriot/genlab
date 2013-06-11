package genlab.core.usermachineinteraction;

import java.util.HashMap;
import java.util.Map;

import genlab.core.model.exec.IGenLabExecution;

public class ListsOfMessages {

	private static Map<IGenLabExecution,ListOfMessages> execution2listOfMessages = new HashMap<IGenLabExecution, ListOfMessages>();
	
	private static ListOfMessages applicationListOfMessage = new ListOfMessages();
	
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
