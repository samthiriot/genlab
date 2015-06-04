package genlab.igraph.algos.measure;

import genlab.core.exec.IAlgoExecutionRemotable;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;

import java.util.HashMap;
import java.util.Map;

public class IGraphClusteringExec extends AbstractIGraphMeasureExec implements IAlgoExecutionRemotable {

	public IGraphClusteringExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
	}

	/**
	 * For serialization only
	 */
	public IGraphClusteringExec() {}

	
	@Override
	protected Map<IInputOutput<?>, Object> analyzeGraph(
			IComputationProgress progress, 
			IGenlabGraph genlabGraph,
			ListOfMessages messages
			) {

		if (genlabGraph.getDirectionality() != GraphDirectionality.UNDIRECTED) {
			messages.infoUser("the global clustering assumes the graph is undirected, while the graph provided as parameter is "+genlabGraph.getDirectionality(), getClass());
		}
		
		Map<IInputOutput<?>, Object> results = new HashMap<IInputOutput<?>, Object>();
		
		// global clustering
		if (isUsed(IGraphClusteringAlgo.OUTPUT_CLUSTERING_GLOBAL) ||  exec.getExecutionForced()) {
			double clusteringGlobal = getLibrary().computeGlobalClustering(genlabGraph, exec);
			results.put(IGraphClusteringAlgo.OUTPUT_CLUSTERING_GLOBAL, clusteringGlobal);
		} else {
			messages.debugTech("the global clustering is not used, so it will not be computed", getClass());	
		}
		
		// average clustering
		if (isUsed(IGraphClusteringAlgo.OUTPUT_CLUSTERING_AVERAGE) ||  exec.getExecutionForced()) {
			double clusteringAvg = getLibrary().computeGlobalClusteringLocal(genlabGraph, exec);
			results.put(IGraphClusteringAlgo.OUTPUT_CLUSTERING_AVERAGE, clusteringAvg);
		} else {
			messages.debugTech("the average clustering is not used, so it will not be computed", getClass());	
		}
		
		return results;
	}

	@Override
	public long getTimeout() {
		return 1000*60*5; // TODO timeout with complexity
	}

}
