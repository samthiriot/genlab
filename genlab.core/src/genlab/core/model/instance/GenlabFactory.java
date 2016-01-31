package genlab.core.model.instance;

import genlab.core.persistence.GenlabPersistence;

public class GenlabFactory {

	
	public static IGenlabWorkflowInstance createWorkflow(
			String name, 
			String desc, 
			String relativePath) {
		
		IGenlabWorkflowInstance workflow = new GenlabWorkflowInstance(name, desc, relativePath);
		 
		GenlabPersistence.getPersistence().saveWorkflow(workflow);

		WorkflowHooks.getWorkflowHooks().notifyWorkflowCreation(workflow);
		
		return workflow;
		
	}


	
	public static IGenlabWorkflowInstance createWorkflow(
			String id,
			String name, 
			String desc, 
			String relativePath) {
		
		IGenlabWorkflowInstance workflow = new GenlabWorkflowInstance(id, name, desc, relativePath);
		 
		GenlabPersistence.getPersistence().saveWorkflow(workflow);

		WorkflowHooks.getWorkflowHooks().notifyWorkflowCreation(workflow);
		
		return workflow;
		
	}

	
	private GenlabFactory() {}

}
