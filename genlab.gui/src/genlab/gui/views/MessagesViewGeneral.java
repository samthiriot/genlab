package genlab.gui.views;

import genlab.core.usermachineinteraction.ListsOfMessages;

/**
 * Displays messages as a table. 
 * Listens for general messages.
 * 
 * TODO add exportation to a text file ?!
 * 
 * @author Samuel Thiriot
 *
 */
public class MessagesViewGeneral extends MessagesViewAbstract  {

	public static final String ID = "genlab.gui.views.MessagesViewGeneral";
	
	
	public MessagesViewGeneral() {
	}

	protected void listenMessages() {
		
		
		messages = ListsOfMessages.getGenlabMessages();
		setPartName("Messages");
		
		if (messages != null) {
			// ... listen for data
			viewer.setContentProvider(new MessagesContentProvider(messages));
			viewer.setInput("toto");
			
			messages.addListener(listener);
			
		}
	
	}


}
