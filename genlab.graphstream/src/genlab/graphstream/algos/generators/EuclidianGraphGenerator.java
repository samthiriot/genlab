package genlab.graphstream.algos.generators;

import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;
import genlab.core.parameters.BooleanParameter;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.RandomEuclideanGenerator;

public class EuclidianGraphGenerator extends GraphStreamGeneratorAlgo {

		
	public static final InputOutput<Integer> INPUT_N = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON,
			"in_N", 
			"N", 
			"number of vertices in the generated graph",
			200
	);
	
	public static final DoubleInOut INPUT_THRESHOLD = new DoubleInOut(
			"in_threshold", 
			"threshold", 
			"if the euclidian distance is lower than this threshold, a link will be created",
			0.1
			);
	
	public EuclidianGraphGenerator() {
		super(
				"euclidian generator (graphstream)", 
				"This generator creates random graphs of any size. Links of such graphs are created according to a threshold. If the Euclidean distance between two nodes is less than a given threshold, then a link is created between those 2 nodes."
				);
		
		inputs.add(INPUT_N);
		inputs.add(INPUT_THRESHOLD);
	}

	

	@Override
	protected BaseGenerator getBaseGeneratorForExec(
			AbstractGraphStreamGeneratorExec exec, AlgoInstance algoInstance) {

		Boolean directed = (Boolean)algoInstance.getValueForParameter(GraphStreamGeneratorAlgo.PARAM_DIRECTED);
		
		RandomEuclideanGenerator gen = new RandomEuclideanGenerator(2, directed, directed);
		
		Double threshold = (Double)exec.getInputValueForInput(INPUT_THRESHOLD);
		
		gen.setThreshold(threshold);
		
		return gen;
	}

	@Override
	public int getIterationsForExec(AbstractGraphStreamGeneratorExec exec) {
		return (Integer) exec.getInputValueForInput(INPUT_N);
	}

}
