package genlab.graphstream.algos.generators;

import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.algorithm.generator.BaseGenerator;

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

public class BarabasiAlbertAlgo extends GraphStreamGeneratorAlgo {

	public static final String ALGO_NAME = "Barabasi-Albert generator (graphstream)";
			
	public static final InputOutput<Integer> PARAM_M = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON,
			"m", 
			"m", 
			"max links per step",
			1
	);
	
	public static final InputOutput<Integer> PARAM_N = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON,
			"N", 
			"N", 
			"number of vertices",
			200
	);
	
	public BarabasiAlbertAlgo() {
		super(
				ALGO_NAME,
				" Scale-free graph generator using the preferential attachment rule as defined in the Barabási-Albert model.",
				ExistingAlgoCategories.GENERATORS_GRAPHS
				);
		
		inputs.add(PARAM_N);
		inputs.add(PARAM_M);
	}
		



	@Override
	public BaseGenerator getBaseGeneratorForExec(
			AbstractGraphStreamGeneratorExec exec,
			AlgoInstance algoInstance) {
		
		final Integer m = (Integer)exec.getInputValueForInput(PARAM_M);

		return new BarabasiAlbertGenerator(m);
	}


	@Override
	public int getIterationsForExec(AbstractGraphStreamGeneratorExec exec) {
		return (Integer)exec.getInputValueForInput(BarabasiAlbertAlgo.PARAM_N);
	}

	


}
