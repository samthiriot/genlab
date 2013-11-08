package genlab.igraph.natjna;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class TestIgraphGraphMetrics {


	@Test
	public void testManyLibsManyGraphsNotSameMetrics() {
		
		final List<Double> generatedValues = Collections.synchronizedList(new LinkedList<Double>());
		
		final int COUNT_PARALLEL_THREAD = 100;
		final int GRAPH_SIZE = 5000;

		System.err.println("starting "+COUNT_PARALLEL_THREAD+" threads");
		// start threads
		for (int i = 0; i<COUNT_PARALLEL_THREAD; i++) {
			Runnable r = new Runnable() {
	
				@Override
				public void run() {
					
					IGraphLibrary lib = new IGraphLibrary();
					IGraphGraph g = lib.generateWattsStrogatz(GRAPH_SIZE, 1, 0.1, 2, false, false);
					Double clusteringLocal = lib.computeGlobalClusteringLocal(g);
					
					generatedValues.add(clusteringLocal);
					
					lib.clearGraphMemory(g); // TODO !
					
				}
				
			};
			(new Thread(r)).start();
		}
		

		while (generatedValues.size() < COUNT_PARALLEL_THREAD) {
			System.err.println("waiting for "+generatedValues.size()+" threads");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		IGraphLibrary lib = new IGraphLibrary();

		System.err.println("threads done. Now checking not equals");
		int countIdentical = 0;
		for (int x=0; x<generatedValues.size(); x++) {
			for (int y=x+1; y<generatedValues.size(); y++) {
								
				Double d1 = generatedValues.get(x);
				Double d2 = generatedValues.get(y);
				
				System.err.println("compare "+d1+" and "+d2);

				if (d1.equals(d2))
					countIdentical++;
				
												
			}
		}
	
						
		assertTrue("many instances of objects are similar: "+countIdentical, countIdentical < 1000);
		
		
	}

}
