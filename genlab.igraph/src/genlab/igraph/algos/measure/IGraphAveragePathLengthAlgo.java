package genlab.igraph.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleFlowType;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;

public class IGraphAveragePathLengthAlgo extends AbstractIGraphMeasure {

	public static final InputOutput<Double> OUTPUT_AVERAGE_PATH_LENGTH = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"out_averagePathLength", 
			"average path length", 
			"average path length computed by the igraph library. " +
			"When the graph is disconnected, the average path length in each component is returned."
	);
	
	public static final InputOutput<Integer> OUTPUT_DIAMETER = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON, 
			"out_diameter", 
			"diameter", 
			"diameter computed by the igraph library. " +
			"When the graph is disconnected, the longest path of all the components is used."
	);
	
	
	public IGraphAveragePathLengthAlgo() {
		super(
				"average path length (igraph)", 
				"igraph implementation",
				null // no preference for implementation
				);
		outputs.add(OUTPUT_AVERAGE_PATH_LENGTH);
		outputs.add(OUTPUT_DIAMETER);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new IGraphAveragePathLengthExec(execution, algoInstance);
		
	}

}
