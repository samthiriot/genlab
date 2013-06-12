package genlab.igraph.algos.measure;

import java.util.HashMap;
import java.util.Map;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.natjna.IGraphGraph;

public class IGraphAveragePathLengthAlgo extends AbstractIGraphMeasure {

	public static final InputOutput<Double> OUTPUT_LENGTH = new InputOutput<Double>(
			new DoubleFlowType(), 
			"TODO.averagePathLength", 
			"average path length", 
			"average path length computed by the igraph library"
	);
	
	public IGraphAveragePathLengthAlgo() {
		super(
				"average path length", 
				"igraph implementation"
				);
		outputs.add(OUTPUT_LENGTH);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new AbstractIGraphMeasureExec(execution, algoInstance) {
			
			@Override
			protected Map<IInputOutput<?>, Object> analyzeGraph(
					IComputationProgress progress, 
					IGraphGraph igraphGraph,
					IGenlabGraph genlabGraph) {
				
				
				
				double res = igraphGraph.lib.computeAveragePathLength(igraphGraph);
				
				Map<IInputOutput<?>, Object> results = new HashMap<IInputOutput<?>, Object>();
				results.put(OUTPUT_LENGTH, res);
				return results;
			}
		};
	}

}
