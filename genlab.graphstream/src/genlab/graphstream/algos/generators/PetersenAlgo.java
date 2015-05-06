package genlab.graphstream.algos.generators;

import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.ExistingAlgoCategories;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.PetersenGraphGenerator;

public class PetersenAlgo extends GraphStreamGeneratorAlgo {

	public static final String ALGO_NAME = "Petersen graph (graphstream)";
		
	
	
	public PetersenAlgo() {
		super(
				ALGO_NAME,
				"Generate a Petersen graph. In the mathematical field of graph theory, the Petersen graph is an undirected graph with 10 vertices and 15 edges",
				ExistingAlgoCategories.STATIC_GRAPHS
				);
		
	}
		


	@Override
	public BaseGenerator getBaseGeneratorForExec(
			AbstractGraphStreamGeneratorExec exec,
			AlgoInstance algoInstance) {
		
		return new PetersenGraphGenerator();
	}


	@Override
	public int getIterationsForExec(AbstractGraphStreamGeneratorExec exec) {
		return -1;
	}

	


}
