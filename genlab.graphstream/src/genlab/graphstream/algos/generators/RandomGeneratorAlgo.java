package genlab.graphstream.algos.generators;

import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.RandomGenerator;

public class RandomGeneratorAlgo extends GraphStreamGeneratorAlgo {

	public static final InputOutput<Integer> PARAM_N = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON,
			"N", 
			"N", 
			"number of vertices in the generated graph"
	);
	
	public final static DoubleInOut INPUT_k = new DoubleInOut(
			"in_k", 
			"k", 
			"average degree in the generated graph"
			);
	
	public RandomGeneratorAlgo() {
		super(
				"random G(k,N) (graphstream)", 
				"This generator creates random graphs of any size n with given average degree k and binomial degree distribution B(n, k / (n - 1)). After n - k steps we obtain a Erdős–Rényí random graph G(n, p) with p = k / (n - 1). In other words the result is the same as if we started with n isolated nodes and connected each pair of them with probability p."
				);
		inputs.add(PARAM_N);
		inputs.add(INPUT_k);
	}

	@Override
	protected BaseGenerator getBaseGeneratorForExec(
			AbstractGraphStreamGeneratorExec exec, AlgoInstance algoInstance) {
	
		Boolean directed = (Boolean)algoInstance.getValueForParameter(GraphStreamGeneratorAlgo.PARAM_DIRECTED);
		
		Double k = (Double)exec.getInputValueForInput(INPUT_k);
		
		RandomGenerator gen = new RandomGenerator(k, true, directed);
		gen.setDirectedEdges(directed, directed);
		return gen;
	}

	@Override
	public int getIterationsForExec(AbstractGraphStreamGeneratorExec exec) {

		Integer n = (Integer)exec.getInputValueForInput(PARAM_N);
		
		return n;
	}

	@Override
	public boolean shouldCountIterations() {
		return false;
	}
	
	

}
