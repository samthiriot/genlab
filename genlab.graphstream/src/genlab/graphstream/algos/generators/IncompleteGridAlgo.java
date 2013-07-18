package genlab.graphstream.algos.generators;

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
			"number of rows and columns"
	);
		
	public IncompleteGridAlgo() {
		super(
				ALGO_NAME,
				"Generator for square grids of any size.",
				ExistingAlgoCategories.GENERATORS_GRAPHS.getTotalId()
				);
		
		inputs.add(PARAM_X);
	}
		

	@Override
	public BaseGenerator getBaseGeneratorForExec(
			AbstractGraphStreamGeneratorExec exec) {
		return new GridGenerator();
	}


	@Override
	public int getIterationsForExec(AbstractGraphStreamGeneratorExec exec) {
		return (Integer)exec.getInputValueForInput(IncompleteGridAlgo.PARAM_X);

	}

	


}
