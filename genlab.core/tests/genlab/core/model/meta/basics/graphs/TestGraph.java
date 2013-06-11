package genlab.core.model.meta.basics.graphs;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestGraph {

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

	
	protected IGenlabGraph getGraph() {
		
		IGenlabGraph g = GraphFactory.createGraph("1", GraphDirectionality.DIRECTED, false);
		
		return g;
		
	}
	
	@Test
	public void testClone() {
		
		IGenlabGraph g1 = getGraph();
		
		g1.declareGraphAttribute("test1", Integer.class);
		
		IGenlabGraph g2 = g1.clone("cloned");

		
		// same properties
		assertEquals(g1.getVerticesCount(), g2.getVerticesCount());
		assertEquals(g1.getEdgesCount(), g2.getEdgesCount());

		assertEquals(g1.getDirectionality(), g2.getDirectionality());
		assertEquals(g1.isMultiGraph(), g2.isMultiGraph());

		assertEquals(g1.getDeclaredGraphAttributes().size(), g2.getDeclaredGraphAttributes().size());
		assertEquals(g1.getDeclaredVertexAttributes().size(), g2.getDeclaredVertexAttributes().size());
		assertEquals(g1.getDeclaredEdgeAttributes().size(), g2.getDeclaredEdgeAttributes().size());

		// other properties
		assertEquals(g2.getGraphId(), "cloned");
		
		// not the same objects
		assertNotSame(g2, g1);
		// (if no vertex or no edge, then an empty collection (singleton) could be returned)
		if (g2.getVerticesCount() > 0) 
			assertNotSame(g2.getVertices(), g1.getVertices());	
		if (g2.getEdgesCount() > 0)
			assertNotSame(g2.getEdges(), g1.getEdges());
		
		// test to add something
		// ... add a vertex: it should be in one set, but not in the other one.
		g1.addVertex("added1");
		assertTrue(g1.containsVertex("added1"));
		assertFalse(g2.containsVertex("added1"));
		
		// TODO test change attribute value
		g1.setGraphAttribute("test1", new Integer(2));
		g2.setGraphAttribute("test1", new Integer(3));
		assertEquals((Integer)g1.getGraphAttribute("test1"), new Integer(2));
		assertEquals((Integer)g2.getGraphAttribute("test1"), new Integer(3));
		
		// ... declare a novel attribute in g1; it should not exist in g2
		g1.declareGraphAttribute("test2", Integer.class);
		assertTrue(g1.getDeclaredGraphAttributesAndTypes().containsKey("test2"));
		assertFalse(g2.getDeclaredGraphAttributesAndTypes().containsKey("test2"));
		
	}

}


