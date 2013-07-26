package genlab.graphstream.algos.generators;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;

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

public class DorogovtsevMendevAlgo extends GraphStreamGeneratorAlgo {

	public static final String ALGO_NAME = "Dorogovtsev-Mendes generator (graphstream)";

	public static final InputOutput<Integer> PARAM_N = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON,
			"N", 
			"N", 
			"number of vertices"
	);
		
	public DorogovtsevMendevAlgo() {
		super(
				ALGO_NAME,
				"Generates a graph using the Dorogovtsev-Mendes algorithm. This starts by creating three nodes and tree edges, making a triangle, and then add one node at a time. Each time a node is added, an edge is chosen randomly and the node is connected to the two extremities of this edge.",
				ExistingAlgoCategories.GENERATORS_GRAPHS.getTotalId()
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
