package genlab.core.model.meta.basics.algos;

import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;
import genlab.core.parameters.IntParameter;
import genlab.core.parameters.Parameter;

public class ConstantValueInteger extends ConstantValue<Integer> {

	public static final InputOutput<Integer> OUTPUT = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON, 
			"constantvalue.integer.out", 
			"constant output", 
			"an output which is constant"
			);

	public ConstantValueInteger() {
		super(
				IntegerFlowType.SINGLETON, 
				OUTPUT, 
				"constant integer", 
				"a constant integer value",
				null
				);
		
	}

	@Override
	protected Parameter<Integer> createConstantParameter() {
		return new IntParameter(paramId, "value", "the value of this constant", 0);
	}

	@Override
	public Integer getPriorityForIntuitiveCreation() {
		// higher pririty than double, so we use integer as soon as possible (double will always be accepted for numbers)
		return 2;
	}


}
