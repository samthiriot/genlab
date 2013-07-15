package genlab.core.model.exec;

import genlab.core.exec.Execution;
import genlab.core.model.instance.GenlabWorkflowInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.WorkflowCheckResult;
import genlab.core.usermachineinteraction.GLLogger;

public class Tests {

	public Tests() {
		// TODO Auto-generated constructor stub
	}

	public static void run(IGenlabWorkflowInstance workflow ) {

		// TODO remove
		
		GLLogger.debugTech("run called", Tests.class);
		
		GLLogger.debugTech("workflow: "+workflow, Tests.class);
		if (workflow == null) {
			GLLogger.warnTech("null...", Tests.class);
			return;
		}
		
		// check workflow
		GLLogger.infoUser("checking the workflow "+workflow+"...", Tests.class);

		WorkflowCheckResult checkInfo = workflow.checkForRun();
		if (checkInfo.isReady()) {
			GLLogger.infoUser("ready :-)", Tests.class);
		} else {
			GLLogger.errorUser("problem..;", Tests.class);
			return;
		}
	
		Execution exec = new Execution();
		exec.setExecutionForced(true);

		IAlgoExecution execution = workflow.execute(exec);

		GLLogger.infoUser("start run !", Tests.class);

		execution.run();

		GLLogger.infoUser("done.", Tests.class);
	}
}
