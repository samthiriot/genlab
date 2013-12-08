package genlab.graphstream.algos.generators;

import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.ChvatalGenerator;
import org.graphstream.algorithm.generator.FlowerSnarkGenerator;

public class FlowerSnarkAlgo extends GraphStreamGeneratorAlgo {

	public static final String ALGO_NAME = "Flower Snark graph (graphstream)";
		
	public static final InputOutput<Integer> PARAM_N = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON,
			"N", 
			"N", 
			"number of vertices in the generated graph"
	);
	
	public FlowerSnarkAlgo() {
		super(
				ALGO_NAME,
				"In the mathematical field of graph theory, the flower snarks form an infinite family of snarks introduced by Rufus Isaacs in 1975. As snarks, the flower snarks are a connected, bridgeless cubic graphs with chromatic index equal to 4. The flower snarks are non-planar and non-hamiltonian.",
				ExistingAlgoCategories.GENERATORS_GRAPHS
				);
		inputs.add(PARAM_N);
	}
		


	@Override
	public BaseGenerator getBaseGeneratorForExec(
			AbstractGraphStreamGeneratorExec exec,
			AlgoInstance algoInstance) {
		
		return new FlowerSnarkGenerator();
	}


	@Override
	public int getIterationsForExec(AbstractGraphStreamGeneratorExec exec) {
		return (Integer)exec.getInputValueForInput(FlowerSnarkAlgo.PARAM_N);
	}

	
	

}
