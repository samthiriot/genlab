package genlab.core.model.exec;

import genlab.core.usermachineinteraction.ListOfMessages;

/**
 * Implementers emit a set of messages (warning, info...)
 * at the end of a computation.
 * 
 * @author Samuel Thiriot
 *
 */
public interface IStaticMessagesEmitter {

	public ListOfMessages getMessages();
	
}
