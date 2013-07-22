package genlab.core.model.meta.basics.algos;

import genlab.core.parameters.IntParameter;
import genlab.core.parameters.Parameter;

// TODO doc !
public class IntegerFillTableAlgo extends ConstantFilledTableGenerator {
			
	public final static IntParameter PARAM_VALUE = new IntParameter("value", "value", "the value to fill the table with", new Integer(0));
	
	public IntegerFillTableAlgo() {
		super(
				"integer-filled table",
				"fills table of a given size with the given double value"
				);

		registerParameter(PARAM_VALUE);
	}

	
	@Override
	public Parameter<?> getParametersForConstantValue() {
		return PARAM_VALUE;
	}


}
