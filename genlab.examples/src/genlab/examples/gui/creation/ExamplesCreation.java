package genlab.examples.gui.creation;

import java.io.File;

import genlab.core.model.instance.GenlabFactory;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.WorkflowHooks;
import genlab.core.persistence.GenlabPersistence;
import genlab.gui.examples.contributors.IGenlabExample;


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
	public static IGenlabWorkflowInstance createWorkflow(IGenlabExample example, File hostDirectory) {

		
		IGenlabWorkflowInstance workflow = GenlabFactory.createWorkflow(
				example.getName(), 
				example.getDescription(), 
				getPathForExample(example)
				);
		
		File dirData = new File(hostDirectory.getAbsolutePath()+File.separator+getPathForExampleResources(example));
		dirData.mkdirs();
		
		example.createFiles(dirData);
		
		example.fillInstance(workflow);
		
		WorkflowHooks.getWorkflowHooks().notifyWorkflowAutomaticallyDone(workflow);
		
		GenlabPersistence.getPersistence().saveWorkflow(workflow);
		
		return workflow;
	}

}
