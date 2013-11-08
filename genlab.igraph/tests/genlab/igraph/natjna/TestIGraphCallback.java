package genlab.igraph.natjna;

import genlab.igraph.commons.StdoutProgressCallback;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.sun.jna.Native;
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
		

		@Override
		protected void finalize() throws Throwable {

			// unregister it
			IGraphRawLibrary.igraph_set_progress_handler(null);
			
			super.finalize();
		}

	}
	
	
	@Test
	public void testCallbackCalled() {

		//Native.setProtected(true);
		
		TestProgressCallback t = new TestProgressCallback();
		//StdoutProgressCallback t = new StdoutProgressCallback();
		
		IGraphRawLibrary.igraph_set_progress_handler(t);
		
		IGraphLibrary lib = new IGraphLibrary();
		IGraphGraph g = null;
		try {
			g = lib.generateWattsStrogatz(1000, 1, 0.2, 4, false, false);
		
			lib.computeAveragePathLength(g);
			System.err.println("generated");
			lib.computeBetweenessEstimate(g, false, 2.0);
			
			assertTrue("callback was not called", t.count>0);
			
		} finally {
		
			IGraphRawLibrary.igraph_set_progress_handler(null);
			
			if (g != null)
				lib.clearGraphMemory(g);
			
		}
	}

}
