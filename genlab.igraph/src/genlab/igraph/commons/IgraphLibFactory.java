package genlab.igraph.commons;

import genlab.core.usermachineinteraction.GLLogger;
import genlab.igraph.Rigraph.RIGraphLibImplementation;
import genlab.igraph.natjna.IGraphLibImplementationNative;
import genlab.igraph.natjna.IGraphRawLibrary;
import genlab.igraph.parameters.ChoiceOfImplementationParameter.EIgraphImplementation;

/**
 * Returns a valid implementation of an IGraph library
 * 
 * @author Samuel Thiriot
 */
public class IgraphLibFactory {

	private IgraphLibFactory() {
	
	}
	
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
		
		isIGraphAvailable = IGraphRawLibrary.isAvailable || RIGraphLibImplementation.isAvailable();
		
		
	}
	
	public static IGraphLibImplementation getImplementation(String pref) {
		
		return getImplementation(EIgraphImplementation.forLabel(pref));
	}
	
	public static IGraphLibImplementation getImplementation(Integer pref) {
		
		return getImplementation(EIgraphImplementation.values()[pref]);
	}

	public static IGraphLibImplementation getImplementation(EIgraphImplementation preference) {
		
		if (!isIGraphAvailable())
			throw new RuntimeException("R is not available on this configuration: neither the native library, nor the R installation is operational");
		
		switch (preference) {
		case JNA_ONLY:
			if (IGraphRawLibrary.isAvailable)
				return new IGraphLibImplementationNative();
			else 
				throw new RuntimeException("The parameter requires the native library but it is not working on this system; you might change the value of this parameter");

		case R_ONLY:
			if (RIGraphLibImplementation.isAvailable())
				return new RIGraphLibImplementation();
			else 
				throw new RuntimeException("The parameter requires the R library but it is not working on this system; you might change the value of this parameter");
			
		case JNA_OR_R:
			if (IGraphRawLibrary.isAvailable)
				return new IGraphLibImplementationNative();
			else
				return new RIGraphLibImplementation();

		case R_OR_JNA:
			if (RIGraphLibImplementation.isAvailable())
				return new RIGraphLibImplementation();
			else
				return new IGraphLibImplementationNative();

		default: 
			// unreachable
			return null;
		}

	}

}
