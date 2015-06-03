package genlab.igraph.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.commons.IGraph2GenLabConvertor;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.natjna.IGraphNativeLibrary;

import java.util.HashMap;
import java.util.Map;

final class IGraphNodeBetweenessExec extends AbstractIGraphMeasureExec {
	private final AlgoInstance algoInstance;

	IGraphNodeBetweenessExec(IExecution exec, IAlgoInstance algoInst,
			AlgoInstance algoInstance) {
		super(exec, algoInst);
		this.algoInstance = algoInstance;
	}

	@Override
	protected Map<IInputOutput<?>, Object> analyzeGraph(
			IComputationProgress progress, 
			IGenlabGraph genlabGraph,
			ListOfMessages messages
			) {
		
		Map<IInputOutput<?>, Object> results = new HashMap<IInputOutput<?>, Object>();

		final String parameterAttribute = (String) algoInstance.getValueForParameter(IGraphNodeBetweenessAlgo.PARAM_ATTRIBUTE_NAME);
			
		// is connected
		if (isUsed(IGraphNodeBetweenessAlgo.OUTPUT_GRAPH)) {
			
			double[] nodesBetweennes = getLibrary().computeNodeBetweeness(
					genlabGraph, 
					genlabGraph.getDirectionality() != GraphDirectionality.UNDIRECTED,
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