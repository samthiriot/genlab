package genlab.core.model.meta.basics.algos;

import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;
import genlab.core.parameters.IntParameter;

public class ConstantValueInteger extends ConstantValue<Integer> {

	public static final InputOutput<Integer> OUTPUT = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON, 
			"constantvalue.integer.out", 
			"constant output", 
			"an output which is constant"
			);

	public ConstantValueInteger() {
		super(IntegerFlowType.SINGLETON, OUTPUT, "constant integer", "a constant integer value");
		
		registerParameter(new IntParameter(paramId, "value", "the value of this constant", 0));

	}


}
