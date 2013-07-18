package genlab.core.exec;

import genlab.core.commons.UniqueTimestamp;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;

public class Execution implements IExecution {

	private final ListOfMessages messages;
	
	private boolean forceExecution = false;
	
	private final UniqueTimestamp stamp;
	
	
	public Execution() {
		
		// init stamp 
		stamp = new UniqueTimestamp();
		
		// create messages 
		messages = new ListOfMessages();
		
		// and register them !
		ListsOfMessages.registerListOfMessages(getId(), messages);
		
	}

	@Override
	public ListOfMessages getListOfMessages() {
		return messages;
	}


	@Override
	public boolean getExecutionForced() {
		return forceExecution;
	}

	@Override
	public void setExecutionForced(boolean f) {
		this.forceExecution = f;		
	}

	@Override
	public String getId() {
		return "execution_"+stamp.toString();
	} 


}
