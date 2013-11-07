package genlab.igraph.natjna;

import com.sun.jna.Pointer;

public class StdoutProgressCallback implements IIGraphProgressCallback {

	public StdoutProgressCallback() {
	}

	@Override
	public int igraph_progress_handler_t(String message, double percent,
			Pointer data) {
		
		System.err.println(message+" "+percent);
		
		return 0;
	}

}
