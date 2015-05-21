package genlab.algog.algos.meta;

import genlab.algog.algos.instance.DoubleGeneInstance;
import genlab.algog.algos.instance.GeneInstance;
import genlab.algog.algos.instance.IntegerGeneInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleFlowType;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.parameters.DoubleParameter;

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


	@Override
	public InputOutput<?> getMainOutput() {
		return OUTPUT_VALUE;
	}

	
	@Override
	public IAlgoInstance createInstance(IGenlabWorkflowInstance workflow) {
		return new DoubleGeneInstance(this, workflow);
	}


	@Override
	public IAlgoInstance createInstance(String id,
			IGenlabWorkflowInstance workflow) {
		return new DoubleGeneInstance(this, workflow, id);
		
	}
	
}
