package genlab.igraph.implementations.Rigraph;

import static org.junit.Assert.fail;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.Rigraph.RIGraph2Genlab;
import genlab.r.rsession.Genlab2RSession;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.math.R.Rsession;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.REXPMismatchException;

public class TestIGraph {

	private Rsession rsession;
	
	public void setUp(){
		rsession = Genlab2RSession.createNewLocalRSession();;
	}
	
	public void tearDown( ) {
		if (rsession != null)
			rsession.end();
	}
	
	@Test
	public void testIGraphInstalled() {
		Rsession rsession = Genlab2RSession.createNewLocalRSession();

		boolean res = rsession.isPackageInstalled("igraph", null);
			
		Assert.assertTrue("the igraph package should be installed", res);
	}

	@Test
	public void testIGraphLoadLib() {
		Rsession rsession = Genlab2RSession.createNewLocalRSession();

		rsession.loadPackage("igraph");
		boolean res = rsession.isPackageLoaded("igraph");
			
		Assert.assertTrue("the igraph package should be loaded", res);
	}
	

	
	@Test
	public void testIGraphGetVersionDirect() {
		Rsession rsession = Genlab2RSession.createNewLocalRSession();

		rsession.loadPackage("igraph");
		boolean res = rsession.isPackageLoaded("igraph");
		
		Assert.assertTrue("the igraph package should be loaded", res);
		
		REXP version = rsession.eval("igraph.version()");
		
		try {
			System.out.println(version.asString());
		} catch (REXPMismatchException e) {
			e.printStackTrace();
			fail();
		}
		
				
	}

	
	@Test
	public void testIGraphGetVersionPackage() {
		Rsession rsession = Genlab2RSession.createNewLocalRSession();

		rsession.loadPackage("igraph");
		boolean res = rsession.isPackageLoaded("igraph");
		
		Assert.assertTrue("the igraph package should be loaded", res);
		
		REXP version = rsession.eval("packageDescription(\"igraph\")$Version");
		
		try {
			System.out.println(version.asString());
		} catch (REXPMismatchException e) {
			e.printStackTrace();
			fail();
		}
				
	}

	@Test
	public void testIGraphEvaluatePropertiesGraph() {
		
		Rsession rsession = Genlab2RSession.createNewLocalRSession();

		// load lib
		rsession.loadPackage("igraph");
		boolean res = rsession.isPackageLoaded("igraph");
		Assert.assertTrue("the igraph package should be loaded", res);
		
		// create graph
		REXP averagePathLength = rsession.eval("g <- watts.strogatz.game(1, 1000, 2, 0.1, loops = FALSE, multiple = FALSE)");
		averagePathLength = rsession.eval("average.path.length(g)");
		
		try {
			System.err.println("average path length: "+averagePathLength.asDouble());
		} catch (REXPMismatchException e) {
			fail();
		}
		
		
	}
	
	@Test
	public void testIGraphWriteGraphToFile() {
		
		Rsession rsession = Genlab2RSession.createNewLocalRSession();

		// load lib
		rsession.loadPackage("igraph");
		boolean res = rsession.isPackageLoaded("igraph");
		Assert.assertTrue("the igraph package should be loaded", res);
		
		// create graph
		rsession.eval("g <- watts.strogatz.game(1, 1000, 2, 0.1, loops = FALSE, multiple = FALSE)");
		
		File fileGraph = RIGraph2Genlab.saveGraphFromRIgraphToFile(rsession, "g");
		
		Assert.assertTrue(fileGraph.isFile());
		Assert.assertTrue(fileGraph.exists());
		Assert.assertTrue(fileGraph.canRead());
		
	}

	@Test
	public void testIGraphLoadInGenlab() {
		
		Rsession rsession = Genlab2RSession.createNewLocalRSession();

		// load lib
		rsession.loadPackage("igraph");
		boolean res = rsession.isPackageLoaded("igraph");
		Assert.assertTrue("the igraph package should be loaded", res);
		
		// create graph
		rsession.eval("g <- watts.strogatz.game(1, 1000, 2, 0.1, loops = FALSE, multiple = FALSE)");

		// load the graph from R
		IGenlabGraph graph = RIGraph2Genlab.loadGraphFromRIgraph(rsession, "g", false, new ListOfMessages());
		
		// reclaim R memory
		rsession.unset("g");
		
		Assert.assertNotNull(graph);
		// should have the right count of vertices
		Assert.assertEquals(1000l, graph.getVerticesCount());
		// should have about the right number of edges
				
	}
	

	@Test
	public void testIGraphLoadFileWithNoOrphan() {
		
		Rsession rsession = Genlab2RSession.createNewLocalRSession();

		// load lib
		rsession.loadPackage("igraph");
		rsession.isPackageLoaded("igraph");
		
		// create a graph
		rsession.eval("g <- watts.strogatz.game(1, 1000, 2, 0.05, loops = FALSE, multiple = FALSE)");

		ListOfMessages messages = new ListOfMessages();
		
		// then load it as a genlab graph
		IGenlabGraph g = RIGraph2Genlab.loadGraphFromRIgraph(rsession, "g", false, messages);
		// and save it again
		RIGraph2Genlab.loadGraphToRIgraph(g, rsession, "g2", messages);
		
		// compare both now
		REXP exp = rsession.eval("graph.isomorphic(g, g2)");
		Assert.assertTrue(((REXPLogical)exp).isTRUE()[0]);

		
	}
	

	@Test
	public void testIGraphLoadFileWithOrphan() {
		
		Rsession rsession = Genlab2RSession.createNewLocalRSession();

		// load lib
		rsession.loadPackage("igraph");
		rsession.isPackageLoaded("igraph");
		
		// create a graph
		rsession.eval("g <- watts.strogatz.game(1, 1000, 2, 0.05, loops = FALSE, multiple = FALSE)");
		rsession.eval("add.vertices(graph=g, nv=10)");

		ListOfMessages messages = new ListOfMessages();
		
		// then load it as a genlab graph
		IGenlabGraph g = RIGraph2Genlab.loadGraphFromRIgraph(rsession, "g", false, messages);
		// and save it again
		RIGraph2Genlab.loadGraphToRIgraph(g, rsession, "g2", messages);
		
		// compare both now
		REXP exp = rsession.eval("graph.isomorphic(g, g2)");
		Assert.assertTrue(((REXPLogical)exp).isTRUE()[0]);
		
		
	}

		
}
