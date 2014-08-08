package genlab.bayesianinference.implementations.inflib;

import edu.ucla.belief.EliminationHeuristic;
import edu.ucla.belief.InferenceEngine;
import edu.ucla.belief.io.PropertySuperintendent;

public final class ZCInflibInferenceEngine extends AbstractInflibInferenceEngine {

	public ZCInflibInferenceEngine(String filename) {
		super("ZC", filename);
	}

	@Override
	public InferenceEngine compileBN() {
		 edu.ucla.belief.inference.ZCEngineGenerator dynamator = new edu.ucla.belief.inference.ZCEngineGenerator();
		 edu.ucla.belief.inference.JoinTreeSettings settings = dynamator.getSettings( (PropertySuperintendent)bayesianNetwork, true );
		 settings.setEliminationHeuristic( EliminationHeuristic.MIN_FILL );
		 return dynamator.manufactureInferenceEngine(bayesianNetwork);
	}

}
