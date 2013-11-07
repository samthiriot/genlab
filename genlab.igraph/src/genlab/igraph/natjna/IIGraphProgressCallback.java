package genlab.igraph.natjna;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

/**
 * Called by igraph during the computation
 * 
 * @see https://jna.java.net/javadoc/com/sun/jna/Callback.html
 * 
 * @author Samuel Thiriot
 *
 */
/*
 * typedef int igraph_progress_handler_t(const char *message, igraph_real_t percent,
				      void *data);
 */
public interface IIGraphProgressCallback extends Callback {

	/**
	 * Should return IGRAPH_SUCCESS (=0)
	 * @param message
	 * @param percent
	 * @param data
	 * @return
	 */
	public int igraph_progress_handler_t(String message, double percent, Pointer data);
	
}
