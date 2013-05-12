package genlab.gui.handlers;

import genlab.gui.views.MessagesView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Clears a messages view
 * 
 * @author Samuel Thiriot
 *
 */
public class ClearAllHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
	    
	    IViewPart view = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(MessagesView.ID);
	    
	    if (view == null)
	    	return null;
	    
	    try {
	    	MessagesView v = (MessagesView)view;
	    	v.getListOfMessages().clear();
	    	
	    } catch (ClassCastException e) {
	    	return null;
	    }
	    
	    return null;
	}

}
