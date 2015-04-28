package genlab.testing.commons;

import static org.junit.Assert.fail;
import genlab.core.exec.IRunner;
import genlab.core.exec.LocalComputationNode;
import genlab.core.model.instance.GenlabFactory;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.projects.GenlabProject;
import genlab.core.projects.IGenlabProject;

import java.io.IOException;

import org.junit.rules.TemporaryFolder;

public class GenlabTestingUtils {

	private GenlabTestingUtils() {
		
	}
	
	public static IGenlabProject createEmptyProject() {
		
		TemporaryFolder tmpDir = new TemporaryFolder();
		try {
			tmpDir.create();
		} catch (IOException e) {
			e.printStackTrace();
			fail("unable to create tmp directory");
		}
				
		IGenlabProject genlabProject = GenlabFactory.createProject(tmpDir.getRoot().getAbsolutePath());
		
		return genlabProject;
		
	}
	
	public static IGenlabWorkflowInstance createEmptyWorkflow(IGenlabProject project, String name, String desc, String relativePath) {

		IGenlabWorkflowInstance workflow = GenlabFactory.createWorkflow(project, name, desc, relativePath);
		
		return workflow;
	}
	
	public static void waitUntilAllTasksExecuted(IRunner r) {
		
		//IRunner r = LocalComputationNode.getSingleton().getRunner();
		while (r.getCountNotFinished() > 0) {
			try {
				Thread.sleep(2000);
				System.err.println("### "+r.getHumanReadableState());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
