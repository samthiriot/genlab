package genlab.graphstream.algos.generators;

import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.GridGenerator;

/**
 * TODO to be implemented (not done yet)
 * see http://graphstream-project.org/api/gs-algo/org/graphstream/algorithm/generator/IncompleteGridGenerator.html
 * 
 * TODO add parameters
 * @author Samuel Thiriot
 *
 */
public class IncompleteGridAlgo extends GraphStreamGeneratorAlgo {

	public static final String ALGO_NAME = "Incomplete grid generator (graphstream)";

	public static final InputOutput<Integer> PARAM_X = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON,
			"X", 
			"X", 
			"number of rows and columns",
			12
	);
		
	public IncompleteGridAlgo() {
		super(
				ALGO_NAME,
				"Generator for square grids of any size.",
				ExistingAlgoCategories.GENERATORS_GRAPHS
				);
		
		inputs.add(PARAM_X);
	}
		

	@Override
	public BaseGenerator getBaseGeneratorForExec(
			AbstractGraphStreamGeneratorExec exec,
			AlgoInstance algoInstance) {
		return new GridGenerator();
	}


	@Override
	public int getIterationsForExec(AbstractGraphStreamGeneratorExec exec) {
		return (Integer)exec.getInputValueForInput(IncompleteGridAlgo.PARAM_X);

	}

	


}
