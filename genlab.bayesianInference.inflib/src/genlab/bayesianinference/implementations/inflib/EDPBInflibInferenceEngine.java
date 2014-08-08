package genlab.bayesianinference.implementations.inflib;

import edu.ucla.belief.InferenceEngine;
import edu.ucla.belief.io.PropertySuperintendent;
import edu.ucla.util.Setting.Settings;

public final class EDPBInflibInferenceEngine extends AbstractInflibInferenceEngine {

	public EDPBInflibInferenceEngine(String filename) {
		super("EDPB", filename);
	}

	@Override
	public InferenceEngine compileBN() {
		edu.ucla.belief.approx.RecoveryEngineGenerator dynamator = new edu.ucla.belief.approx.RecoveryEngineGenerator();

		Settings<edu.ucla.belief.approx.RecoverySetting> settings = dynamator.getSettings( (PropertySuperintendent)bayesianNetwork);
	    settings.put( edu.ucla.belief.approx.RecoverySetting.iterations, 100 );
	    settings.put( edu.ucla.belief.approx.RecoverySetting.timeout, 10000 );
	    settings.put( edu.ucla.belief.approx.RecoverySetting.threshold, 1.0E-7 );
	    settings.put( edu.ucla.belief.approx.RecoverySetting.heuristic, il2.inf.edgedeletion.EDAlgorithm.RankingHeuristic.mi );
	    settings.put( edu.ucla.belief.approx.RecoverySetting.recovery, 0 );
	    settings.put( edu.ucla.belief.approx.RecoverySetting.seed, 1681840860 );
	    settings.put( edu.ucla.belief.approx.RecoverySetting.compare2exact, false );
	    settings.put( edu.ucla.belief.approx.RecoverySetting.subalgorithm, edu.ucla.belief.CrouchingTiger.DynamatorImpl.zchugin );

	    return dynamator.manufactureInferenceEngine(bayesianNetwork);
	}

}
