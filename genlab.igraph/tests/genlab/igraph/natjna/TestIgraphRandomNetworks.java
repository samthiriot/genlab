package genlab.igraph.natjna;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class TestIgraphRandomNetworks {

	@Test
	public void testTwoWSoneLibDifferent() {
		
		IGraphLibrary lib1 = new IGraphLibrary();

		IGraphGraph g1 = null;
		IGraphGraph g2 = null;
		
		try {
				
			
			g1 = lib1.generateWattsStrogatz(1000, 1, 0.1, 2, false, false);
			g2 = lib1.generateWattsStrogatz(1000, 1, 0.1, 2, false, false);
			
			assertNotEquals("objects are similar", g1, g2);
			assertNotEquals("objects are similar", g1.graphStruct, g2.graphStruct);
			
			boolean iso = lib1.computeIsomorphicm(g1, g2);
			assertFalse("graphs are isomorphic", iso);
			
		} finally {
			if (g1 != null)
				lib1.clearGraphMemory(g1);
			if (g2 != null)
				lib1.clearGraphMemory(g2);	
		}
		
	}
	
	@Test
	public void testIsoMorphismSameLib() {
		
		IGraphLibrary lib1 = new IGraphLibrary();

		IGraphGraph g1 = null;
		IGraphGraph g2 = null;
		
		try {
				
			g1 = lib1.generateWattsStrogatz(1000, 1, 0.0, 2, false, false);
			g2 = lib1.generateWattsStrogatz(1000, 1, 0.0, 2, false, false);
			
			assertNotEquals("objects are similar", g1, g2);
			assertNotEquals("objects are similar", g1.graphStruct, g2.graphStruct);
			
			boolean iso = lib1.computeIsomorphicm(g1, g2);
			assertTrue("graphs are not isomorphic", iso);
			
		} finally {
			if (g1 != null)
				lib1.clearGraphMemory(g1);
			if (g2 != null)
				lib1.clearGraphMemory(g2);	
		}
		
	}
	
	@Test
	public void testIsoMorphismTwoLibs() {
		
		IGraphLibrary lib1 = new IGraphLibrary();
		IGraphLibrary lib2 = new IGraphLibrary();

		IGraphGraph g1 = null;
		IGraphGraph g2 = null;
		
		try {
				
			g1 = lib1.generateWattsStrogatz(1000, 1, 0.0, 2, false, false);
			g2 = lib2.generateWattsStrogatz(1000, 1, 0.0, 2, false, false);
			
			assertNotEquals("objects are similar", g1, g2);
			assertNotEquals("objects are similar", g1.graphStruct, g2.graphStruct);
			
			boolean iso = lib1.computeIsomorphicm(g1, g2);
			assertTrue("graphs are not isomorphic", iso);
			
			boolean iso2 = lib2.computeIsomorphicm(g1, g2);
			assertTrue("graphs are not isomorphic", iso2);
			
			
		} finally {
			if (g1 != null)
				lib1.clearGraphMemory(g1);
			if (g2 != null)
				lib2.clearGraphMemory(g2);	
		}
		
	}
	
	@Test
	public void testTwoWStwoLibsDifferent() {
		
		IGraphLibrary lib1 = new IGraphLibrary();
		IGraphLibrary lib2 = new IGraphLibrary();
		
		IGraphGraph g1 = lib1.generateWattsStrogatz(1000, 1, 0.1, 2, false, false);
		IGraphGraph g2 = lib2.generateWattsStrogatz(1000, 1, 0.1, 2, false, false);
		
		assertNotEquals("objects are similar", g1, g2);
		assertNotEquals("objects are similar", g1.graphStruct, g2.graphStruct);
		
		boolean iso = lib1.computeIsomorphicm(g1, g2);
		assertFalse("graphs are isomorphic", iso);
		
		
	}
	
	@Test
	public void testManyLibsManyGraphsNotIsomorphic() {
		
		final List<IGraphGraph> generatedGraphs = Collections.synchronizedList(new LinkedList<IGraphGraph>());
		
		final int COUNT_PARALLEL_THREAD = 20;
		final int GRAPH_SIZE = 10000;

		System.err.println("starting "+COUNT_PARALLEL_THREAD+" threads");
		// start threads
		for (int i = 0; i<COUNT_PARALLEL_THREAD; i++) {
			Runnable r = new Runnable() {
	
				@Override
				public void run() {
					
					IGraphLibrary lib = new IGraphLibrary();
					IGraphGraph g = lib.generateWattsStrogatz(GRAPH_SIZE, 1, 0.1, 2, false, false);
					generatedGraphs.add(g);
					
				}
				
			};
			(new Thread(r)).start();
		}
		

		while (generatedGraphs.size() < COUNT_PARALLEL_THREAD) {
			System.err.println("waiting for "+generatedGraphs.size()+" threads");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		IGraphLibrary lib = new IGraphLibrary();

		System.err.println("threads done. Now checking isomorphism");
		int countIso = 0;
		for (int x=0; x<generatedGraphs.size(); x++) {
			for (int y=x+1; y<generatedGraphs.size(); y++) {
				
				System.err.println("compare "+x+" and "+y);
				
				IGraphGraph g1 = generatedGraphs.get(x);
				IGraphGraph g2 = generatedGraphs.get(y);
				
				assertNotEquals("objects are similar", g1, g2);
				assertNotEquals("objects are similar", g1.graphStruct, g2.graphStruct);
								
				boolean iso = lib.computeIsomorphicm(g1, g2);
				
				if (iso)
					countIso ++;
				
				
			}
		}
		
		assertTrue("many graphs are isomorphic ("+countIso+")", countIso < 10);
		
		// clear memory
		for (IGraphGraph g: generatedGraphs) {
			g.lib.clearGraphMemory(g);
		}
		
	}
	

}
