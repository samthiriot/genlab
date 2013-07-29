package genlab.gui.actions;

import genlab.gui.Activator;
import genlab.gui.views.TasksProgressView;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

/**
 * Clears a list of messagesd
 * 
 * @author Samuel Thiriot
 *
 */
public class ClearProgressAction extends Action implements IWorkbenchAction {

	private static final String ID = "genlab.gui.actions.clearProgress";  

	public ClearProgressAction() {

		setId(ID);
		setText("clear progress");
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
		if (part instanceof TasksProgressView) {
		
			TasksProgressView view = (TasksProgressView)part;
			
			view.clearFinished();
		}
		
	}

	
}
