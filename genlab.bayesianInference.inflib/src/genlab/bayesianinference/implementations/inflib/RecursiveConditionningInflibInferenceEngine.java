package genlab.bayesianinference.implementations.inflib;

import edu.ucla.belief.EliminationHeuristic;
import edu.ucla.belief.InferenceEngine;
import edu.ucla.belief.inference.RCInfo;
import edu.ucla.belief.io.PropertySuperintendent;

public final class RecursiveConditionningInflibInferenceEngine extends AbstractInflibInferenceEngine {

	public RecursiveConditionningInflibInferenceEngine(String filename) {
		super("RecursiveConditionning", filename);

	}

	@Override
	public InferenceEngine compileBN() {
		
		edu.ucla.belief.inference.RCEngineGenerator dynamator = new edu.ucla.belief.inference.RCEngineGenerator();
		edu.ucla.belief.inference.RCSettings settings = dynamator.getSettings( (PropertySuperintendent)bayesianNetwork);
		settings.setEliminationHeuristic( EliminationHeuristic.MIN_FILL );
		settings.setPrEOnly( false );
	    settings.setUserMemoryProportion( (double)1.0 );
	    try {
			settings.validateAllocation(bayesianNetwork);
		} catch (Throwable e) {
			throw new RuntimeException("Error while validating allocation",e);
		}
	    
	    // info for tech
	    {
	    	StringBuffer sb = new StringBuffer();
	    	sb.append("proportion memory: ").append(settings.getActualMemoryProportion()).append("\n");
	    	final RCInfo info = settings.getInfo();
	    	sb.append("full caching: ").append(info.cacheEntriesFullCaching()).append("\n");
	    	sb.append("allocatedCacheEntries: ").append(info.allocatedCacheEntries()).append("\n");
	    	sb.append("recursiveCallsFullCaching: ").append(info.recursiveCallsFullCaching()).append("\n");
	    	sb.append("recursiveCalls: ").append(info.recursiveCalls()).append("\n");
		    	
	    	messages.debugTech(sb.toString(), getClass());
		    
	    }
	    
	    return dynamator.manufactureInferenceEngine(bayesianNetwork);
	    
	}

}
