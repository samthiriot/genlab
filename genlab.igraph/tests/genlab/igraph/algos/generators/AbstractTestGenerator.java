package genlab.igraph.algos.generators;

import genlab.core.exec.Execution;
import genlab.core.exec.IExecution;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.Rigraph.RIGraphLibImplementation;
import genlab.igraph.commons.IGraphLibImplementation;
import genlab.igraph.natjna.IGraphLibImplementationNative;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests a random network generator with all the existing implementations;
 * checks the impact of seeds, directivity of networks, etc.
 * 
 * @author Samuel Thiriot
 *
 */
@RunWith(Parameterized.class)
public abstract class AbstractTestGenerator {

	// parameterized
	private IGraphLibImplementation lib;
	private GraphDirectionality directionalityExpected;
	private Integer verticesCountExpected;
	private Long edgesCountExpected;
	
		
    public AbstractTestGenerator(
    		IGraphLibImplementation lib, 
    		GraphDirectionality directionalityExpected,
    		Integer verticesCountExpected,
    		Long edgesCountExpected) {
    	this.lib = lib;
    	this.directionalityExpected = directionalityExpected;
    	this.verticesCountExpected = verticesCountExpected;
    	this.edgesCountExpected = edgesCountExpected;
    }

    @Parameters
    public static Collection<Object[]> generateData() {
    	
	    return Arrays.asList(new Object[][] {
	    		{ 
	    			new RIGraphLibImplementation() 
	    		}, 
	    		{ 
	    			new IGraphLibImplementationNative() 
	    		}
	    	} 
	    );
    }
    
    @Before
    public void setup() {
    	

    }

    @After
    public void teardown() {
    	
    }
    

    private IExecution getExecution() {
    	return new Execution();
    }
    
    private long getSeed() {
    	Long seed = System.currentTimeMillis();
		return (long)seed.intValue();
    }
    
    protected abstract IGenlabGraph generateGraph(Long seed, IGraphLibImplementation lib, IExecution exec);


    @Test(timeout = 10000)
    public void testGeneratedGraphProperties() {

    	long seed1 = getSeed();
		IExecution exec = getExecution();
		
		// create a graph
		IGenlabGraph g1 = generateGraph(seed1, lib, exec);
		
		// these graph should be similar
		if (directionalityExpected != null)
			Assert.assertTrue(
					"wrong directionnality: expected "+directionalityExpected+" but found "+g1.getDirectionality(),
					directionalityExpected == g1.getDirectionality()
					);
		if (verticesCountExpected != null)
			Assert.assertEquals(
					"wrong count of vertices: expected "+verticesCountExpected+" but found "+g1.getVerticesCount(),
					verticesCountExpected.longValue(),
					g1.getVerticesCount()
					);
		if (edgesCountExpected != null)
			Assert.assertEquals(
					"wrong count of edges: expected "+edgesCountExpected+" but found "+g1.getEdgesCount(),
					edgesCountExpected.longValue(),
					g1.getEdgesCount()
					);
		
	}
    
    @Test(timeout = 10000)
    public void testIsomorphismForSameGraph() {

    	long seed1 = getSeed();
		IExecution exec = getExecution();
		
		// create a graph
		IGenlabGraph g1 = generateGraph(seed1, lib, exec);
		
		// these graph should be similar
		Assert.assertTrue(
				"two similar graphs should be isomorphic",
				lib.computeIsomorphicm(g1, g1, exec)
				);
		
	}
    

    @Test(timeout = 10000)
    public void testIsomorphismForDifferentGraph() {

    	long seed1 = getSeed();
    	long seed2 = getSeed();
		while (seed1 == seed2) {
			seed2 = getSeed();
		}
		IExecution exec = getExecution();
		
		// create a graph
		IGenlabGraph g1 = generateGraph(seed1, lib, exec);
		IGenlabGraph g2 = generateGraph(seed2, lib, exec);

		// these graph should be similar
		Assert.assertFalse(
				"two different graphs should not be isomorphic",
				lib.computeIsomorphicm(g1, g2, exec)
				);
		
	}

    
    /**
     * Ensures that with 2 different seeds, the graphs are different, 
     * while with the same they are isomorphic
     */
    @Test(timeout = 10000)
    public void testImpactSeed() {

    	long seed1 = getSeed();
    	long seed2 = getSeed();
		while (seed1 == seed2) {
			seed2 = getSeed();
		}
		IExecution exec = getExecution();
		
		// are the properties ok ? 
		IGenlabGraph g1 = generateGraph(seed1, lib, exec);
		
		// let's generate it with another seed
		IGenlabGraph g2 = generateGraph(seed2, lib, exec);
		
		// these graph should be different
		Assert.assertFalse(
				"two graphs generated with different seeds should be different", 
				lib.computeIsomorphicm(g1, g2, exec)
				);
		
		// let's generate another one with the first seed
		IGenlabGraph g3 = generateGraph(seed1, lib, exec);
		
		// these graph should be similar
		Assert.assertTrue(
				"two graphs generated with the same seed should be similar",
				lib.computeIsomorphicm(g1, g3, exec)
				);
		
	}
    

    /**
     * Ensures that with no seed provided, the generation works
     * and creates different graphs
     */
    @Test(timeout = 10000)
    public void testImpactNoSeed() {

		IExecution exec = getExecution();
		
		// are the properties ok ? 
		IGenlabGraph g1 = generateGraph(null, lib, exec);
		
		// let's generate it with another seed
		IGenlabGraph g2 = generateGraph(null, lib, exec);
		
		// these graph should be different
		Assert.assertFalse(
				"two graphs generated with no defined seed should be different", 
				lib.computeIsomorphicm(g1, g2, exec)
				);
		
		
	}
    

	class TestRandomnessRunnable extends Thread {
	
		private final long seed;
		private final int count;
		private IGenlabGraph [] generated = null;
		public boolean finished = false;
		private final IExecution exec;
		
		public TestRandomnessRunnable(long seed, int count) {
			this.seed = seed;
			this.count = count;
			this.exec = new Execution();
		}
		
		@Override
		public void run() {
			

			generated = new IGenlabGraph [count];
			
			for (int i=0; i<count; i++) {
				generated[i] = generateGraph(seed+i, lib, exec);
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			System.out.println("generated here "+this+": "+Arrays.toString(generated));
			
			finished = true;
		}
		
	}
	
	
	/**
	 * Ensures parallel access are able to define different seeds.
	 * We create N threads. These threads all compute a serie of random graphs;
	 * they all take the same seed at the beginning and evolve it in the same way.
	 * If there is a difference in the graphs generated between different threads,
	 * then the RNG is shared between the different threads. 
	 * It is not dramatic, but it means replicability is not guaranteed.
	 */
	@Test(timeout = 60000)
	public void testNonContagionBetweenThreads() {
		
		final int paralellThreads = 3;
		final long theSeed = getSeed();
		final int lenghtSeriesToTest=10;
		
		TestRandomnessRunnable[] pThreads = new TestRandomnessRunnable[paralellThreads];
		
		// init the threads
		for (int i=0; i<paralellThreads; i++) {
			pThreads[i] = new TestRandomnessRunnable(theSeed, lenghtSeriesToTest);
		}
		
		// start the threads
		for (int i=0; i<paralellThreads; i++) {
			pThreads[i].start();
		}
		
		// now wait for all of them
		System.out.println("waiting for threads to finish...");
		while (true) {
			boolean all_finished = true;
			for (int i=0; i<paralellThreads; i++) {
				if (!pThreads[i].finished) {
					all_finished = false;
					break;
				}
			}
			if (all_finished) 
				break;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// analyze results: are the series the same ? 
		for (int i=0; i<paralellThreads-1; i++) {
			
			for (int j=0; j<lenghtSeriesToTest; j++) {
				Assert.assertTrue(
						"with lib "+lib.getClass().getCanonicalName()+", random graph diverged despite of the same evolution of seeds; so the random number generator is shared between the different threads",
						lib.computeIsomorphicm(
								pThreads[i].generated[j], 
								pThreads[i+1].generated[j], 
								getExecution()
								)
						);
						
			}
			
		}
		
		// ok
	}
	
    
}
