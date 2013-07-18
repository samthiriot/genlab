package genlab.graphstream.algos.generators;

import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;

import org.graphstream.algorithm.generator.BananaTreeGenerator;
import org.graphstream.algorithm.generator.BaseGenerator;

public class BananaTreeAlgo extends GraphStreamGeneratorAlgo {

	public static final String ALGO_NAME = "Banana Tree generator (graphstream)";
			
	public static final InputOutput<Integer> PARAM_N = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON,
			"N", 
			"N", 
			"number of vertices in the generated graph"
	);
	
	public static final InputOutput<Integer> PARAM_K = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON,
			"K", 
			"K", 
			"start size"
	);
	
		
	public BananaTreeAlgo() {
		super(
				ALGO_NAME,
				"Banana tree generator. A (n,k)-banana tree is composed of a root node and n k-stars with one leaf of each star connected to the root node."				
				);
		
		inputs.add(PARAM_N);
		inputs.add(PARAM_K);		

	}
		



	@Override
	public BaseGenerator getBaseGeneratorForExec(
			AbstractGraphStreamGeneratorExec exec) {
		 
		
		final Integer k = (Integer)exec.getInputValueForInput(BananaTreeAlgo.PARAM_K);
		
		return new BananaTreeGenerator(k);
		
	}

	@Override
	public int getIterationsForExec(AbstractGraphStreamGeneratorExec exec) {
		return (Integer)exec.getInputValueForInput(BananaTreeAlgo.PARAM_N);
	}

	


}
