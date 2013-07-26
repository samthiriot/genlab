package genlab.graphstream.algos.generators;

import javax.swing.text.StyledEditorKit.BoldAction;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.algorithm.generator.FullGenerator;
import org.graphstream.algorithm.generator.GridGenerator;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.IGenlabWorkflow;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleFlowType;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.parameters.BooleanParameter;

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
			"number of rows and columns"
	);
	
	
	public static final BooleanParameter PARAM_TORUS = new BooleanParameter(
			"torus", "torus", "should the grid be a torus", Boolean.FALSE);
	
	public static final BooleanParameter PARAM_DIAGS = new BooleanParameter(
			"diagonals", "diagonals", "should create diagonals", Boolean.FALSE);
	
	public GridAlgo() {
		super(
				ALGO_NAME,
				"Generator for square grids of any size.",
				ExistingAlgoCategories.GENERATORS_GRAPHS.getTotalId()
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
