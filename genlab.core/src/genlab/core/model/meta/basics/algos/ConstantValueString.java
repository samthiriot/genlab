package genlab.core.model.meta.basics.algos;

import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleFlowType;
import genlab.core.model.meta.basics.flowtypes.StringFlowType;
import genlab.core.parameters.DoubleParameter;
import genlab.core.parameters.Parameter;
import genlab.core.parameters.StringParameter;

public class ConstantValueString extends ConstantValue<String> {

	public static final InputOutput<String> OUTPUT = new InputOutput<String>(
			StringFlowType.SINGLETON, 
			"constantvalue.string.out", 
			"constant output", 
			"an output which is constant"
			);
	
	public ConstantValueString() {
		super(
				StringFlowType.SINGLETON, 
				OUTPUT, 
				"constant string", 
				"a constant string value",
				buildHtmlDescription(
						"constant string", 
						"a constant string value", 
						"core",
						"Samuel Thiriot", 
						null,
						null,
						""
						)
				
				);
		
	}

	@Override
	protected Parameter<String> createConstantParameter() {
		return new StringParameter(paramId, "value", "the value of this constant", "type in here");
	}

	@Override
	public Integer getPriorityForIntuitiveCreation() {
		return 1;
	}


}
