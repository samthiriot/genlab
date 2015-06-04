package genlab.cytoscape.commons;

import java.util.HashMap;
import java.util.Map;

import genlab.core.commons.ProgramException;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.GraphFactory;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import cytoscape.randomnetwork.RandomNetwork;
import cytoscape.util.intr.IntEnumerator;

public class Cytoscape2Genlab {

	public static IGenlabGraph getGenlabGraphForCytoscape(RandomNetwork cyNetwork, boolean directed) {
		
		IGenlabGraph glGraph = GraphFactory.createGraph(
				"loaded", 
				(directed?GraphDirectionality.DIRECTED:GraphDirectionality.UNDIRECTED), 
				false
				);
		
		// add vertices
		IntEnumerator itNodes = cyNetwork.nodes();
		while (itNodes.numRemaining() > 0) {
			int nodeId = itNodes.nextInt();
			glGraph.addVertex(
					Integer.toString(nodeId)
					);
		}
		
		// add edges
		IntEnumerator itEdges = cyNetwork.edges();
		while (itEdges.numRemaining()>0) {
			int edgeId = itEdges.nextInt();
			glGraph.addEdge(
					Integer.toString(edgeId),
					Integer.toString(cyNetwork.edgeSource(edgeId)),
					Integer.toString(cyNetwork.edgeTarget(edgeId)),
					cyNetwork.edgeType(edgeId)==RandomNetwork.DIRECTED_EDGE
					);
			
		}
		
		// basic check
		if (glGraph.getVerticesCount() != cyNetwork.getNumNodes())
			throw new ProgramException("error during the conversion: the number of vertices is not correct");
		if (glGraph.getEdgesCount() != cyNetwork.getNumEdges())
			throw new ProgramException("error during the conversion: the number of edgesis not correct");
		
		return glGraph;
		
	}
	
	public static RandomNetwork getCytoscapeGraphForIGraph(IGenlabGraph glGraph) {
		
		RandomNetwork cyNetwork = new RandomNetwork(glGraph.getDirectionality() == GraphDirectionality.DIRECTED);
		
		Map<String,Integer> glNodeId2cyNodeId = new HashMap<String, Integer>((int)glGraph.getVerticesCount());
		
		// copy nodes
		for (String nodeId : glGraph.getVertices()) {
			
			glNodeId2cyNodeId.put(
					nodeId,
					cyNetwork.nodeCreate()
					);
		}
		
		// copy edges
		for (String edgeId : glGraph.getEdges()) {
			
			cyNetwork.edgeCreate(
					glNodeId2cyNodeId.get(glGraph.getEdgeVertexFrom(edgeId)), 
					glNodeId2cyNodeId.get(glGraph.getEdgeVertexTo(edgeId)), 
					glGraph.isEdgeDirected(edgeId)
					);
		}
		
		
		// useless, but may facilitate gc (?)
		glNodeId2cyNodeId.clear();
		
		// basic check
		if (glGraph.getVerticesCount() != cyNetwork.getNumNodes())
			throw new ProgramException("error during the conversion: the number of vertices is not correct");
		if (glGraph.getEdgesCount() != cyNetwork.getNumEdges())
			throw new ProgramException("error during the conversion: the number of edgesis not correct");
		
		return cyNetwork;
		
	}
	
	private Cytoscape2Genlab() {
		
	}

}
