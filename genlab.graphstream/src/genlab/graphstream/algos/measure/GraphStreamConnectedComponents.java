package genlab.graphstream.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
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

public class GraphStreamConnectedComponents extends AbstractGraphStreamMeasure {


	public static final InputOutput<Integer> OUTPUT_COUNT = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON, 
			"out_count", 
			"count", 
			"the number of components found in the graph"
	);
	public static final InputOutput<Integer> OUTPUT_GIANT_COMPONENT_SIZE = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON, 
			"out_giantComponentSize", 
			"giant component size", 
			"the number of vertices found in the giant component"
	);
	public static final InputOutput<IGenlabGraph> OUTPUT_GRAPH = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"out_graph", 
			"graph", 
			"the graph analyzed, with some data added as attributes"
	);
	
	
	public GraphStreamConnectedComponents() {
		super(
				"connected components", 
				"detects connected components (provided by graphstream)"
				);
		
		outputs.add(OUTPUT_COUNT);
		outputs.add(OUTPUT_GRAPH);
		outputs.add(OUTPUT_GIANT_COMPONENT_SIZE);
		
	}

	@Override
	public IAlgoExecution createExec(
			IExecution execution,
			AlgoInstance algoInstance) {
		
		return new AbstractGraphstreamMeasureExecution(execution, algoInstance) {
			
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
				if (!isUsed(OUTPUT_GRAPH)) {
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
					results.put(OUTPUT_GRAPH, outputGraph);
				
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
				System.err.println("components count: "+count);
				// (as an output)
				results.put(OUTPUT_COUNT, new Integer(count));
				// (and as a graph attribute)
				if (graphAttributeNumberComponents != null) {
					outputGraph.declareGraphAttribute(graphAttributeNumberComponents, Integer.class);
				}
				
				// ... giant component
				final List<Node> nodesInGiantComponent = cc.getGiantComponent();
				results.put(OUTPUT_GIANT_COMPONENT_SIZE, nodesInGiantComponent.size());
				// TODO add as a graph attribute ? 
				// (if asked, add an attribute for nodes and define its value)
				if (vertexAttributeBelongGiantComponent != null) {
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
				if (vertexAttributeComponentId != null) {
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
		};
	}

}
