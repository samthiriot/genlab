package genlab.testing.commons;

import genlab.core.commons.UniqueTimestamp;
import genlab.core.exec.Execution;
import genlab.core.exec.IRunner;
import genlab.core.exec.TasksManager;
import genlab.core.exec.client.ComputationNodes;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.ExecutionHooks;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.GenlabFactory;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.WorkflowCheckResult;
import genlab.core.projects.IGenlabProject;
import genlab.core.usermachineinteraction.MessageLevel;
import static org.junit.Assert.*;

/**
 * BAsic test for a workflow: user just has to fill the workflow, and this basic class
 * provides tests for init, saving, exec, etc.
 * 
 * @author Samuel Thiriot
 *
 */
public abstract class BasicTestWorkflow {

	protected IGenlabProject project = null;
	protected IGenlabWorkflowInstance workflow = null;
	
	public BasicTestWorkflow() {
	
	}

	/**
	 * Here the workflow should be filled with everything. WIll be encapsulated with error detection and 
	 * corresponding JUnit unit test failure.
	 * @param workflow
	 */
	protected abstract void populateWorkflow(IGenlabWorkflowInstance workflow);
	
	/**
	 * Tries to create the workflow, and catches errors and reports them as JUnit errors.
	 */
	public void createWorkflow(boolean expectedFail) {
		
		// create host project and workflow
		System.err.println("### tmp project creation...");
		project = GenlabTestingUtils.createEmptyProject();
		Object uniqueId = new UniqueTimestamp();
		workflow = GenlabFactory.createWorkflow(
									project, 
									"test"+uniqueId.toString(), 
									"test"+uniqueId.toString(), 
									"my workflow test "+uniqueId.toString()
									);
		System.err.println("### tmp workflow created inside: "+workflow.getAbsolutePath());

		// populate
		System.err.println("### workflow creation...");
		try {
			populateWorkflow(workflow);
			if (expectedFail) {
				fail("an error was expected, but no exception raised");
			}
			System.err.println("### workflow created.");

		} catch (RuntimeException e) {
			e.printStackTrace();
			if (!expectedFail) {
				fail("error during workflow creation "+e.getMessage());
			} else {
				System.out.println("### workflow creation failed as expected : "+e.getMessage());
			}
		}
		

	}
	
	/**
	 * Tries to save the workflow, and catches errors and reports them as Junit errors.
	 */
	public void saveWorkflow() {
		
	}

	public void checkWorkflow(boolean checkExpectedFailure) {
		
		// check for execution
		WorkflowCheckResult checkInfo = workflow.checkForRun();
		if (!checkInfo.isReady()) {
			checkInfo.messages.dumpToStream(System.err);
			if (checkExpectedFailure) {
				System.err.println("### workflow check failed as expected");	
			} else {
				fail("workflow checking failed");
			}
		} else {
			if (checkExpectedFailure) {
				fail("workflow checking was supposed to fail but worked");
			} else {
				System.err.println("### workflow can be ran");
				
			}
		}
		
		
		
	}

	public void execWorkflow(boolean execExpectedFailure) {
		
		
		// execute it
		IRunner r = ComputationNodes.getSingleton().getRunner();

		Execution exec = new Execution(r);
		exec.setExecutionForced(true);
		exec.getListOfMessages().setFilterIgnoreBelow(MessageLevel.INFO);
		
		ExecutionHooks.singleton.notifyParentTaskAdded(exec);	// TODO something clean...
		IAlgoExecution execution = workflow.execute(exec);
		
		TasksManager.singleton.notifyListenersOfTaskAdded(execution); // TODO something clean...

		r.addTask(execution);

		System.err.println("### workflow started...");

		// TODO
		
		// wait for the end of computation
		GenlabTestingUtils.waitUntilAllTasksExecuted();
		exec.getListOfMessages().dumpToStream(System.err);
		// check exec ok
		
		if (execution.getProgress().getComputationState() != ComputationState.FINISHED_OK) {
			
			if (execExpectedFailure) { 
				System.err.println("### workflow exec failed as expected.");
			} else {
				fail("workflow exec failed");
			}
		} else {
			if (execExpectedFailure) {
				fail("workflow exec was supposed to fail but did not");
			} else {
				System.err.println("### workflow was executed.");
				
			}
		}
		
	}
	
	public void execAll(boolean initExpectedFailure, boolean checkExpectedFailure, boolean execExpectedFailure) {
		
		createWorkflow(initExpectedFailure);
		
		// saving
		saveWorkflow();
		
		// exec
		checkWorkflow(checkExpectedFailure);
		execWorkflow(execExpectedFailure);
		
	}
	
}
