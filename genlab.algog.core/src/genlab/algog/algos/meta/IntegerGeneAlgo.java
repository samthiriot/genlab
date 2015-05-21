package genlab.algog.algos.meta;

import genlab.algog.algos.instance.GeneInstance;
import genlab.algog.algos.instance.IntegerGeneInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.parameters.IntParameter;

/**
 * TODO nice visual in graphiti, and a plugable visual interface
 * 
 * @author Samuel Thiriot
 * 
 *
 */
public class IntegerGeneAlgo extends AbstractGeneAlgo {

	
	public static final InputOutput<Integer> OUTPUT_VALUE = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON, 
			"out_gene_int", 
			"integer",
			"a gene encoding an integer"
			);

	public static final IntParameter PARAM_MINIMUM = new IntParameter(
			"param_min", 
			"minimim value", 
			"the minimal value", 
			new Integer(0)
			);
	
	public static final IntParameter PARAM_MAXIMUM = new IntParameter(
			"param_max", 
			"maximum value", 
			"the maximal value", 
			new Integer(65535)
			);
	
	
	public IntegerGeneAlgo() {
		super(
				"integer gene", 
				"gene which encodes an integer"
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
		return new IntegerGeneInstance(this, workflow);
	}


	@Override
	public IAlgoInstance createInstance(String id,
			IGenlabWorkflowInstance workflow) {
		return new IntegerGeneInstance(this, workflow, id);
		
	}

}
