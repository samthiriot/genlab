package genlab.graphstream.algos.generators;

import genlab.core.model.meta.ExistingAlgoCategories;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.ChvatalGenerator;

public class FlowerSnarkAlgo extends GraphStreamGeneratorAlgo {

	public static final String ALGO_NAME = "Flower Snark graph (graphstream)";
		
	
	
	public FlowerSnarkAlgo() {
		super(
				ALGO_NAME,
				"In the mathematical field of graph theory, the flower snarks form an infinite family of snarks introduced by Rufus Isaacs in 1975. As snarks, the flower snarks are a connected, bridgeless cubic graphs with chromatic index equal to 4. The flower snarks are non-planar and non-hamiltonian.",
				ExistingAlgoCategories.STATIC_GRAPHS.getTotalId()
				);
		
	}
		


	@Override
	public BaseGenerator getBaseGeneratorForExec(
			AbstractGraphStreamGeneratorExec exec) {
		
		return new ChvatalGenerator();
	}


	@Override
	public int getIterationsForExec(AbstractGraphStreamGeneratorExec exec) {
		return -1;
	}

	


}
