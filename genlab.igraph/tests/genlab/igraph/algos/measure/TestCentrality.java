package genlab.igraph.algos.measure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.natjna.IGraphLibrary;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.sun.jna.Native;

@RunWith(Parameterized.class)
public class TestCentrality {

	// parameterized
	private boolean runInProtectedMode;
	private boolean directed;
	private boolean loops;
	private int size;

	
	private IGraphLibrary lib;
	private IGraphGraph graph;
	
    public TestCentrality(boolean runInProtectedMode, boolean directedNetwork, boolean loops, int size) {
    	
        this.runInProtectedMode = runInProtectedMode;
        this.directed = directedNetwork;
        this.loops = loops;
        this.size = size;
    }
    
    @Parameters
    public static Collection<Object[]> generateData() {
    	
	    return Arrays.asList(new Object[][] {
	    		{ 
	    			Boolean.TRUE, Boolean.TRUE,	Boolean.TRUE, 100 
	    		}, 
	    		{ 
	    			Boolean.TRUE, Boolean.TRUE,	Boolean.FALSE, 100 
	    		}, 
	    		{
	    			Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, 100
	    		},
	    		{
	    			Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, 100
	    		},
	    		{ 
	    			Boolean.TRUE, Boolean.TRUE,	Boolean.TRUE, 500 
	    		}, 
	    		{ 
	    			Boolean.TRUE, Boolean.TRUE,	Boolean.FALSE, 500 
	    		}, 
	    		{
	    			Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, 500
	    		},
	    		{
	    			Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, 500
	    		}

	    		
	    		
	    		} 
	    );
    }

    protected IGraphGraph generateNetworkForTest() {
    
    	IGraphGraph res = lib.generateWattsStrogatz(size, 1, 0.1, 4, directed, loops);
    	
    	assertEquals("wrong network directivity", directed, res.directed);
    	
    	return res;
    }
	
    @Before
    public void setup() {
    	
    	System.err.println("JNA protected: "+runInProtectedMode);
    	Native.setProtected(runInProtectedMode);
    	
    	lib = new IGraphLibrary();
    	graph = generateNetworkForTest();
    	
    	
    }

    @After
    public void teardown() {
    	lib.clearGraphMemory(graph);
    }
    


    @Test(timeout = 1000)
	public void testEdgeBetweenessEstimate() {

		double [] edgeBet;
		
		edgeBet = lib.computeEdgeBetweenessEstimate(
									graph, 
									graph.directed, 
									2.0
									);

		assertEquals("the size of results is not OK", lib.getEdgeCount(graph), edgeBet.length);
		
		
		
	}
    

    @Test(timeout = 1000)
	public void testEdgeBetweeness() {

		double [] edgeBet;
		
		edgeBet = lib.computeEdgeBetweeness(
									graph, 
									graph.directed
									);

		assertEquals("the size of results is not OK", lib.getEdgeCount(graph), edgeBet.length);
				
		
	}
	
    
	
	@Test(timeout = 1000)
	public void testNodeBetweenessEstimate() {

		double [] nodeBet;
		
		nodeBet = lib.computeNodeBetweenessEstimate(
									graph, 
									graph.directed, 
									2.0
									);
		
		assertEquals("the size of results is not OK", lib.getVertexCount(graph), nodeBet.length);
		
	}
	
	
	@Test(timeout = 1000)
	public void testNodeBetweeness() {

	
		double [] nodeBet;
		
		nodeBet = lib.computeNodeBetweeness(
									graph, 
									graph.directed, 
									2.0
									);
		assertEquals("the size of results is not OK", lib.getVertexCount(graph), nodeBet.length);
		
	
		
	}
	
	@Test(timeout = 1000)
	public void testAveragePathLength() {

	
		double r;
		
		r = lib.computeAveragePathLength(graph);
		
		assertTrue("the average path length is weird:"+r, r > 2);
		
		// TODO depends on the rewiring of the net
		assertTrue("the average path length is weird:"+r, r < 20);
		
		double d;
		
		d = lib.computeDiameter(graph);
		
		assertTrue("the diameter is weird:"+d, d > 2);
		
		assertTrue("the diameter is weird:"+r, d < 20);
		
		assertTrue("the diameter and average path length are not compliant :"+r+", "+d, d>r);
		
		
	}
	
	

	
	
}
