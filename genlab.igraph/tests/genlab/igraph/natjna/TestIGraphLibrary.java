package genlab.igraph.natjna;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jna.ptr.PointerByReference;

public class TestIGraphLibrary {

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
	public void testInstance() {
		IGraphLibrary.INSTANCE.toString();
	}
	
	@Test
	public void testEmptyGraph() {
		
		//PointerByReference graph = new PointerByReference();
		
		IGraphLibrary.IGraphGraph.ByReference graphReference = new IGraphLibrary.IGraphGraph.ByReference();
		
		
		System.err.println("call igraph...");
		IGraphLibrary.INSTANCE.igraph_empty(
				graphReference, 
				100,
				false
				);
		System.err.println("back from igraph.");
		assertNotNull("no graph returned", graphReference);

		System.err.flush();

		try {
			Thread.sleep(40);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println("create structure.");

		//IGraphGraph gg = new IGraphGraph(graph.getValue());
		//System.err.println("n="+gg.n);
		
		//System.err.flush();
		
		System.err.println("results.");
		
		//System.err.println("vertices: "+IGraphLibrary.INSTANCE.igraph_vcount(graph.getValue()));
		assertEquals(
				"wrong edge count",
				0, 
				IGraphLibrary.INSTANCE.igraph_ecount(graphReference)
				);
		assertEquals(
				"wrong vertex count",
				100, 
				IGraphLibrary.INSTANCE.igraph_vcount(graphReference)
				);
	
			
	}
	
	@Test
	public void testWattsStrogatz() {
		IGraphLibrary.IGraphGraph graph = new IGraphLibrary.IGraphGraph(); 
		System.err.println("call igraph...");
		IGraphLibrary.INSTANCE.igraph_watts_strogatz_game(
				graph, 
				2, 
				100, 
				2, 
				0.1, 
				false, 
				false
				);
		System.err.println("back from igraph.");
		System.err.flush();

		assertNotNull(graph);
	}
	
	
	
	

}
