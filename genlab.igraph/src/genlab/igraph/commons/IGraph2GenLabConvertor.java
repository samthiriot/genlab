package genlab.igraph.commons;

import genlab.core.commons.ProgramException;
import genlab.core.commons.WrongParametersException;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.GraphFactory;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.UserMachineInteractionUtils;
import genlab.igraph.natjna.IGraphEdge;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.natjna.IGraphLibrary;
import genlab.igraph.natjna.IGraphRawLibraryPool;

public class IGraph2GenLabConvertor {

	public static IGenlabGraph getGenlabGraphForIgraph(IGraphGraph graph, ListOfMessages messages) {
		
		long timestampStart = System.currentTimeMillis();
		
		IGenlabGraph glGraph = GraphFactory.createGraph(
				"igraphGen", 
				graph.directed?GraphDirectionality.DIRECTED:GraphDirectionality.UNDIRECTED, 
				false
				);
		
		
		// add node attributes
		if (graph.xPositions != null) {
			
			// declare attributes
			glGraph.declareVertexAttribute("x", Double.class);
			glGraph.declareVertexAttribute("y", Double.class);
			
		}
		
		// add nodes
		final int totalNodes = graph.lib.getVertexCount(graph);
		for (int i=0; i<totalNodes; i++) {
			
			String vertexId = Integer.toString(i);

			glGraph.addVertex(vertexId);
			
			// add node attributes
			if (graph.xPositions != null) {
				glGraph.setVertexAttribute(
						vertexId, 
						"x", graph.xPositions[i]
						);
				glGraph.setVertexAttribute(
						vertexId, 
						"y", graph.yPositions[i]
						);
				
			}			
			
		}
		
		
		// add edges
		for (IGraphEdge edge : graph) {
		
			glGraph.addEdge(
					Integer.toString(edge.id), 
					Integer.toString(edge.node1id),
					Integer.toString(edge.node2id)
					);
			
		}
		
		{
			GLLogger.traceTech(
					"transformed an igraph graph with "+totalNodes+" vertices and "+glGraph.getEdgesCount()+" edges in "+
					UserMachineInteractionUtils.getHumanReadableTimeRepresentation(System.currentTimeMillis()-timestampStart), 
					IGraph2GenLabConvertor.class
					);
		}
		
		return glGraph;
		
	}
	
	public static IGraphGraph getIGraphGraphForGenlabGraph(IGenlabGraph genlabGraph, ListOfMessages messages, IGraphLibrary lib) {
	
		if (genlabGraph.getVerticesCount() > Integer.MAX_VALUE)
			throw new WrongParametersException("The network is too large for igraph conversion.");
		
		// TODO emit messages
		
		// TODO check parameters
		
		// TODO check directionaliy
			
		// init the igraph network
		IGraphGraph igraphGraph = lib.generateEmpty(
				(int)genlabGraph.getVerticesCount(), 
				genlabGraph.getDirectionality()==GraphDirectionality.DIRECTED
				);
		
		// copy links
		for (String edgeId : genlabGraph.getEdges()) {
			final Integer id1 = igraphGraph.getIGraphNodeIdForGenlabId(genlabGraph.getEdgeVertexFrom(edgeId)); 
			final Integer id2 = igraphGraph.getIGraphNodeIdForGenlabId(genlabGraph.getEdgeVertexTo(edgeId));
			lib.addEdge(igraphGraph, id1, id2);
		}
		
		// basic checks
		if (genlabGraph.getVerticesCount() != lib.getVertexCount(igraphGraph))
			throw new ProgramException("wrong vertex count after copy");
		if (genlabGraph.getEdgesCount() != lib.getEdgeCount(igraphGraph))
			throw new ProgramException("wrong edge count after copy");
		
		// end !
		return igraphGraph;
		
	}
	

	public static IGraphGraph getIGraphGraphForGenlabGraph(IGenlabGraph genlabGraph, ListOfMessages messages) {
		
		return getIGraphGraphForGenlabGraph(
				genlabGraph, 
				messages, 
				IGraphRawLibraryPool.singleton.getLibrary()
				);

	}
	
	private IGraph2GenLabConvertor() {
		
	}

}

