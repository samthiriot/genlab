package genlab.graphstream.algos.generators;

import static org.junit.Assert.*;

import java.util.HashMap;

import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationResult;
import genlab.core.model.instance.GenlabFactory;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IGenlabWorkflow;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

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
					put(WattsStrogatzAlgo.INPUT_N, 100);
					put(WattsStrogatzAlgo.INPUT_K, 2);
					put(WattsStrogatzAlgo.INPUT_P, 0.1);
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
