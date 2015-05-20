package genlab.algog.algos.meta;

import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.BooleanFlowType;

/**
 * TODO nice visual in graphiti, and a plugable visual interface
 * 
 * @author Samuel Thiriot
 * 
 *
 */
public class BooleanGeneAlgo extends AbstractGeneAlgo {

	
	public static final InputOutput<Boolean> OUTPUT_VALUE = new InputOutput<Boolean>(
			BooleanFlowType.SINGLETON, 
			"out_gene_bool", 
			"bool",
			"a gene encoding a boolean"
			);

	
	
	public BooleanGeneAlgo() {
		super(
				"boolean gene", 
				"gene which encodes a boolean"
				);
		
		outputs.add(OUTPUT_VALUE);
		
	}


	@Override
	public InputOutput<?> getMainOutput() {
		return OUTPUT_VALUE;
	}

	
}
