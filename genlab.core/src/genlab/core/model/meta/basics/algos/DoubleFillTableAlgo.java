package genlab.core.model.meta.basics.algos;

import genlab.core.parameters.DoubleParameter;
import genlab.core.parameters.Parameter;

// TODO doc !
public class DoubleFillTableAlgo extends ConstantFilledTableGenerator {
			

	public final static DoubleParameter PARAM_VALUE = new DoubleParameter("value", "value", "the value to fill the table with", new Double(0.0));

	public DoubleFillTableAlgo() {
		super(
				"double-filled table",
				"fills table of a given size with the given Integer value"
				);

		registerParameter(PARAM_VALUE);
	}

	

	@Override
	public Parameter<?> getParametersForConstantValue() {
		return PARAM_VALUE;
	}


}
