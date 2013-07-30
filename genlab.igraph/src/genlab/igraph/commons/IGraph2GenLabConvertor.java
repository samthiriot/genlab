package genlab.igraph.commons;

import genlab.core.commons.ProgramException;
import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.GraphFactory;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.UserMachineInteractionUtils;
import genlab.igraph.natjna.IGraphEdge;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.natjna.IGraphLibrary;
import genlab.igraph.natjna.IGraphRawLibraryPool;
import genlab.igraph.natjna.InternalVectorStruct;

public class IGraph2GenLabConvertor {

	public static final String KEY_INFO_CONVERT_GENLAB_TO_IGRAPH = "count of graph conversions from genlab to igraph";
	public static final String KEY_INFO_CONVERT_GENLAB_TO_IGRAPH_TIME = "cumulated time of graph conversions from genlab to igraph (ms)";
	public static final String KEY_INFO_CONVERT_IGRAPH_TO_GENLAB = "count of graph conversions from igraph to genlab";
	public static final String KEY_INFO_CONVERT_IGRAPH_TO_GENLAB_TIME = "cumulated time of graph conversions from igraph to genlab (ms)";
	
	
	public static IGenlabGraph getGenlabGraphForIgraph(IGraphGraph graph, IExecution execution) {
		
		final ListOfMessages messages = execution.getListOfMessages();
		
		final long timestampStart = System.currentTimeMillis();
		
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
			
			messages.debugUser("x and y attributes are provided by igraph; will copy them", IGraph2GenLabConvertor.class);
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
		
			//System.err.println(edge);
			
			glGraph.addEdge(
					Integer.toString(edge.id), 
					Integer.toString(edge.node1id),
					Integer.toString(edge.node2id)
					);
			
		}
		
		{
			long timeElapsed = System.currentTimeMillis()-timestampStart;
			
			execution.incrementTechnicalInformationLong(KEY_INFO_CONVERT_IGRAPH_TO_GENLAB);
			execution.incrementTechnicalInformationLong(KEY_INFO_CONVERT_IGRAPH_TO_GENLAB_TIME, timeElapsed);

			messages.traceTech(
					"transformed an igraph graph with "+totalNodes+" vertices and "+glGraph.getEdgesCount()+" edges in "+
					UserMachineInteractionUtils.getHumanReadableTimeRepresentation(timeElapsed), 
					IGraph2GenLabConvertor.class
					);
		}
		
		return glGraph;
		
	}
	
	/**
	 * Number of edges to create (should be even)
	 */
	public static final int BUFFER_EDGES_CREATION = 1000;
	
	public static IGraphGraph getIGraphGraphForGenlabGraph(IGenlabGraph genlabGraph, IExecution execution, IGraphLibrary lib) {
	
		if (genlabGraph.getVerticesCount() > Integer.MAX_VALUE)
			throw new WrongParametersException("The network is too large for igraph conversion.");
	
		final ListOfMessages messages = execution.getListOfMessages();

		final long timestampStart = System.currentTimeMillis();

		// TODO emit messages
		
		// TODO check parameters
		
		// TODO check directionaliy
			
		// init the igraph network (with the good directionality and vertex count)
		IGraphGraph igraphGraph = lib.generateEmpty(
				(int)genlabGraph.getVerticesCount(), 
				genlabGraph.getDirectionality()==GraphDirectionality.DIRECTED
				);
		
		// copy links
		if (genlabGraph.getEdgesCount() > 0) {
			
			int sizeBuffer = 	Math.min(
					BUFFER_EDGES_CREATION, 
					(int)genlabGraph.getEdgesCount()*2
					);
			InternalVectorStruct vectorEdges = lib.createEmptyVector(
					sizeBuffer
					);
			int filledInBuffer = 0;
					
			double[] buffer = new double[sizeBuffer];
			
			try {
			
			
			for (String edgeId : genlabGraph.getEdges()) {
				
				final Integer id1 = igraphGraph.getIGraphNodeIdForGenlabId(genlabGraph.getEdgeVertexFrom(edgeId)); 
				final Integer id2 = igraphGraph.getIGraphNodeIdForGenlabId(genlabGraph.getEdgeVertexTo(edgeId));
				buffer[filledInBuffer++] = id1.doubleValue();
				buffer[filledInBuffer++] = id2.doubleValue();

				if (sizeBuffer - filledInBuffer < 2) {
					// no more place in this buffer...
					// fill the vector with this data
					vectorEdges.fillWithArray(buffer, sizeBuffer);
					// write this data
					lib.addEdges(igraphGraph, vectorEdges);
					// continue
					filledInBuffer = 0;
				}
				//lib.addEdge(igraphGraph, id1, id2);
			}
			
			} finally {
				lib.clearVector(vectorEdges);
			}
		}
		
		// basic checks
		if (genlabGraph.getVerticesCount() != lib.getVertexCount(igraphGraph))
			throw new ProgramException("wrong vertex count after copy");
		if (genlabGraph.getEdgesCount() != lib.getEdgeCount(igraphGraph))
			throw new ProgramException("wrong edge count after copy");
		

		{ // communicate
			long timeElapsed = System.currentTimeMillis()-timestampStart;
	
			execution.incrementTechnicalInformationLong(KEY_INFO_CONVERT_GENLAB_TO_IGRAPH);
			execution.incrementTechnicalInformationLong(KEY_INFO_CONVERT_GENLAB_TO_IGRAPH_TIME, timeElapsed);

		}
		// end !
		return igraphGraph;
		
	}
	

	public static IGraphGraph getIGraphGraphForGenlabGraph(IGenlabGraph genlabGraph, IExecution execution) {
		
		return getIGraphGraphForGenlabGraph(
				genlabGraph, 
				execution, 
				IGraphRawLibraryPool.singleton.getLibrary()
				);

	}
	
	private IGraph2GenLabConvertor() {
		
	}

}

