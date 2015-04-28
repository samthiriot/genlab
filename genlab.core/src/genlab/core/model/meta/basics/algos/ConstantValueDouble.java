package genlab.core.model.meta.basics.algos;

import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleFlowType;
import genlab.core.parameters.DoubleParameter;
import genlab.core.parameters.Parameter;

public class ConstantValueDouble extends ConstantValue<Double> {

	public static final InputOutput<Double> OUTPUT = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"constantvalue.double.out", 
			"constant output", 
			"an output which is constant"
			);
	
	public ConstantValueDouble() {
		super(
				DoubleFlowType.SINGLETON, 
				OUTPUT, 
				"constant double", 
				"a constant double value",
				buildHtmlDescription(
						"constant double", 
						"a constant double value", 
						"core",
						"Samuel Thiriot", 
						null,
						null,
						""
						)
				
				);
		
	}

	@Override
	protected Parameter<Double> createConstantParameter() {
		return new DoubleParameter(paramId, "value", "the value of this constant", 0.0);
	}

	@Override
	public Integer getPriorityForIntuitiveCreation() {
		return 2;
	}


}
