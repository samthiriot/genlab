package genlab.igraph.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.commons.IGraph2GenLabConvertor;

import java.util.HashMap;
import java.util.Map;

public class IGraphNodeClosenessExec extends AbstractIGraphMeasureExec {
	
	public IGraphNodeClosenessExec(IExecution exec, IAlgoInstance algoInst,
			AlgoInstance algoInstance) {
		super(exec, algoInst);
	}

	@Override
	protected Map<IInputOutput<?>, Object> analyzeGraph(
			IComputationProgress progress, 
			IGenlabGraph genlabGraph,
			ListOfMessages messages
			) {
		
		Map<IInputOutput<?>, Object> results = new HashMap<IInputOutput<?>, Object>();

		final String parameterAttribute = (String) algoInst.getValueForParameter(IGraphNodeClosenessAlgo.PARAM_ATTRIBUTE_NAME);
			
		// is connected
		if (isUsed(IGraphNodeBetweenessAlgo.OUTPUT_GRAPH)) {
			
			double[] nodesBetweennes = getLibrary().computeNodeCloseness(
					genlabGraph, 
					exec
					);
			
			IGenlabGraph output = genlabGraph.clone("cloned");
	
			IGraph2GenLabConvertor.addAttributesToNodesGenlabGraphFromIgraph(
					output,
					parameterAttribute,
					nodesBetweennes
					);
			
			
			results.put(IGraphNodeBetweenessAlgo.OUTPUT_GRAPH, output);
			
		} else {
			messages.infoUser("the betwenness of the graph is not used, so it will not be computed", getClass());	
		}
		
		return results;
		
		
	}

	@Override
	public long getTimeout() {
		// TODO interesting timeout given the complexity
		return 1000*60*5;
	}
}