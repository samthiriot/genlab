package genlab.igraph.natjna;

import com.sun.jna.Pointer;

public class StdoutProgressCallback implements IIGraphProgressCallback {

	private double nextToTransmit = 0;
	
	public final static double THRESHOLD_TRANSMIT = 0.5;
	
	public StdoutProgressCallback() {
		
	}

	@Override
	public int igraph_progress_handler_t(String message, double percent, Pointer data) {
	
		if (percent >= nextToTransmit) {
			
			nextToTransmit = percent + THRESHOLD_TRANSMIT;
			System.err.println(message+" "+percent);

		}
		
		
		return 0;
	}

}
