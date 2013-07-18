package genlab.graphstream.algos.generators;

import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleFlowType;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.WattsStrogatzGenerator;

public class WattsStrogatzAlgo extends GraphStreamGeneratorAlgo {

	public static final String ALGO_NAME = "Watts-Strogatz beta generator (graphstream)";
			
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
			"neighboors (should be even)"
	);
	public static final InputOutput<Double> PARAM_P =  new InputOutput<Double>(
			DoubleFlowType.SINGLETON,
			"p", 
			"p", 
			"rewiring probability"
		);
	
	public WattsStrogatzAlgo() {
		super(
				ALGO_NAME,
				"This generator creates small-world graphs of arbitrary size. This generator is based on the Watts-Strogatz model."
				);
		
		inputs.add(PARAM_N);
		inputs.add(PARAM_K);
		inputs.add(PARAM_P);
	}

	@Override
	public BaseGenerator getBaseGeneratorForExec(
			AbstractGraphStreamGeneratorExec exec) {
		
		final Integer size = (Integer)exec.getInputValueForInput(WattsStrogatzAlgo.PARAM_N);
		final Integer nei = (Integer)exec.getInputValueForInput(WattsStrogatzAlgo.PARAM_K);
		final Double p = (Double)exec.getInputValueForInput(WattsStrogatzAlgo.PARAM_P);
			
		return new WattsStrogatzGenerator(size, nei, p);
	}

	@Override
	public int getIterationsForExec(AbstractGraphStreamGeneratorExec exec) {
		return -1;
	}
		
	


}
