package genlab.core.model.meta.basics.algos;

import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.BooleanFlowType;
import genlab.core.model.meta.basics.flowtypes.DoubleFlowType;
import genlab.core.parameters.BooleanParameter;
import genlab.core.parameters.DoubleParameter;
import genlab.core.parameters.Parameter;

public class ConstantValueBoolean extends ConstantValue<Boolean> {

	public static final InputOutput<Boolean> OUTPUT = new InputOutput<Boolean>(
			BooleanFlowType.SINGLETON, 
			"constantvalue.boolean.out", 
			"constant output", 
			"an output which is constant"
			);
	
	public ConstantValueBoolean() {
		super(
				BooleanFlowType.SINGLETON, 
				OUTPUT, 
				"constant Boolean", 
				"a constant Boolean value",
				buildHtmlDescription(
						"constant Boolean", 
						"a constant Boolean value", 
						"core",
						"Samuel Thiriot", 
						null,
						null,
						""
						)
				
				);
		
	}

	@Override
	protected Parameter<Boolean> createConstantParameter() {
		return new BooleanParameter(paramId, "value", "the value of this constant", true);
	}

	@Override
	public Integer getPriorityForIntuitiveCreation() {
		return 1;
	}


}
