package genlab.gui.views;

import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.IGenlabWorkflow;

public class WorkflowRoot {

	public final IGenlabWorkflowInstance workflow;
	
	public WorkflowRoot(IGenlabWorkflowInstance workflow) {
		this.workflow = workflow;
	}
	
}
