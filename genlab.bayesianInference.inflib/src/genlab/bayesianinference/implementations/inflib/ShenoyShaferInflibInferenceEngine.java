package genlab.bayesianinference.implementations.inflib;

import edu.ucla.belief.EliminationHeuristic;
import edu.ucla.belief.InferenceEngine;
import edu.ucla.belief.io.PropertySuperintendent;

public final class ShenoyShaferInflibInferenceEngine extends AbstractInflibInferenceEngine {

	public ShenoyShaferInflibInferenceEngine(String filename) {
		super("ShenoyShafer", filename);

	}

	@Override
	public InferenceEngine compileBN() {
		// checked against CodeBandit 23/06/2010
	    edu.ucla.belief.inference.JEngineGenerator dynamator = new edu.ucla.belief.inference.JEngineGenerator();
	    edu.ucla.belief.inference.JoinTreeSettings settings = dynamator.getSettings( (PropertySuperintendent)bayesianNetwork, true );
	    settings.setEliminationHeuristic( EliminationHeuristic.MIN_FILL );
	    return dynamator.manufactureInferenceEngine(bayesianNetwork);
	}

}
