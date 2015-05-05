package genlab.graphstream.algos.measure;

import genlab.core.exec.IAlgoExecutionRemotable;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class GraphStreamConnectedComponentsExec extends AbstractGraphstreamMeasureExecution implements IAlgoExecutionRemotable{
	
	public GraphStreamConnectedComponentsExec() {}
	
	public GraphStreamConnectedComponentsExec(IExecution exec,
			IAlgoInstance algoInst) {
		super(exec, algoInst);
	}

	@Override
	protected Map<IInputOutput<?>, Object> analyzeGraph(
			IComputationProgress progress, 
			Graph gsGraph,
			IGenlabGraph genlabGraph,
			ListOfMessages messages
			) {
		
		
		// parameters
		final String graphAttributeNumberComponents = "number of components";
		final String vertexAttributeBelongGiantComponent = "belongs_giant_component";
		final String vertexAttributeComponentId= "component_id";

		// final results
		final Map<IInputOutput<?>, Object> results = new HashMap<IInputOutput<?>, Object>();
		
		// output graph is... ?
		IGenlabGraph outputGraph = null;
		if (!isUsed(GraphStreamConnectedComponents.OUTPUT_GRAPH)) {
			// we will not create an output graph
			
		} else {
			// someone is using this graph !
			
			if (
					vertexAttributeBelongGiantComponent == null 
					&& 
					vertexAttributeComponentId == null
					&& 
					graphAttributeNumberComponents == null
					) {
				// nobody asks for a graph attribute; let's return the same graph as the one provided as input !
				outputGraph = genlabGraph;
			} else {
				// someone is interested in some graph attributes; 
				// we have to clone this graph
				outputGraph = genlabGraph.clone("cloned"); // TODO id ?
			}
			
		}
		if (outputGraph != null)
			results.put(GraphStreamConnectedComponents.OUTPUT_GRAPH, outputGraph);
		
		// create and run the graphstream algorithm
		ConnectedComponents cc = new ConnectedComponents();				
		cc.init(gsGraph);
		if (vertexAttributeComponentId != null) {
			// the algo will add the info into the gsGraph
			cc.setCountAttribute(vertexAttributeComponentId);
		}
		cc.compute();
		
		// process its results
		
		// ... count of connected components
		int count = cc.getConnectedComponentsCount();
		// (as an output)
		results.put(GraphStreamConnectedComponents.OUTPUT_COUNT, new Integer(count));
		results.put(GraphStreamConnectedComponents.OUTPUT_CONNECTED, new Boolean(count == 1));
		// (and as a graph attribute)
		if (graphAttributeNumberComponents != null && outputGraph != null) {
			outputGraph.declareGraphAttribute(graphAttributeNumberComponents, Integer.class);
		}
		
		// ... giant component
		final List<Node> nodesInGiantComponent = cc.getGiantComponent();
		results.put(GraphStreamConnectedComponents.OUTPUT_GIANT_COMPONENT_SIZE, nodesInGiantComponent.size());
		// TODO add as a graph attribute ? 
		// (if asked, add an attribute for nodes and define its value)
		if (vertexAttributeBelongGiantComponent != null && outputGraph != null) {
			outputGraph.declareVertexAttribute(vertexAttributeBelongGiantComponent, Boolean.class);
			Set<Node> nodesInGiantComponentSet = new HashSet<Node>(nodesInGiantComponent);
			for (Node n : gsGraph.getNodeSet()) {
				outputGraph.setVertexAttribute(
						n.getId(), 
						vertexAttributeBelongGiantComponent, 
						nodesInGiantComponentSet.contains(n)
						);
			}
		}
		
		// ... id of each component
		if (vertexAttributeComponentId != null && outputGraph != null) {
			outputGraph.declareVertexAttribute(vertexAttributeComponentId, Integer.class);
			for (Node n : gsGraph.getNodeSet()) {
				outputGraph.setVertexAttribute(
						n.getId(), 
						vertexAttributeComponentId, 
						n.getAttribute(vertexAttributeComponentId)
						);
			}
		}
		
		// TODO distribution of size ???
		
		// clean graphstream algo internal data
		cc.terminate();
		
		return results;
	}

	@Override
	public long getTimeout() {
		return 1000*60*10;
	}
}