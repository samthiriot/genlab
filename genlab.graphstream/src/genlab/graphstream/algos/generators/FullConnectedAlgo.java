package genlab.graphstream.algos.generators;

import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.FullGenerator;

/**
 * TODO add parameters
 * @author Samuel Thiriot
 *
 */
public class FullConnectedAlgo extends GraphStreamGeneratorAlgo {

	public static final String ALGO_NAME = "Full graph generator (graphstream)";

	public static final InputOutput<Integer> PARAM_N = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON,
			"N", 
			"N", 
			"number of vertices",
			200
	);
		
	public FullConnectedAlgo() {
		super(
				ALGO_NAME,
				"This generator creates fully connected graphs of any size.",
				ExistingAlgoCategories.GENERATORS_GRAPHS
				);
		
		inputs.add(PARAM_N);
	}
		

	@Override
	public BaseGenerator getBaseGeneratorForExec(
			AbstractGraphStreamGeneratorExec exec,
			AlgoInstance algoInstance) {
		return new FullGenerator();
	}


	@Override
	public int getIterationsForExec(AbstractGraphStreamGeneratorExec exec) {
		return (Integer)exec.getInputValueForInput(FullConnectedAlgo.PARAM_N);

	}

	


}
