package genlab.examples.gui.creation;

import genlab.core.model.instance.GenlabFactory;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.WorkflowHooks;
import genlab.core.persistence.GenlabPersistence;
import genlab.core.projects.IGenlabProject;
import genlab.gui.examples.contributors.IGenlabExample;

import java.io.File;


public class ExamplesCreation {
	
	private ExamplesCreation() {
		
	}
	
	
	private static String getPathForExample(IGenlabExample example) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("/workflows/").append(example.getFileName()).append(".glw");
		
		return sb.toString();
	}
	

	public static String getPathForExampleResources(IGenlabExample example) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("data/").append(example.getFileName()).append(File.separator);
		
		return sb.toString();
	}

	/**
	 * Creates a workflow for the given example inside the given project
	 * @param example
	 * @param glProject
	 * @return
	 */
	public static IGenlabWorkflowInstance createWorkflow(IGenlabExample example, IGenlabProject glProject) {

		
		IGenlabWorkflowInstance workflow = GenlabFactory.createWorkflow(
				glProject,
				example.getName(), 
				example.getDescription(), 
				getPathForExample(example)
				);
		
		File dirData = new File(glProject.getBaseDirectory()+File.separator+getPathForExampleResources(example));
		dirData.mkdirs();
		
		example.createFiles(dirData);
		
		example.fillInstance(workflow);
		
		WorkflowHooks.getWorkflowHooks().notifyWorkflowAutomaticallyDone(workflow);
		
		GenlabPersistence.getPersistence().saveWorkflow(workflow);
		
		return workflow;
	}

}
