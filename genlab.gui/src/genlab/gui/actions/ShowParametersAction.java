package genlab.gui.actions;

import genlab.core.model.instance.IAlgoInstance;
import genlab.gui.Activator;
import genlab.gui.views.IViewAttachedToAlgoInstance;
import genlab.gui.views.ViewHelpers;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

/**
 * The action to show parameters.
 * 
 * @author Samuel Thiriot
 *
 */
public class ShowParametersAction extends Action implements IWorkbenchAction {

	private static final String ID = "genlab.gui.actions.showParameters";  

	public ShowParametersAction() {
		
		setId(ID);
		setText("open parameters");
		setImageDescriptor(Activator.getImageDescriptor("icons/gears.gif"));  

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
		if (!(part instanceof IViewAttachedToAlgoInstance)) 
			return;
		
		IViewAttachedToAlgoInstance view = (IViewAttachedToAlgoInstance)part;
		
		IAlgoInstance algoInstance = view.getAlgoInstance();
		
		if (algoInstance == null)
			return;
		
		ViewHelpers.showParametersFor(algoInstance);
		
	}


}
