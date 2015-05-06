package genlab.igraph.algos.measure;

import genlab.core.exec.IAlgoExecutionRemotable;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.natjna.IGraphGraph;

import java.util.HashMap;
import java.util.Map;

public class IGraphAveragePathLengthExec extends AbstractIGraphMeasureExec implements IAlgoExecutionRemotable {

	public IGraphAveragePathLengthExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
	}

	/**
	 * for serialization only
	 */
	public IGraphAveragePathLengthExec() {}
	
	@Override
	protected Map<IInputOutput<?>, Object> analyzeGraph(
			IComputationProgress progress, 
			IGraphGraph igraphGraph,
			IGenlabGraph genlabGraph,
			ListOfMessages messages
			) {
		
		Map<IInputOutput<?>, Object> results = new HashMap<IInputOutput<?>, Object>();
		
		// average path length
		if (isUsed(IGraphAveragePathLengthAlgo.OUTPUT_AVERAGE_PATH_LENGTH) ||  exec.getExecutionForced()) {
			double averagePathLength = igraphGraph.lib.computeAveragePathLength(igraphGraph);
			results.put(IGraphAveragePathLengthAlgo.OUTPUT_AVERAGE_PATH_LENGTH, averagePathLength);
		} else {
			messages.debugUser("the average path length is not used, so it will not be computed", getClass());	
		}
		
		// diameter
		if (isUsed(IGraphAveragePathLengthAlgo.OUTPUT_DIAMETER) ||  exec.getExecutionForced()) {
			int diameter = igraphGraph.lib.computeDiameter(igraphGraph);
			results.put(IGraphAveragePathLengthAlgo.OUTPUT_DIAMETER, diameter);
		} else {
			messages.debugUser("the diameter is not used, so it will not be computed", getClass());	
		}
		
		return results;
	}

	@Override
	public long getTimeout() {
		return 1000*60*5; // TODO timeout with complexity
	}


}
