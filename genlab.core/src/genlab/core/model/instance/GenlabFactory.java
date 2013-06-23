package genlab.core.model.instance;

import genlab.core.persistence.GenlabPersistence;
import genlab.core.projects.GenlabProject;
import genlab.core.projects.IGenlabProject;

public class GenlabFactory {

	
	public static IGenlabWorkflowInstance createWorkflow(
			IGenlabProject project, 
			String name, 
			String desc, 
			String relativePath) {
		
		IGenlabWorkflowInstance workflow = new GenlabWorkflowInstance(project, name, desc, relativePath);
		 
		if (project != null)
			project.addWorkflow(workflow);
		
		GenlabPersistence.getPersistence().saveProject(project, false);
		GenlabPersistence.getPersistence().saveWorkflow(workflow);

		WorkflowHooks.getWorkflowHooks().notifyWorkflowCreation(workflow);
		
		return workflow;
		
	}
	
	public static IGenlabProject createProject(String absoluteDirectory) {
		
		IGenlabProject project = new GenlabProject(absoluteDirectory);
				
		GenlabPersistence.getPersistence().saveProject(project);
		
		return project;
		
	}
	
	
	private GenlabFactory() {}

}
