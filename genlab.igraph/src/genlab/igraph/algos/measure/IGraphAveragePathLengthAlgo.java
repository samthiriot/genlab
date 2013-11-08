package genlab.igraph.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleFlowType;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.natjna.IGraphGraph;

import java.util.HashMap;
import java.util.Map;

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
				"igraph implementation"
				);
		outputs.add(OUTPUT_AVERAGE_PATH_LENGTH);
		outputs.add(OUTPUT_DIAMETER);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new AbstractIGraphMeasureExec(execution, algoInstance) {
			
			@Override
			protected Map<IInputOutput<?>, Object> analyzeGraph(
					IComputationProgress progress, 
					IGraphGraph igraphGraph,
					IGenlabGraph genlabGraph,
					ListOfMessages messages
					) {
				
				Map<IInputOutput<?>, Object> results = new HashMap<IInputOutput<?>, Object>();
				
				// average path length
				if (isUsed(OUTPUT_AVERAGE_PATH_LENGTH) ||  exec.getExecutionForced()) {
					double averagePathLength = igraphGraph.lib.computeAveragePathLength(igraphGraph);
					results.put(OUTPUT_AVERAGE_PATH_LENGTH, averagePathLength);
				} else {
					messages.debugUser("the average path length is not used, so it will not be computed", getClass());	
				}
				
				// diameter
				if (isUsed(OUTPUT_DIAMETER) ||  exec.getExecutionForced()) {
					int diameter = igraphGraph.lib.computeDiameter(igraphGraph);
					results.put(OUTPUT_DIAMETER, diameter);
				} else {
					messages.debugUser("the diameter is not used, so it will not be computed", getClass());	
				}
				
				return results;
			}

			@Override
			public long getTimeout() {
				return 1000*60*5; // TODO timeout with complexity
			}
		};
	}

}
