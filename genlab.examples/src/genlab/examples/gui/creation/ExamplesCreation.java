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
				hostDirectory.getAbsolutePath()+File.separator+getPathForExample(example)
				);
		
		File dirData = new File(hostDirectory.getAbsolutePath()+File.separator+getPathForExampleResources(example));
		dirData.mkdirs();
		
		// let the example create its data files
		example.createFiles(dirData);
		
		// let the example create the algos in the workflow
		example.fillInstance(workflow);
		
		// emit the information this workflow was automatically created (graphical counterparts might be automatically created)
		WorkflowHooks.getWorkflowHooks().notifyWorkflowAutomaticallyDone(workflow);
		
		GenlabPersistence.getPersistence().saveWorkflow(workflow);
		
		return workflow;
	}

}
