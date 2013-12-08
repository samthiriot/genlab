package genlab.gui.views;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;

/**
 * Hides the complexity of eclipse RCP view openings with simple helper functions.
 * 
 * @author Samuel Thiriot
 *
 */
public class ViewHelpers {

	private ViewHelpers() {
	}

	/**
	 * Shows the parameters views for this algo instance
	 * @param algoInstance
	 */
	public static void showParametersFor(IAlgoInstance algoInstance) {
		
		try {
			IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
					// id of the view (provided by the genlab.gui package)
					"genlab.gui.views.ParametersView",
					// if of the content (so several instances can be opened)
					algoInstance.getId(),
					IWorkbenchPage.VIEW_ACTIVATE
					);
			// transmit info to enable the view to load what is required
			WorkbenchPart v = (WorkbenchPart)view;
			v.setPartProperty(
					ParametersView.PROPERTY_PROJECT_ID, 
					algoInstance.getWorkflow().getProject().getId()
					);
			v.setPartProperty(
					ParametersView.PROPERTY_WORKFLOW_ID, 
					algoInstance.getWorkflow().getId()
					);
			v.setPartProperty(
					ParametersView.PROPERTY_ALGOINSTANCE_ID, 
					algoInstance.getId()
					);
			
		} catch (PartInitException e) {
			GLLogger.errorTech("error while attempting to open preferences: "+e.getLocalizedMessage(), ViewHelpers.class, e);
		}
	}
}
