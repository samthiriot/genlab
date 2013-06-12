package genlab.igraph.natjna;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import genlab.igraph.natjna.IGraphRawLibrary.IGraphGraph;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jna.Native;

public class TestIGraphLibrary {

	IGraphLibrary lib = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		Native.setProtected(true);
		lib = new IGraphLibrary();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	

	@Test
	public void testInstance() {
		assertNotNull(lib);
	}
	
	@Test
	public void testVersion() {
		
		String version = lib.getVersionString();
		
		assertNotNull(version);
		
		System.err.println(version);
		
	}

	@Test
	public void testER() {
		
		genlab.igraph.natjna.IGraphGraph g = lib.generateErdosRenyiGNP(100, 0.6);
		
		assertNotNull(g.igraphPointer);
		assertEquals(100, lib.getVertexCount(g));
		assertEquals(false, lib.isDirected(g));
		
		
	}

	@Test
	public void testEmptyGraph() {
		
		genlab.igraph.natjna.IGraphGraph g = lib.generateEmpty(100, false);
		
		assertNotNull(g.igraphPointer);
		assertEquals(100, lib.getVertexCount(g));
		assertEquals(false, lib.isDirected(g));
		assertEquals(0, lib.getEdgeCount(g));
		
			
	}
	
	@Test
	public void testWattsStrogatz() {

		genlab.igraph.natjna.IGraphGraph g = lib.generateWattsStrogatz(500, 1, 0.1, 2, false, false);
		
		assertNotNull(g.igraphPointer);
		assertEquals(100, lib.getVertexCount(g));
		assertEquals(false, lib.isDirected(g));
		
		System.err.println(lib.computeAveragePathLength(g));
		
	}
	
	
	@Test
	public void testAddEdge() {

		System.err.println("creating an empy graph...");

		genlab.igraph.natjna.IGraphGraph g = lib.generateEmpty(100, false);
		
		assertEquals(100, lib.getVertexCount(g));
		assertEquals(false, lib.isDirected(g));
		assertEquals(0, lib.getEdgeCount(g));
		
		System.err.println("adding an edge...");
		lib.addEdge(g, 1, 2);
		
		assertEquals(100, lib.getVertexCount(g));
		assertEquals(false, lib.isDirected(g));
		assertEquals(1, lib.getEdgeCount(g));
		
		System.err.println("adding more edges...");
		lib.addEdge(g, 2, 3);
		lib.addEdge(g, 3, 4);
		lib.addEdge(g, 6, 7);
		lib.addEdge(g, 6, 8);
		lib.addEdge(g, 9, 99);
		
		assertEquals(100, lib.getVertexCount(g));
		assertEquals(false, lib.isDirected(g));
		assertEquals(6, lib.getEdgeCount(g));
		
		System.err.println("adding plenty of edges !");
		for (int i=1; i<99; i++) {
			lib.addEdge(g, 0, i);
		}
		
		assertEquals(100, lib.getVertexCount(g));
		assertEquals(false, lib.isDirected(g));
		assertEquals(106, lib.getEdgeCount(g));
		
	}
	
	
	@Test
	public void testCopy() {
		
		IGraphGraph from = new IGraphGraph();
		IGraphGraph to = new IGraphGraph();

		// TODO !
		
	}
	
	

}
