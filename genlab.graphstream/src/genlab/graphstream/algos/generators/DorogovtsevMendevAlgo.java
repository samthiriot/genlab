package genlab.graphstream.algos.generators;

import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;

public class DorogovtsevMendevAlgo extends GraphStreamGeneratorAlgo {

	public static final String ALGO_NAME = "Dorogovtsev-Mendes generator (graphstream)";

	public static final InputOutput<Integer> PARAM_N = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON,
			"N", 
			"N", 
			"number of vertices",
			200
	);
		
	public DorogovtsevMendevAlgo() {
		super(
				ALGO_NAME,
				"Generates a graph using the Dorogovtsev-Mendes algorithm. This starts by creating three nodes and tree edges, making a triangle, and then add one node at a time. Each time a node is added, an edge is chosen randomly and the node is connected to the two extremities of this edge.",
				ExistingAlgoCategories.GENERATORS_GRAPHS
				);
		
		inputs.add(PARAM_N);
	}
		



	@Override
	public BaseGenerator getBaseGeneratorForExec(
			AbstractGraphStreamGeneratorExec exec,
			AlgoInstance algoInstance) {
		return new DorogovtsevMendesGenerator();
	}


	@Override
	public int getIterationsForExec(AbstractGraphStreamGeneratorExec exec) {
		return (Integer)exec.getInputValueForInput(DorogovtsevMendevAlgo.PARAM_N);

	}

	


}
