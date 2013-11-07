package genlab.igraph.natjna;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.sun.jna.Pointer;

public class TestIGraphCallback {

	@Test
	public void testAccessCallback() {
		
		StdoutProgressCallback t = new StdoutProgressCallback();
		
		Pointer p = IGraphRawLibrary.igraph_set_progress_handler(t);
		
	}
	
	private class TestProgressCallback implements IIGraphProgressCallback {
	
		public int count = 0;
		public double percent = 0;
		
		@Override
		public int igraph_progress_handler_t(String message, double percent,
				Pointer data) {
			count++;
			System.err.println(percent);
			this.percent = percent;
			return 0;
		}
	}
	
	
	@Test
	public void testCallbackCalled() {

		
		TestProgressCallback t = new TestProgressCallback();
		
		Pointer p = IGraphRawLibrary.igraph_set_progress_handler(t);
		
		IGraphLibrary lib = new IGraphLibrary();
		IGraphGraph g = null;
		try {
		
			g = lib.generateWattsStrogatz(1000, 1, 0.2, 4, false, false);
			//lib.computeAveragePathLength(g);
			lib.computeBetweeness(g, false);
			//lib.computeBetweenessEstimate(g, false, 0.1);

			assertTrue("callback was not called", t.count>0);
			
		} finally {
		
			if (g != null)
				lib.clearGraphMemory(g);
			
		}
	}

}
