package genlab.graphstream.algos.generators;

import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;
import genlab.core.parameters.BooleanParameter;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.GridGenerator;

/**
 * TODO add parameters
 * @author Samuel Thiriot
 *
 */
public class GridAlgo extends GraphStreamGeneratorAlgo {

	public static final String ALGO_NAME = "Grid generator (graphstream)";

	public static final InputOutput<Integer> PARAM_X = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON,
			"X", 
			"X", 
			"number of rows and columns",
			12
	);
	
	
	public static final BooleanParameter PARAM_TORUS = new BooleanParameter(
			"torus", "torus", "should the grid be a torus", Boolean.FALSE);
	
	public static final BooleanParameter PARAM_DIAGS = new BooleanParameter(
			"diagonals", "diagonals", "should create diagonals", Boolean.FALSE);
	
	public GridAlgo() {
		super(
				ALGO_NAME,
				"Generator for square grids of any size.",
				ExistingAlgoCategories.GENERATORS_GRAPHS
				);
		
		inputs.add(PARAM_X);
		registerParameter(PARAM_TORUS);
		registerParameter(PARAM_DIAGS);

	}
		

	@Override
	public BaseGenerator getBaseGeneratorForExec(
			AbstractGraphStreamGeneratorExec exec,
			AlgoInstance algoInstance) {
		
		final Boolean diags = (Boolean)exec.getAlgoInstance().getValueForParameter(PARAM_DIAGS.getId());
		final Boolean torus = (Boolean)exec.getAlgoInstance().getValueForParameter(PARAM_TORUS.getId());

		return new GridGenerator(diags, torus, true);
	}


	@Override
	public int getIterationsForExec(AbstractGraphStreamGeneratorExec exec) {
		return (Integer)exec.getInputValueForInput(GridAlgo.PARAM_X);

	}

	public boolean shouldCountIterations() {
		return true;
	}
	


}
