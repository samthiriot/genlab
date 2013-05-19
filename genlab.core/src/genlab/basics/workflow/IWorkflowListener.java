package genlab.basics.workflow;

import genlab.core.algos.IGenlabWorkflow;

public interface IWorkflowListener {

	public void workflowCreation(IGenlabWorkflow workflow);
	
	public void workflowChanged(IGenlabWorkflow workflow);

	public void workflowOpened(IGenlabWorkflow workflow);
	
	public void workflowSaving(IGenlabWorkflow workflow);

	public void workflowSaved(IGenlabWorkflow workflow);

	
}
