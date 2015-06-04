package genlab.igraph.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;

import java.util.HashMap;
import java.util.Map;

public class IGraphComponentsExec extends AbstractIGraphMeasureExec {

	public IGraphComponentsExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
	}


	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected Map<IInputOutput<?>, Object> analyzeGraph(
			IComputationProgress progress, IGenlabGraph genlabGraph,
			ListOfMessages messages) {
		
		Map<IInputOutput<?>, Object> results = new HashMap<IInputOutput<?>, Object>();
		
		// is connected
		if (isUsed(IGraphComponentsAlgo.OUTPUT_ISCONNECTED)) {
			boolean isConnected = getLibrary().isConnected(genlabGraph, exec);
			results.put(IGraphComponentsAlgo.OUTPUT_ISCONNECTED, isConnected);
		} else {
			messages.debugTech("the average path length is not used, so it will not be computed", getClass());	
		}
						
		// average path length
		if (isUsed(IGraphComponentsAlgo.OUTPUT_SIZE_GIANT_CLUSTER)) {
			int giantClusterSize = getLibrary().computeGiantCluster(genlabGraph, exec);
			results.put(IGraphComponentsAlgo.OUTPUT_SIZE_GIANT_CLUSTER, giantClusterSize);
		} else {
			messages.debugTech("the size of the giant cluster is not used, so it will not be computed", getClass());	
		}
		
		return results;
	}

}
