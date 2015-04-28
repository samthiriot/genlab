package genlab.core.exec.server;

import static org.junit.Assert.fail;
import genlab.core.commons.UniqueTimestamp;
import genlab.core.exec.Execution;
import genlab.core.exec.IRunner;
import genlab.core.exec.LocalComputationNode;
import genlab.core.exec.TasksManager;
import genlab.core.exec.client.RunnerWithDistant;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.ExecutionHooks;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.GenlabFactory;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.basics.algos.ConstantValueDouble;
import genlab.core.model.meta.basics.algos.ConstantValueInteger;
import genlab.core.model.meta.basics.algos.StandardOutputAlgo;
import genlab.core.projects.IGenlabProject;
import genlab.core.usermachineinteraction.MessageLevel;
import genlab.graphstream.algos.generators.WattsStrogatzAlgo;
import genlab.testing.commons.GenlabTestingUtils;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestRMI {


    @BeforeClass
    public static void oneTimeSetUp() {
    	
        // one-time initialization code
    	System.out.println("define policy");
    	
    	// define server policy
    	System.setProperty("java.security.policy","file:/local00/home/B12772/workspaceGenlab/genlab/genlab.core/GenlabServer.policy");

    }
	
    public void tearDown() {
    	// force exit (RMI registry !)
    	System.exit(0);
    }
    
	@Test
	public void testStartServer() {
		
		// start the server...
		GenlabComputationServer.start();
		
		// create a runner connected as a client
		IRunner runner = getRunnerDistance();
		
		// run a workflow
		IGenlabWorkflowInstance workflowWS = getWorkflowTestOneWS();
		execWorkflow(workflowWS, false, runner);
		
	}
	
	private IGenlabWorkflowInstance createEmptyWorklow() {
		
		IGenlabProject project = null;
		IGenlabWorkflowInstance workflow = null;
			
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

		return workflow;
		
	}
	
	private IGenlabWorkflowInstance getWorkflowTestOneWS() {
		
		IGenlabWorkflowInstance workflow = createEmptyWorklow();
	
		int N = 1000;
		int K = 2;
		double p = 0.05;
		
		// ref algos
		WattsStrogatzAlgo ws = new WattsStrogatzAlgo();
		ConstantValueInteger constantInt = new ConstantValueInteger();
		ConstantValueDouble constantDouble = new ConstantValueDouble();
		StandardOutputAlgo outputAlgo = new StandardOutputAlgo();
		
		// create instances inside the workflow
		{	
			IAlgoInstance wsInstance = ws.createInstance(workflow);
			workflow.addAlgoInstance(wsInstance);
			
			{
				IAlgoInstance constantN = constantInt.createInstance(workflow);
				workflow.addAlgoInstance(constantN);
				constantN.setValueForParameter(constantInt.getConstantParameter(), N);
				workflow.connect(
						 constantN.getOutputInstanceForOutput(ConstantValueInteger.OUTPUT),
						 wsInstance.getInputInstanceForInput(WattsStrogatzAlgo.INPUT_N)
				);
			}
			
			{
				IAlgoInstance constantK = constantInt.createInstance(workflow);
				workflow.addAlgoInstance(constantK);
				constantK.setValueForParameter(constantInt.getConstantParameter(), K);
				workflow.connect(
						constantK.getOutputInstanceForOutput(ConstantValueInteger.OUTPUT),
						 wsInstance.getInputInstanceForInput(WattsStrogatzAlgo.INPUT_K)
				);
			}
			
			{
				IAlgoInstance constantP = constantDouble.createInstance(workflow);
				workflow.addAlgoInstance(constantP);
				constantP.setValueForParameter(constantDouble.getConstantParameter(), p);
				workflow.connect(
						constantP.getOutputInstanceForOutput(ConstantValueDouble.OUTPUT),
						 wsInstance.getInputInstanceForInput(WattsStrogatzAlgo.INPUT_P)
				);
			}
			
			IAlgoInstance stdOutInstance = outputAlgo.createInstance(workflow);
			workflow.addAlgoInstance(stdOutInstance);
			workflow.connect(
					wsInstance.getOutputInstanceForOutput(WattsStrogatzAlgo.OUTPUT_GRAPH), 
					stdOutInstance.getInputInstanceForInput(StandardOutputAlgo.INPUT)
					);
			
		}
		
		return workflow;
	}

	private IRunner getRunnerDistance() {
		
		RunnerWithDistant runner = new RunnerWithDistant(0);
				
		runner.addRunnerDistant("gl_worker_distant_1", "localhost", 20000);
				
		return runner;

	}
	private void execWorkflow(IGenlabWorkflowInstance workflow, boolean execExpectedFailure, IRunner r) {
		
		// execute it

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
		GenlabTestingUtils.waitUntilAllTasksExecuted(r);
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

	
}
