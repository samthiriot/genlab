package genlab.testing.commons;

import genlab.core.exec.IRunner;
import genlab.core.model.instance.GenlabFactory;
import genlab.core.model.instance.IGenlabWorkflowInstance;

public class GenlabTestingUtils {

	private GenlabTestingUtils() {
		
	}
	
	
	public static IGenlabWorkflowInstance createEmptyWorkflow(String name, String desc, String relativePath) {

		IGenlabWorkflowInstance workflow = GenlabFactory.createWorkflow(name, desc, relativePath);
		
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
