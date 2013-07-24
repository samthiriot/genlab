package genlab.gui.actions;

import genlab.core.exec.GenlabExecution;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.Activator;
import genlab.gui.editors.IWorkflowEditor;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

public class RunAction extends Action implements IWorkbenchAction {

	private static final String ID = "genlab.gui.actions.run";  
	
	public RunAction() {
		setId(ID);
		setText("run workflow");
	}
	
	
	public void run() {  
		
		IEditorPart part = null;
		try {
			part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		} catch (NullPointerException e) {
			GLLogger.errorTech("unable to find an active window, view or editor; unable to run a workflow", getClass());
			return;
		}
		   		
		if (part == null) {
			GLLogger.errorTech("unable to find an active editor; unable to run a workflow", getClass());
			return;
		}
		if (part instanceof IWorkflowEditor) {
		
			IGenlabWorkflowInstance workflow = ((IWorkflowEditor)part).getEditedWorkflow();
			if (workflow == null) {
				GLLogger.errorTech("no workflow associated with this editor; unable to run the workflow", getClass());
				return;
			}
					// ;GenlabWorkflowInstance.currentTODO;

			GenlabExecution.runBackground(workflow);
			
		}
		
			
	}  
	
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}


}
