package genlab.algog.algos.meta;

import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.BooleanFlowType;
import genlab.core.model.meta.basics.flowtypes.DoubleFlowType;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;
import genlab.core.parameters.DoubleParameter;
import genlab.core.parameters.IntParameter;

/**
 * TODO nice visual in graphiti, and a plugable visual interface
 * 
 * @author Samuel Thiriot
 * 
 *
 */
public class DoubleGeneAlgo extends AbstractGeneAlgo {

	
	public static final InputOutput<Double> OUTPUT_VALUE = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"out_gene_double", 
			"double",
			"a gene encoding a double"
			);
	
	public static final DoubleParameter PARAM_MINIMUM = new DoubleParameter(
			"param_min", 
			"minimim value", 
			"the minimal value", 
			new Double(0)
			);
	
	public static final DoubleParameter PARAM_MAXIMUM = new DoubleParameter(
			"param_max", 
			"maximum value", 
			"the maximal value", 
			new Double(65535)
			);

	
	
	public DoubleGeneAlgo() {
		super(
				"double gene", 
				"gene which encodes a double"
				);
		
		outputs.add(OUTPUT_VALUE);

		registerParameter(PARAM_MINIMUM);
		registerParameter(PARAM_MAXIMUM);
		
		
	}


	
}
