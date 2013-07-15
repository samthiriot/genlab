package genlab.core.model.instance;

import genlab.core.projects.IGenlabProject;

/**
 * Describes listeners interested in the lifecycle of workflows
 * 
 * @author Samuel Thiriot
 *
 */
public interface IWorkflowListener {

	public void workflowCreation(IGenlabWorkflowInstance workflow);
	
	public void workflowChanged(IGenlabWorkflowInstance workflow);

	public void workflowOpened(IGenlabWorkflowInstance workflow);
	
	public void workflowSaving(IGenlabWorkflowInstance workflow);

	public void workflowSaved(IGenlabWorkflowInstance workflow);

	public void projectSaved(IGenlabProject project);

}
