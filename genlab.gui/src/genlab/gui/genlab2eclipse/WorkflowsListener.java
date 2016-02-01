package genlab.gui.genlab2eclipse;

import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IWorkflowListener;
import genlab.core.usermachineinteraction.GLLogger;

/**
 * Listens for workflow activity, and updates the corresponding 
 * GUI eclipse files and views accordingly.
 * 
 * @author Samuel Thiriot
 *
 */
public class WorkflowsListener implements IWorkflowListener {

	public WorkflowsListener() {

	}

	@Override
	public void workflowCreation(IGenlabWorkflowInstance workflow) {
		
		
	}

	@Override
	public void workflowOpened(IGenlabWorkflowInstance workflow) {
		
		
	}

	@Override
	public void workflowSaving(IGenlabWorkflowInstance workflow) {
		
	}

	@Override
	public void workflowChanged(IGenlabWorkflowInstance workflow) {
		
	}

	@Override
	public void workflowSaved(IGenlabWorkflowInstance workflow) {
		// if the workflow is saved, there may be a file to update
		
		// we have to refresh the folder which contains the parent
		
		try {
			GenLab2eclipseUtils.getFileForWorkflow(workflow).getParent().refreshLocal(1, null);
		} catch (Throwable t) {
			GLLogger.warnTech("error while attempting to refresh the folder containing the workflow "+workflow.getAbsolutePath(), getClass());
		}
	}

	@Override
	public void workflowAutomaticallyCreatedAndFinished(
			IGenlabWorkflowInstance instance) {
		
	}

	@Override
	public void workflowLoaded(IGenlabWorkflowInstance instance) {
		
	}

}
