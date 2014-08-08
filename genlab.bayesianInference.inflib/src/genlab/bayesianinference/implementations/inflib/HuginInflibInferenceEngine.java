package genlab.bayesianinference.implementations.inflib;

import java.io.PrintStream;

import edu.ucla.belief.EliminationHeuristic;
import edu.ucla.belief.InferenceEngine;
import edu.ucla.belief.io.PropertySuperintendent;

public final class HuginInflibInferenceEngine extends AbstractInflibInferenceEngine {

	public HuginInflibInferenceEngine(String filename) {
		super("Hugin", filename);

	}

	@Override
	public InferenceEngine compileBN() {
		edu.ucla.belief.inference.HuginEngineGenerator dynamator = new edu.ucla.belief.inference.HuginEngineGenerator();
		edu.ucla.belief.inference.JoinTreeSettings settings = dynamator.getSettings( (PropertySuperintendent)bayesianNetwork, true );
		settings.setEliminationHeuristic( EliminationHeuristic.MIN_FILL );
		return dynamator.manufactureInferenceEngine(bayesianNetwork);
	}

	

}
