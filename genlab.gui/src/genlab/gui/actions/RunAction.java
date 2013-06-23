package genlab.gui.actions;

import genlab.core.model.exec.Tests;
import genlab.core.model.instance.GenlabWorkflowInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

public class RunAction extends Action implements IWorkbenchAction {

	private static final String ID = "genlab.gui.actions.run";  
	
	public RunAction() {
		setId(ID);
		setText("run workflow");
		
	}
	
	
	public void run() {  
		
		IGenlabWorkflowInstance workflow = GenlabWorkflowInstance.currentTODO;

		Tests.run(workflow);
			
	}  
	
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}


}
