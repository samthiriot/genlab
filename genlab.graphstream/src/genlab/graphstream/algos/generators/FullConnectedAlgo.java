package genlab.graphstream.algos.generators;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.algorithm.generator.FullGenerator;

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
			"number of vertices"
	);
		
	public FullConnectedAlgo() {
		super(
				ALGO_NAME,
				"This generator creates fully connected graphs of any size.",
				ExistingAlgoCategories.GENERATORS_GRAPHS.getTotalId()
				);
		
		inputs.add(PARAM_N);
	}
		

	@Override
	public BaseGenerator getBaseGeneratorForExec(
			AbstractGraphStreamGeneratorExec exec) {
		return new FullGenerator();
	}


	@Override
	public int getIterationsForExec(AbstractGraphStreamGeneratorExec exec) {
		return (Integer)exec.getInputValueForInput(FullConnectedAlgo.PARAM_N);

	}

	


}
