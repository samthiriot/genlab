package genlab.core.exec;

import genlab.core.usermachineinteraction.ListOfMessages;

public class Execution implements IExecution {

	private ListOfMessages messages = new ListOfMessages();
	
	private boolean forceExecution = false;
	
	public Execution() {
		// TODO Auto-generated constructor stub
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

}
