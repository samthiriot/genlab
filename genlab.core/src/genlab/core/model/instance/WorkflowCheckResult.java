package genlab.core.model.instance;

import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.IListOfMessagesListener;
import genlab.core.usermachineinteraction.ITextMessage;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.MessageLevel;

/**
 * The result of the checking of an workflow for run: a list of messages
 * and an overall result (if a problem is detected, that is a level error,
 * the run can not be send).
 * 
 * @author Samuel Thiriot
 *
 */
public class WorkflowCheckResult implements IListOfMessagesListener {

	private MessageLevel highestLevel = MessageLevel.getLowest();
	
	/**
	 * list of checking messages. Should be used to add messages for checking.
	 */
	public final ListOfMessages messages = new ListOfMessages();

	
	public WorkflowCheckResult() {
		messages.addListener(this); // listen for our list of messages
	}

	@Override
	public void contentChanged(ListOfMessages list) {
		// don't care of such a general message
	}

	@Override
	public void messageAdded(ListOfMessages list, ITextMessage message) {
		
		// only listen our messages
		if (list != messages)
			return;
		
		// check that the message is of the good type
		highestLevel = MessageLevel.max(highestLevel, message.getLevel());
		
	}
	
	public boolean isReady() {
				
		return highestLevel.compareTo(MessageLevel.ERROR) <= 0;
	}
	
}
