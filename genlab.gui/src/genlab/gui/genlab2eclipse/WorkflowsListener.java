package genlab.gui.genlab2eclipse;

import genlab.basics.workflow.IWorkflowListener;
import genlab.core.algos.IGenlabWorkflow;
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
		// TODO Auto-generated method stub
		System.err.println("a workflow was added !");
		
	}

	@Override
	public void workflowOpened(IGenlabWorkflow workflow) {
		// TODO Auto-generated method stub
		System.err.println("a workflow is opened !");
		
	}

	@Override
	public void workflowSaving(IGenlabWorkflow workflow) {
		// TODO Auto-generated method stub
		System.err.println("a workflow is saving !");
		
	}

	@Override
	public void workflowChanged(IGenlabWorkflow workflow) {
		// TODO Auto-generated method stub
		System.err.println("a workflow was changed !");

	}

	@Override
	public void workflowSaved(IGenlabWorkflow workflow) {
		// if the workflow is saved, there may be a file to update
		// TODO is not working !
		
		// we have to refresh the folder which contains the parent
		IProject eclipseProject = GenLab2eclipseUtils.getEclipseProjectForGenlabProject(workflow.getProject());
		Utils.updateCommonNavigator(
				"genlab.gui.views.projectexplorer", 
				eclipseProject.getFolder(workflow.getRelativePath())
				);
	}

}
