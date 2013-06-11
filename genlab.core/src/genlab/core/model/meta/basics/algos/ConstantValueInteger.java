package genlab.core.model.meta.basics.algos;

import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;

public class ConstantValueInteger extends ConstantValue<Integer> {

	public static final InputOutput<Integer> OUTPUT = new InputOutput<Integer>(
			new IntegerFlowType(), 
			"constantvalue.integer.out", 
			"constant output", 
			"an output which is constant"
			);

	public ConstantValueInteger() {
		super(new IntegerFlowType(), OUTPUT);
	}


}
