package genlab.gui.genlab2eclipse;

import genlab.basics.workflow.IWorkflowListener;
import genlab.core.algos.IGenlabWorkflow;

/**
 * Listens for workflow activity, and updates the corresponding 
 * GUI eclipse files correspondingly
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

}
