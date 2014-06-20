package genlab.gui.handlers;

import genlab.core.exec.GenlabExecution;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.editors.IWorkflowEditor;
import genlab.gui.perspectives.RunPerspective;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**
 * Clears a messages view
 * 
 * @author Samuel Thiriot
 *
 */
public class RunWorkflowHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		// retrieve the workflow to run
		IEditorPart part = null;
		try {
			part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		} catch (NullPointerException e) {
			GLLogger.errorTech("unable to find an active window, view or editor; unable to run a workflow", getClass());
			return null ;
		}
		   		
		if (part == null) {
			GLLogger.errorTech("unable to find an active editor; unable to run a workflow", getClass());
			return null ;
		}
		
		IGenlabWorkflowInstance workflow = null;
		if (part instanceof IWorkflowEditor) {
		
			workflow = ((IWorkflowEditor)part).getEditedWorkflow();
			if (workflow == null) {
				GLLogger.errorTech("no workflow associated with this editor; unable to run the workflow", getClass());
				return null;
			}
					// ;GenlabWorkflowInstance.currentTODO;
			
		}
		
		// change perspective
		// TODO propose user ?
		try {
		   PlatformUI.getWorkbench().showPerspective(
				   RunPerspective.ID,       
				   PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				   );
		} catch (WorkbenchException e) {
		   e.printStackTrace();
		}
	
		// run the workflow
		GenlabExecution.runBackground(workflow);

		return null;
	}

}
