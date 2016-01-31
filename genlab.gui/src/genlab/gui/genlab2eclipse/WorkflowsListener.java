package genlab.gui.genlab2eclipse;

import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IWorkflowListener;

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
		
		// TODO should we react to the saving of workfhlow with an UI action ?
		/*
		try {
			IProject eclipseProject = GenLab2eclipseUtils.getEclipseProjectForGenlabProject(workflow.getProject());
			Utils.updateCommonNavigator(
					"genlab.gui.views.projectexplorer", 
					eclipseProject.getFolder(workflow.getRelativePath())
					);
		} catch (Throwable t) {
			GLLogger.warnTech("error while attempting to process the saving state for project "+workflow.getProject(), getClass());
		}*/
	}

	@Override
	public void workflowAutomaticallyCreatedAndFinished(
			IGenlabWorkflowInstance instance) {
		
	}

	@Override
	public void workflowLoaded(IGenlabWorkflowInstance instance) {
		
	}

}
