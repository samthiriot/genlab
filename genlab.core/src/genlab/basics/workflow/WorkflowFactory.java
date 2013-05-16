package genlab.basics.workflow;

import genlab.core.algos.IGenlabWorkflow;
import genlab.core.projects.IGenlabProject;

public class WorkflowFactory {

	
	public static IGenlabWorkflow createWorkflow(IGenlabProject project, String name, String desc, String relativePath) {
		
		IGenlabWorkflow workflow = new GenlabWorkflow(project, name, desc, relativePath);
		
		WorkflowHooks.getWorkflowHooks().notifyWorkflowCreation(workflow);
		
		return workflow;
		
	}
	
	
	private WorkflowFactory() {}

}
