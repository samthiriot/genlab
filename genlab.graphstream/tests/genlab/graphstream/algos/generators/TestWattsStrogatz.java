package genlab.graphstream.algos.generators;

import static org.junit.Assert.*;

import java.util.HashMap;

import genlab.basics.javaTypes.graphs.IGenlabGraph;
import genlab.basics.workflow.GenlabFactory;
import genlab.core.algos.IAlgoExecution;
import genlab.core.algos.IAlgoInstance;
import genlab.core.algos.IComputationResult;
import genlab.core.algos.IGenlabWorkflow;
import genlab.core.algos.IInputOutput;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO test randomness as well !
 */
public class TestWattsStrogatz {

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
	public void test1() {
		
		WattsStrogatzAlgo algo = new WattsStrogatzAlgo();
		assertNotNull(algo.getInputs());
		assertNotNull(algo.getOuputs());
		assertNotNull(algo.getName());
		assertNotNull(algo.getDescription());
		
		IAlgoInstance instance = algo.createInstance();
		
		IAlgoExecution execution = instance.execute(
				new HashMap<IInputOutput, Object>() {{
					put(WattsStrogatzAlgo.PARAM_N, 100);
					put(WattsStrogatzAlgo.PARAM_K, 2);
					put(WattsStrogatzAlgo.PARAM_P, 0.1);
				}}
		);
		
		// run
		execution.run();
		// (and wait for end of run)
		while (execution.getProgress().getTimestampEnd() == null) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// use result
		IComputationResult res = execution.getResult();
		for (IInputOutput<?> io : algo.getOuputs()) {
			
			assertTrue(
					"expected results to contain one more io "+io, 
					res.getResults().containsKey(io)
					);
			assertNotNull(
					"returned output is null for io "+io, 
					res.getResults().get(io)
					);
		}
		
		// check the resulting graph
		IGenlabGraph graph = (IGenlabGraph) res.getResults().get(WattsStrogatzAlgo.OUTPUT_GRAPH);
		assertEquals(100, graph.getVerticesCount());
		assertTrue(graph.getEdgesCount()>100);
		
		
	}
	


}
