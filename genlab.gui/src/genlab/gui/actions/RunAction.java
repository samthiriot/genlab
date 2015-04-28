package genlab.gui.actions;

import genlab.core.exec.GenlabExecution;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.Utils;
import genlab.gui.editors.IWorkflowEditor;
import genlab.gui.perspectives.RunPerspective;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

public class RunAction extends Action implements IWorkbenchAction {

	private static final String ID = "genlab.gui.actions.run";  
	
	public RunAction() {
		setId(ID);
		setText("run workflow");
	}
	
	
	public void run() {  
		
		System.err.println("EXEC: retrieve workflow...");
		IGenlabWorkflowInstance workflow = Utils.getSelectedWorflow();
		if (workflow == null)
			return;
		
		System.err.println("EXEC: change perspective...");
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
		System.err.println("EXEC: run background...");
		GenlabExecution.runBackground(workflow);

	}  
	
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}


}
