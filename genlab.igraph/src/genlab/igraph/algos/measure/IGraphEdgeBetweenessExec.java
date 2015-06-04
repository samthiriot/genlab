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

import java.util.HashMap;
import java.util.Map;

final class IGraphEdgeBetweenessExec extends
		AbstractIGraphMeasureExec {
	private final AlgoInstance algoInstance;

	IGraphEdgeBetweenessExec(IExecution exec,
			IAlgoInstance algoInst, AlgoInstance algoInstance) {
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
		 

		String parameterAttribute = (String) algoInstance.getValueForParameter(IGraphEdgeBetweenessAlgo.PARAM_ATTRIBUTE_NAME);
		
		try {
			
			// is connected
			if (isUsed(IGraphEdgeBetweenessAlgo.OUTPUT_GRAPH)) {
				
				double[] edgeBetweennes = getLibrary().computeEdgeBetweeness(
						genlabGraph, 
						genlabGraph.getDirectionality() != GraphDirectionality.UNDIRECTED, 
						exec
						);
				
				IGenlabGraph output = genlabGraph.clone("cloned");
		
				IGraph2GenLabConvertor.addAttributesToEdgesGenlabGraphFromIgraph(
						output,
						parameterAttribute,
						edgeBetweennes
						);
				
				
				results.put(IGraphEdgeBetweenessAlgo.OUTPUT_GRAPH, output);
				
			} else {
				messages.infoUser("the betwenness of the graph is not used, so it will not be computed", getClass());	
			}
			
			return results;
			
		} finally {
		
		}
	}

	@Override
	public long getTimeout() {
		// TODO interesting timeout given the complexity
		return 1000*60*5;
	}
}