package genlab.bayesianinference.implementations.inflib;

import genlab.bayesianInference.AbstractTestNetwork;
import genlab.bayesianinference.IBayesianNetwork;

public class TestBasicsInflib extends AbstractTestNetwork {

	@Override
	protected IBayesianNetwork createEmptyNetwork() {
		return new InflibBaysianNetwork();
	}
	
	

}
