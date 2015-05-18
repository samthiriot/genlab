package genlab.igraph.commons;

import genlab.igraph.Rigraph.RIGraphLibImplementation;
import genlab.igraph.natjna.IGraphLibImplementationNative;

/**
 * Returns a valid implementation of an IGraph library
 * 
 * @author Samuel Thiriot
 */
public class IgraphLibFactory {

	private IgraphLibFactory() {
	
	}
	
	private static IGraphLibImplementation implementation = null;
	private static Boolean isIGraphAvailable = null;
	
	public static boolean isIGraphAvailable() {
		return isIGraphAvailable;
	}
	
	private static void loadIgraphImplementation() {
		
		// TODO which one should be prefered ? 
		implementation = new RIGraphLibImplementation();
		isIGraphAvailable = true;
		
		return;
		/*
		if (!IGraphRawLibrary.isAvailable) {
			GLLogger.infoTech("the igraph native library is not available", IgraphLibFactory.class);
		} else {
			implementation = new IGraphLibImplementationNative();
		}*/
		
		
	}
	
	public static IGraphLibImplementation getImplementation() {
		
		if (isIGraphAvailable == null) {
			loadIgraphImplementation();
		}
		if (!isIGraphAvailable) {
			// TODO meaningfull error message
			throw new RuntimeException("no igraph implementation is available; please install R and the Rsession package");
		}
		return implementation;
		
	}

}
