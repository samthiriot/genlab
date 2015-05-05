package genlab.graphstream.algos.measure;

import genlab.core.exec.IAlgoExecutionRemotable;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;

import java.util.HashMap;
import java.util.Map;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;

public class GraphStreamAverageClusteringExec extends AbstractGraphstreamMeasureExecution implements IAlgoExecutionRemotable {

	private final IExecution execution;

	public GraphStreamAverageClusteringExec(IExecution exec,
			IAlgoInstance algoInst, IExecution execution) {
		super(exec, algoInst);
		this.execution = execution;
	}

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
		
		if (isUsed(GraphStreamAverageClustering.OUTPUT_AVERAGE_CLUSTERING) || !execution.getExecutionForced()) {
			double averageClustering = Toolkit.averageClusteringCoefficient(gsGraph);
			results.put(GraphStreamAverageClustering.OUTPUT_AVERAGE_CLUSTERING, averageClustering);
		}

		return results;
	}

	@Override
	public long getTimeout() {
		return 1000*60*5; // TODO
	}
}