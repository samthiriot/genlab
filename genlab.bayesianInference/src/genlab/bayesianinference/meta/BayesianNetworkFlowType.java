package genlab.bayesianinference.meta;

import genlab.bayesianinference.IBayesianNetwork;
import genlab.core.commons.NotImplementedException;
import genlab.core.model.meta.basics.flowtypes.AbstractFlowType;

public class BayesianNetworkFlowType extends AbstractFlowType<IBayesianNetwork> {

	public static final BayesianNetworkFlowType SINGLETON = new BayesianNetworkFlowType();

	private BayesianNetworkFlowType() {
		super(
				"genlab.bayesianInference.bayesianNetwork", 
				"Bayesian network", 
				"a Bayesian network"
				);
		
		
	}

	@Override
	public IBayesianNetwork decodeFrom(Object value) {

		return (IBayesianNetwork)value;

	}

}
