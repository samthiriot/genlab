package genlab.graphstream.algos.generators;

import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleFlowType;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.WattsStrogatzGenerator;

public class WattsStrogatzAlgo extends GraphStreamGeneratorAlgo {

	public static final String ALGO_NAME = "Watts-Strogatz beta generator (graphstream)";
			
	public static final InputOutput<Integer> INPUT_N = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON,
			"N", 
			"N", 
			"number of vertices in the generated graph",
			200
	);
	
	public static final InputOutput<Integer> INPUT_K = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON,
			"K", 
			"K", 
			"neighboors (should be even)",
			4
	);
	public static final InputOutput<Double> INPUT_P =  new InputOutput<Double>(
			DoubleFlowType.SINGLETON,
			"p", 
			"p", 
			"rewiring probability",
			0.05
	);

	public WattsStrogatzAlgo() {
		super(
				ALGO_NAME,
				"This generator creates small-world graphs of arbitrary size. This generator is based on the Watts-Strogatz model."
				);
		
		inputs.add(INPUT_N);
		inputs.add(INPUT_K);
		inputs.add(INPUT_P);
	}

	@Override
	public BaseGenerator getBaseGeneratorForExec(
			AbstractGraphStreamGeneratorExec exec,
			AlgoInstance algoInstance) {
		
		final Integer size = (Integer)exec.getInputValueForInput(WattsStrogatzAlgo.INPUT_N);
		final Integer nei = (Integer)exec.getInputValueForInput(WattsStrogatzAlgo.INPUT_K);
		final Double p = (Double)exec.getInputValueForInput(WattsStrogatzAlgo.INPUT_P);
			
		return new WattsStrogatzGenerator(size, nei, p);
		
	}

	@Override
	public int getIterationsForExec(AbstractGraphStreamGeneratorExec exec) {
		return -1;
	}
		
	


}
