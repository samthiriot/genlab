package genlab.igraph.commons;

import genlab.core.model.exec.IComputationProgress;
import genlab.igraph.natjna.IIGraphProgressCallback;

import java.util.HashSet;
import java.util.Set;

import com.sun.jna.Pointer;

/**
 * Receives the progress from igraph, and writes it into a genlab progress
 * 
 * @author Samuel Thiriot
 *
 */
public class GenlabProgressCallback implements IIGraphProgressCallback {

	private double nextToTransmit = 0;
	
	public final static double THRESHOLD_TRANSMIT = 5.0;
	
	private final IComputationProgress glProgress;
	
	private final long glProgressInitialDone;

	/**
	 * Set of references we keep there to ensure the progress callback is not going to be garbage collected
	 * on the Java side, then accessed from C. This would lead to a dramatic crash of the JVM.
	 */
	private final static Set<GenlabProgressCallback> strongReferences = new HashSet<GenlabProgressCallback>(500);
	
	
	public GenlabProgressCallback(IComputationProgress progress) {
		glProgress = progress;
		
		glProgress.setProgressTotal(glProgress.getProgressTotalToDo()+100);
		glProgressInitialDone = glProgress.getProgressDone();
	}

	@Override
	public int igraph_progress_handler_t(String message, double percent, Pointer data) {
	
		if (percent >= nextToTransmit) {
			
			nextToTransmit = percent + THRESHOLD_TRANSMIT;
			//System.err.println(message+" "+percent);

			glProgress.setProgressMade(glProgressInitialDone+(new Double(percent)).longValue());
		}
		
		return 0;
	}
	
	
	/**
	 * Will keep a strong reference to this callback so it is not garbage collected. 
	 * @param c
	 */
	public static void keepStrongReference(GenlabProgressCallback c) {
		strongReferences.add(c);
	}
	
	public static void removeStrongReference(GenlabProgressCallback c) {
		strongReferences.remove(c);
	}

}
