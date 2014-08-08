package genlab.bayesianinference.implementations.inflib;

import edu.ucla.belief.EliminationHeuristic;
import edu.ucla.belief.InferenceEngine;
import edu.ucla.belief.io.PropertySuperintendent;

public final class LoopyInflibInferenceEngine extends AbstractInflibInferenceEngine {

	public LoopyInflibInferenceEngine(String filename) {
		super("Loopy", filename);

	}

	@Override
	public InferenceEngine compileBN() {
	    edu.ucla.belief.approx.PropagationEngineGenerator dynamator = new edu.ucla.belief.approx.PropagationEngineGenerator();

	    edu.ucla.belief.approx.BeliefPropagationSettings settings = dynamator.getSettings( (PropertySuperintendent)bayesianNetwork);
	    settings.setTimeoutMillis( 10000 );
	    settings.setMaxIterations( 100 );
	    settings.setScheduler( edu.ucla.belief.approx.MessagePassingScheduler.TOPDOWNBOTTUMUP );
	    settings.setConvergenceThreshold( 1.0E-7 );
	    
	    return dynamator.manufactureInferenceEngine(bayesianNetwork);
	}

}
