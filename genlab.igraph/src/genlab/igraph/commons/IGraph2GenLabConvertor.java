package genlab.igraph.commons;

import genlab.core.commons.ProgramException;
import genlab.core.commons.WrongParametersException;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.natjna.IGraphLibrary;
import genlab.igraph.natjna.IGraphRawLibraryPool;

public class IGraph2GenLabConvertor {

	public static IGraphGraph getIGraphGraphForGenlabGraph(IGenlabGraph genlabGraph, ListOfMessages messages) {
		
		// TODO emit messages
		
		// TODO check parameters
		if (genlabGraph.getVerticesCount() > Integer.MAX_VALUE)
			throw new WrongParametersException("The network is too large for igraph conversion.");
		
		// TODO check directionaliy
		
		// find one library for the processing
		IGraphLibrary lib = IGraphRawLibraryPool.singleton.getLibrary();
	
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
	
	private IGraph2GenLabConvertor() {
		
	}

}

