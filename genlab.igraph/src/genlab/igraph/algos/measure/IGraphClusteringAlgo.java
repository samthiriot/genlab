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
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.natjna.IGraphGraph;

public class IGraphClusteringAlgo extends AbstractIGraphMeasure {

	public static final InputOutput<Double> OUTPUT_CLUSTERING_GLOBAL = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"global_clustering", 
			"global clustering", 
			"global clustering of the graph."
	);
	
	public static final InputOutput<Double> OUTPUT_CLUSTERING_AVERAGE = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"average_clustering", 
			"average clustering", 
			"average clustering of the graph."
	);
	
	
	public IGraphClusteringAlgo() {
		super(
				"average clustering (igraph)", 
				"computes the average clustering (or transitivity) of this graph. The transitivity measures the probability that two neighbors of a vertex are connected.",
				null
				);
		outputs.add(OUTPUT_CLUSTERING_GLOBAL);
		outputs.add(OUTPUT_CLUSTERING_AVERAGE);

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
				
				
				if (genlabGraph.getDirectionality() != GraphDirectionality.UNDIRECTED) {
					messages.warnUser("the global clustering assumes the graph is undirected, while the graph provided as parameter is "+genlabGraph.getDirectionality(), getClass());
				}
				
				Map<IInputOutput<?>, Object> results = new HashMap<IInputOutput<?>, Object>();
				
				// global clustering
				if (isUsed(OUTPUT_CLUSTERING_GLOBAL) ||  exec.getExecutionForced()) {
					double clusteringGlobal = igraphGraph.lib.computeGlobalClustering(igraphGraph);
					results.put(OUTPUT_CLUSTERING_GLOBAL, clusteringGlobal);
				} else {
					messages.debugTech("the global clustering is not used, so it will not be computed", getClass());	
				}
				
				// average clustering
				if (isUsed(OUTPUT_CLUSTERING_AVERAGE) ||  exec.getExecutionForced()) {
					double clusteringAvg = igraphGraph.lib.computeGlobalClusteringLocal(igraphGraph);
					results.put(OUTPUT_CLUSTERING_AVERAGE, clusteringAvg);
				} else {
					messages.debugTech("the average clustering is not used, so it will not be computed", getClass());	
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
