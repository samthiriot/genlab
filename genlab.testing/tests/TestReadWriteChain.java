import static org.junit.Assert.*;
import genlab.core.exec.Execution;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.GenlabFactory;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.WorkflowCheckResult;
import genlab.core.model.meta.basics.algos.ConstantValueDouble;
import genlab.core.model.meta.basics.algos.ConstantValueInteger;
import genlab.core.persistence.GenlabPersistence;
import genlab.core.projects.IGenlabProject;
import genlab.gephi.algos.measure.GephiAveragePathLengthAlgo;
import genlab.graphstream.algos.generators.WattsStrogatzAlgo;
import genlab.graphstream.algos.measure.GraphStreamAPSP;
import genlab.graphstream.algos.measure.GraphStreamConnectedComponents;
import genlab.graphstream.algos.writers.GraphStreamDGSWriter;
import genlab.graphstream.algos.writers.GraphStreamGMLWriter;
import genlab.igraph.algos.measure.IGraphAveragePathLengthAlgo;
import genlab.neo4j.algos.writers.Neo4jGraphWriter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestReadWriteChain {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		IGenlabProject project = GenlabFactory.createProject("/tmp/project");

		IGenlabWorkflowInstance workflow =  GenlabFactory.createWorkflow(
				project,
				"test", 
				"for unit testing", 
				"workflow"
				);
		
		// algos available (meta model)
		WattsStrogatzAlgo wsAlgo = new WattsStrogatzAlgo();
		GraphStreamDGSWriter writerDGs = new GraphStreamDGSWriter();
		GraphStreamGMLWriter writerGML = new GraphStreamGMLWriter();
		ConstantValueInteger constInteger = new ConstantValueInteger();
		ConstantValueDouble constDouble = new ConstantValueDouble();
		GraphStreamConnectedComponents detectConnectedComponents = new GraphStreamConnectedComponents();
		GraphStreamAPSP apsp = new GraphStreamAPSP();
		GephiAveragePathLengthAlgo gephiLength = new GephiAveragePathLengthAlgo();
		IGraphAveragePathLengthAlgo igraphLength = new IGraphAveragePathLengthAlgo();
		Neo4jGraphWriter neo4j = new Neo4jGraphWriter();
		// add a generator
		IAlgoInstance wsAlgoInstance = wsAlgo.createInstance(workflow); 
		
		// add an exporter
		IAlgoInstance writerInstance = writerDGs.createInstance(workflow); 
		IAlgoInstance writerInstance2 = writerGML.createInstance(workflow); 
		
		// add a detector
		IAlgoInstance analysisComponent = detectConnectedComponents.createInstance(workflow);

		
		
		
		// link them 
		// ... constant parameters for the ws algo
		{
			IAlgoInstance wsN = constInteger.createInstance(workflow);
			wsN.setValueForParameter("value", 500);
			IConnection c = workflow.connect(
					wsN.getOutputInstanceForOutput(constInteger.OUTPUT),
					wsAlgoInstance.getInputInstanceForInput(wsAlgo.INPUT_N)
					);
		}
		{
			IAlgoInstance wsK = constInteger.createInstance(workflow);
			wsK.setValueForParameter("value", 2);
			IConnection c = workflow.connect(
					wsK.getOutputInstanceForOutput(constInteger.OUTPUT),
					wsAlgoInstance.getInputInstanceForInput(wsAlgo.INPUT_K)
					);
		}
		{
			IAlgoInstance wsP = constDouble.createInstance(workflow);
			wsP.setValueForParameter("value", 0.2);
			IConnection c = workflow.connect(
					wsP.getOutputInstanceForOutput(constDouble.OUTPUT),
					wsAlgoInstance.getInputInstanceForInput(wsAlgo.INPUT_P)
					);
		}
		
		
		// connect components detection
		IConnection c3 = workflow.connect(
				wsAlgoInstance.getOutputInstanceForOutput(wsAlgo.OUTPUT_GRAPH),
				analysisComponent.getInputInstanceForInput(detectConnectedComponents.INPUT_GRAPH)
				);
		
		// ... link to the writing 
		IConnection c = workflow.connect(
				analysisComponent.getOutputInstanceForOutput(detectConnectedComponents.OUTPUT_GRAPH),
				writerInstance.getInputInstanceForInput(writerDGs.INPUT_GRAPH)
				);
		IConnection c2 = workflow.connect(
				analysisComponent.getOutputInstanceForOutput(detectConnectedComponents.OUTPUT_GRAPH),
				writerInstance2.getInputInstanceForInput(writerGML.INPUT_GRAPH)
				);
	
		
		workflow.connect(
				analysisComponent.getOutputInstanceForOutput(detectConnectedComponents.OUTPUT_GRAPH), 
				neo4j.createInstance(workflow).getInputInstanceForInput(neo4j.PARAM_GRAPH)
				);
		
		// length
		workflow.connect(
				wsAlgoInstance.getOutputInstanceForOutput(wsAlgo.OUTPUT_GRAPH),
				apsp.createInstance(workflow).getInputInstanceForInput(apsp.INPUT_GRAPH)
				);
		
		workflow.connect(
				wsAlgoInstance.getOutputInstanceForOutput(wsAlgo.OUTPUT_GRAPH),
				igraphLength.createInstance(workflow).getInputInstanceForInput(igraphLength.INPUT_GRAPH)
				);
		
		workflow.connect(
				wsAlgoInstance.getOutputInstanceForOutput(wsAlgo.OUTPUT_GRAPH),
				gephiLength.createInstance(workflow).getInputInstanceForInput(gephiLength.INPUT_GRAPH)
				);
		
		// check everything
		WorkflowCheckResult checkInfo = workflow.checkForRun();
		assertTrue("the workflow is not ready to run", checkInfo.isReady());
		
		// save it !
		GenlabPersistence.getPersistence().saveProject(project);
		
		if (2==1*2)
			return;
			
		// init execution context
		Execution execCtxt = new Execution();
		execCtxt.setExecutionForced(true);
		
		
		// and now shift to execution
		IAlgoExecution exec =  workflow.execute(execCtxt);
		
		exec.run();
		
		assertEquals(ComputationState.FINISHED_OK, exec.getProgress().getComputationState());
		
	}

}
