package genlab.igraph.commons;

import genlab.core.usermachineinteraction.GLLogger;
import genlab.igraph.Rigraph.RIGraphLibImplementation;
import genlab.igraph.natjna.IGraphLibImplementationNative;
import genlab.igraph.natjna.IGraphRawLibrary;

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
		if (isIGraphAvailable == null)
			loadIgraphImplementation();
		return isIGraphAvailable;
	}
	
	/**
	 * detects the possible accesses to igraph, selects the best implementation, 
	 * and defines the variable to indicate accessibility - or not - of igraph.
	 */
	private static void loadIgraphImplementation() {
		
		
		// display messages
		if (IGraphRawLibrary.isAvailable) {
			GLLogger.infoTech("the igraph native library is available :-)", IgraphLibFactory.class);
		}  else {
			GLLogger.infoTech("the igraph native library is not available on this platform", IgraphLibFactory.class);
		}
		
		if (RIGraphLibImplementation.isAvailable()) {
			GLLogger.infoTech("the R/igraph library is available :-)", IgraphLibFactory.class);
		} 
		
		// choose one
		if (IGraphRawLibrary.isAvailable) {
			GLLogger.infoTech("we will use the igraph native library for computations; it is quick, but does not enables reproductibility of experiments", IgraphLibFactory.class);
			implementation = new IGraphLibImplementationNative();
		} else if (RIGraphLibImplementation.isAvailable()) {
			GLLogger.infoTech("we will use the igraph R/igraph library for igraph operations", IgraphLibFactory.class);
			implementation = new RIGraphLibImplementation();
		} else {
			GLLogger.errorUser("No igraph installation was detected on this system. Many graph algorithms will not be available. Please install R-cran and the related packages", IgraphLibFactory.class);
			implementation = null;
		}
		isIGraphAvailable = implementation != null;

		
		return;
		
		
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
