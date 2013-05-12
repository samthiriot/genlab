package genlab.basics.javaTypes.graphs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import genlab.core.algos.WrongParametersException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO test multiplex !
 * 
 * @author Samuel Thiriot
 */
public class TestGraphs {
	

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
	public void testDirectionalitySimpleUndirected() {
		
		IGenlabGraph graph = GraphFactory.createGraph("testA", GraphDirectionality.UNDIRECTED, false);
		
		// basic properties
		graph.addVertex("a");
		graph.addVertex("b");
		assertTrue(graph.containsVertex("a"));
		assertTrue(graph.containsVertex("b"));
		assertEquals(graph.getVerticesCount(), 2);
		assertEquals(graph.getEdgesCount(), 0);
		
		// can not add a the same vertex twice
		try {
			graph.addVertex("a");
			fail("exception expected");
		} catch (WrongParametersException e) {
			// ok
		}
		
		// can not create a directed edge in an undirected graph
		try {
			graph.addEdge("a", "b", true);
			fail("exception expected");
		} catch (WrongParametersException e) {
			// ok
		}
		
		// can add one undirected edge 
		graph.addEdge("a", "b", false);
		assertTrue(graph.containsVertex("a"));
		assertTrue(graph.containsVertex("b"));
		assertTrue(graph.containsEdge("a", "b"));
		assertEquals(graph.getVerticesCount(), 2);
		assertEquals(graph.getEdgesCount(), 1);
		
		// can not create the same edge twice
		try {
			graph.addEdge("a", "b", false);
			fail("exception expected");
		} catch (WrongParametersException e) {
			// ok
		}
		
		// can not create the same edge twice, even if reversed
		try {
			graph.addEdge("b", "a", false);
			fail("exception expected");
		} catch (WrongParametersException e) {
			// ok
		}
		
		
		
	}
	
	@Test
	public void testDirectionalitySimpleDirected() {
		
		IGenlabGraph graph = GraphFactory.createGraph("testA", GraphDirectionality.DIRECTED, false);
		
		// basic properties
		graph.addVertex("a");
		graph.addVertex("b");
		assertTrue(graph.containsVertex("a"));
		assertTrue(graph.containsVertex("b"));
		assertEquals(graph.getVerticesCount(), 2);
		assertEquals(graph.getEdgesCount(), 0);
		
		// can not add a the same vertex twice
		try {
			graph.addVertex("a");
			fail("exception expected");
		} catch (WrongParametersException e) {
			// ok
		}
		
		// can not create an undirected edge in an directed graph
		try {
			graph.addEdge("a", "b", false);
			fail("exception expected");
		} catch (WrongParametersException e) {
			// ok
		}
		
		// can add one undirected edge 
		graph.addEdge("a", "b", true);
		assertTrue(graph.containsVertex("a"));
		assertTrue(graph.containsVertex("b"));
		assertTrue(graph.containsEdge("a", "b"));
		assertEquals(graph.getVerticesCount(), 2);
		assertEquals(graph.getEdgesCount(), 1);
		
		// can not create the same edge twice
		try {
			graph.addEdge("a", "b", true);
			fail("exception expected");
		} catch (WrongParametersException e) {
			// ok
		}
		
		// can  create the same edge twice if reversed
		graph.addEdge("b", "a", true);
		assertEquals(graph.getVerticesCount(), 2);
		assertEquals(graph.getEdgesCount(), 2);
		
		
	}

	@Test
	public void testDirectionalitySimpleMixed() {
		
		IGenlabGraph graph = GraphFactory.createGraph("testA", GraphDirectionality.MIXED, false);
		
		// basic properties
		graph.addVertex("a");
		graph.addVertex("b");
		assertTrue(graph.containsVertex("a"));
		assertTrue(graph.containsVertex("b"));
		assertEquals(graph.getVerticesCount(), 2);
		assertEquals(graph.getEdgesCount(), 0);
		
		// can not add a the same vertex twice
		try {
			graph.addVertex("a");
			fail("exception expected");
		} catch (WrongParametersException e) {
			// ok
		}
		
		// can create an undirected edge in an directed graph
		graph.addEdge("a", "b", false);
		
		// can not create the same edge twice
		try {
			graph.addEdge("a", "b", false);
			fail("exception expected");
		} catch (WrongParametersException e) {
			// ok
		}
		
		// even in the reverse order
		try {
			graph.addEdge("b", "a", true);
			fail("exception expected");
		} catch (WrongParametersException e) {
			// ok
		}
		
		
		assertEquals(2, graph.getVerticesCount());
		assertEquals(1, graph.getEdgesCount());
				
		
	}

}
