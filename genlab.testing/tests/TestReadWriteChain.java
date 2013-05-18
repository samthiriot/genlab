import static org.junit.Assert.*;
import genlab.basics.workflow.WorkflowFactory;
import genlab.core.algos.IAlgoInstance;
import genlab.core.algos.IGenlabWorkflow;
import genlab.graphstream.algos.generators.WattsStrogatzAlgo;
import genlab.graphstream.algos.writers.GraphStreamDGSWriter;

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

		IGenlabWorkflow workflow =  WorkflowFactory.createWorkflow("test", "for unit testing", null);
		
		// add a generator
		
		WattsStrogatzAlgo wsAlgo = new WattsStrogatzAlgo();
		IAlgoInstance wsInstance = wsAlgo.createInstance();
		
		workflow.addAlgoInstance(wsInstance);
	
		// add an exporter
		GraphStreamDGSWriter writer = new GraphStreamDGSWriter();
		IAlgoInstance writerInstance = writer.createInstance();
		
		workflow.addAlgoInstance(writerInstance);

		
		// link them
		workflow.connect(
				wsInstance, wsAlgo.OUTPUT_GRAPH,
				writerInstance, writer.PARAM_GRAPH
				);
		
	}

}
