package genlab.gui.genlab2eclipse;

import genlab.basics.workflow.IWorkflowListener;
import genlab.core.algos.IGenlabWorkflow;
import genlab.core.projects.IGenlabProject;
import genlab.gui.Utils;

import org.eclipse.core.resources.IProject;

/**
 * Listens for workflow activity, and updates the corresponding 
 * GUI eclipse files and views accordingly.
 * 
 * @author Samuel Thiriot
 *
 */
public class WorkflowsListener implements IWorkflowListener {

	public WorkflowsListener() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void workflowCreation(IGenlabWorkflow workflow) {
		
		
	}

	@Override
	public void workflowOpened(IGenlabWorkflow workflow) {
		
		
	}

	@Override
	public void workflowSaving(IGenlabWorkflow workflow) {
		
	}

	@Override
	public void workflowChanged(IGenlabWorkflow workflow) {
		
	}

	@Override
	public void workflowSaved(IGenlabWorkflow workflow) {
		// if the workflow is saved, there may be a file to update
		
		// we have to refresh the folder which contains the parent
		IProject eclipseProject = GenLab2eclipseUtils.getEclipseProjectForGenlabProject(workflow.getProject());
		Utils.updateCommonNavigator(
				"genlab.gui.views.projectexplorer", 
				eclipseProject.getFolder(workflow.getRelativePath())
				);
	}

	@Override
	public void projectSaved(IGenlabProject project) {
		// we have to refresh the project folder
		IProject eclipseProject = GenLab2eclipseUtils.getEclipseProjectForGenlabProject(project);
		if (eclipseProject == null)
			return;
		Utils.updateCommonNavigator(
				"genlab.gui.views.projectexplorer", 
				eclipseProject.getFolder(eclipseProject.getFullPath())
				);
		
	}

}
