package genlab.graphstream.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;

import java.util.HashMap;
import java.util.Map;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;

/**
 * The APSP graphstream algorithm implements the Floyd-Warshall all pair shortest path algorithm.
 * 
 * @see http://graphstream-project.org/api/gs-algo/org/graphstream/algorithm/APSP.html
 * @see http://en.wikipedia.org/wiki/Average_path_length
 * 
 * @author Samuel Thiriot
 *
 */
public class GraphStreamAverageClustering extends AbstractGraphStreamMeasure {


	public static final InputOutput<Double> OUTPUT_AVERAGE_CLUSTERING = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"out_average_clustering", 
			"average clustering", 
			"the average clustering rate in the graph"
	);
	
	
	public GraphStreamAverageClustering() {
		super(
				"clustering (graphstream)", 
				"computes the clustering rate of a graph"
				);
		
		outputs.add(OUTPUT_AVERAGE_CLUSTERING);
		
	}

	@Override
	public IAlgoExecution createExec(
			final IExecution execution,
			final AlgoInstance algoInstance) {
		
		return new AbstractGraphstreamMeasureExecution(execution, algoInstance) {
						

			@Override
			protected Map<IInputOutput<?>, Object> analyzeGraph(
					final IComputationProgress progress, 
					final Graph gsGraph,
					IGenlabGraph genlabGraph,
					ListOfMessages messages
					) {
					
				progress.setProgressTotal(3);
				
				// final results
				final Map<IInputOutput<?>, Object> results = new HashMap<IInputOutput<?>, Object>();
				
				progress.setProgressMade(1);
				
				if (isUsed(OUTPUT_AVERAGE_CLUSTERING) || !execution.getExecutionForced()) {
					double averageClustering = Toolkit.averageClusteringCoefficient(gsGraph);
					results.put(OUTPUT_AVERAGE_CLUSTERING, averageClustering);
				}
	
				return results;
			}

			@Override
			public long getTimeout() {
				return 1000*60*5; // TODO
			}

		};
	}

}
