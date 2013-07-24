package genlab.gui.actions;

import genlab.gui.Activator;
import genlab.gui.views.MessagesViewAbstract;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

public class ClearMessagesAction extends Action implements IWorkbenchAction {

	private static final String ID = "genlab.gui.actions.clearMessages";  

	public ClearMessagesAction() {

		setId(ID);
		setText("clear console");
		setImageDescriptor(Activator.getImageDescriptor("icons/console_clear.gif"));  

	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void run() {

		IWorkbenchPart part = null;
		try {
			part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		} catch (NullPointerException e) {
			//GLLogger.errorTech("unable to find an active window, view or editor; unable to run a workflow", getClass());
			return;
		}
		   		
		if (part == null) {
			//GLLogger.errorTech("unable to find an active editor; unable to run a workflow", getClass());
			return;
		}
		if (part instanceof MessagesViewAbstract) {
		
			MessagesViewAbstract view = (MessagesViewAbstract)part;
			
			view.getListOfMessages().clear();
		}
		
	}

	
}
