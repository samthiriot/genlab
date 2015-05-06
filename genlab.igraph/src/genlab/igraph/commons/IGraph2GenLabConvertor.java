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
import genlab.igraph.natjna.InternalVectorStruct;

import java.util.Collection;
import java.util.HashSet;

/**
 * Conversion between igraph Graphs and Genlab Graphs 
 * 
 * @author Samuel Thiriot
 *
 */
public class IGraph2GenLabConvertor {

	/**
	 * Number of edges to create at the same time (should be even)
	 */
	public static final int BUFFER_EDGES_CREATION = 2000;
	
	public static final String KEY_INFO_CONVERT_GENLAB_TO_IGRAPH = "igraph / count of graph conversions from genlab to igraph";
	public static final String KEY_INFO_CONVERT_GENLAB_TO_IGRAPH_TIME = "igraph / cumulated time for graph conversions from genlab to igraph (ms)";
	public static final String KEY_INFO_CONVERT_IGRAPH_TO_GENLAB = "igraph / count of graph conversions from igraph to genlab";
	public static final String KEY_INFO_CONVERT_IGRAPH_TO_GENLAB_TIME = "igraph / cumulated time for graph conversions from igraph to genlab (ms)";
	
	
	public static IGenlabGraph getGenlabGraphForIgraph(IGraphGraph graph, IExecution execution) {
		
		final ListOfMessages messages = execution.getListOfMessages();
		
		final long timestampStart = System.currentTimeMillis();
		
		int totalNodes = -1;
		IGenlabGraph glGraph = null;
		
		try {
		
			if (graph.isMultiGraph() == null) {
				messages.infoUser("the multiplicity of the converted graph is unknown; will assume it is multiplex, but this will lead to bad performance", IGraph2GenLabConvertor.class);
			}		
			
			glGraph = GraphFactory.createGraph(
					"igraphGen", 
					graph.directed?GraphDirectionality.DIRECTED:GraphDirectionality.UNDIRECTED, 
					(graph.isMultiGraph() != Boolean.FALSE)
					);
			
			
			// add node attributes
			if (graph.xPositions != null) {
				
				// declare attributes
				glGraph.declareVertexAttribute("x", Double.class);
				glGraph.declareVertexAttribute("y", Double.class);
				
				messages.debugUser("x and y attributes are provided by igraph; will copy them", IGraph2GenLabConvertor.class);
			}
			
			// add nodes
			totalNodes = graph.lib.getVertexCount(graph);
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
				long timeElapsed = System.currentTimeMillis()-timestampStart;
				
				execution.incrementTechnicalInformationLong(KEY_INFO_CONVERT_IGRAPH_TO_GENLAB);
				execution.incrementTechnicalInformationLong(KEY_INFO_CONVERT_IGRAPH_TO_GENLAB_TIME, timeElapsed);
	
				messages.traceTech(
						"transformed an igraph graph with "+totalNodes+" vertices and "+glGraph.getEdgesCount()+" edges in "+
						UserMachineInteractionUtils.getHumanReadableTimeRepresentation(timeElapsed), 
						IGraph2GenLabConvertor.class
						);
			}
			
			// basic checks
			if (glGraph.getVerticesCount() != graph.lib.getVertexCount(graph))
				throw new ProgramException("wrong vertex count after copy: the genlab graph has "+glGraph.getVerticesCount()+", while the copy has "+graph.lib.getVertexCount(graph));
			if (glGraph.getEdgesCount() != graph.lib.getEdgeCount(graph))
				throw new ProgramException("wrong edge count after copy: the genlab graph has "+glGraph.getEdgesCount()+", while the copy has "+graph.lib.getEdgeCount(graph));
			
			
			return glGraph;
			
		} catch (OutOfMemoryError e) {
			messages.errorUser("error during the conversion of a graph from igraph ("+totalNodes+" vertices, more than "+glGraph.getEdgesCount()+" edges): no enough memory", IGraph2GenLabConvertor.class, e);
			throw new ProgramException("not enough memory", e);
		}
		
	}
	
	
	public static IGraphGraph getIGraphGraphForGenlabGraph(IGenlabGraph genlabGraph, IExecution execution, IGraphLibrary lib) {
	
		if (genlabGraph.getVerticesCount() > Integer.MAX_VALUE)
			throw new WrongParametersException("The network is too large for igraph conversion.");
	
		final ListOfMessages messages = execution.getListOfMessages();

		final long timestampStart = System.currentTimeMillis();

		try {
			// TODO emit messages
			
			// TODO check parameters
			
			// TODO check directionaliy
			
			// init the igraph network (with the good directionality and vertex count)
			IGraphGraph igraphGraph = lib.generateEmpty(
					(int)genlabGraph.getVerticesCount(), 
					genlabGraph.getDirectionality()==GraphDirectionality.DIRECTED
					);
			// TODO multigrapph ??
			
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
						
						final Integer id1 = igraphGraph.getOrCreateIGraphNodeIdForGenlabId(genlabGraph.getEdgeVertexFrom(edgeId)); 
						final Integer id2 = igraphGraph.getOrCreateIGraphNodeIdForGenlabId(genlabGraph.getEdgeVertexTo(edgeId));
				
						buffer[filledInBuffer++] = id1.doubleValue();
						buffer[filledInBuffer++] = id2.doubleValue();
		
						if (sizeBuffer - filledInBuffer < 2) {
							// no more place in this buffer...
							// fill the vector with this data
							vectorEdges.fillWithArray(buffer, filledInBuffer);
							// write this data
							lib.addEdges(igraphGraph, vectorEdges);
							// continue
							filledInBuffer = 0;
						}
						//lib.addEdge(igraphGraph, id1, id2);
					}
					
					// push remaining data
					if (filledInBuffer > 0) {
						// fill the vector with this data
						vectorEdges.fillWithArray(buffer, filledInBuffer);
						// write this data
						lib.addEdges(igraphGraph, vectorEdges);
					}
				
				} finally {
					lib.clearVector(vectorEdges);
				}
			}
			
			// basic checks
			if (genlabGraph.getVerticesCount() != lib.getVertexCount(igraphGraph))
				throw new ProgramException("wrong vertex count after copy: the genlab graph has "+genlabGraph.getVerticesCount()+", while the copy has "+lib.getVertexCount(igraphGraph));
			if (genlabGraph.getEdgesCount() != lib.getEdgeCount(igraphGraph))
				throw new ProgramException("wrong edge count after copy: the genlab graph has "+genlabGraph.getEdgesCount()+", while the copy has "+lib.getEdgeCount(igraphGraph));
			
	
			{ // communicate
				long timeElapsed = System.currentTimeMillis()-timestampStart;
		
				execution.incrementTechnicalInformationLong(KEY_INFO_CONVERT_GENLAB_TO_IGRAPH);
				execution.incrementTechnicalInformationLong(KEY_INFO_CONVERT_GENLAB_TO_IGRAPH_TIME, timeElapsed);
	
			}
			// end !
			return igraphGraph;
		} catch (OutOfMemoryError e) {
			messages.errorUser("error during the conversion of a graph to igraph ("+genlabGraph.getVerticesCount()+" vertices, "+genlabGraph.getEdgesCount()+" edges): no enough memory", IGraph2GenLabConvertor.class, e);
			throw new ProgramException("not enough memory", e);
		}
		
	}
	
	/**
	 * For an output genlab graph, which is the original of the igraphGraph provided,
	 * take the values provided as parameters, and use them as the attributes' values in the 
	 * genlab graph.
	 * @param genlabGraph
	 * @param igraphGraph
	 * @param attributeName
	 * @param attributesValues
	 */
	public static void addAttributesToNodesGenlabGraphFromIgraph(
			IGenlabGraph genlabGraph, 
			IGraphGraph igraphGraph,
			String attributeName,
			double[] attributesValues
			) {
	
		// check data size
		if (genlabGraph.getVerticesCount() != attributesValues.length)
			throw new ProgramException("wrong number of vertices");
			
		// declare the resulting parameter
		genlabGraph.declareVertexAttribute(attributeName, Double.class);
	
		// transfert data
		for (int i=0; i<attributesValues.length; i++) {
			genlabGraph.setVertexAttribute(
					igraphGraph.getGenlabIdForIGraphNode(i), 
					attributeName, 
					attributesValues[i]
					);
		}
		
		// done.
		
	}
	
	/**
	 * For an output genlab graph, which is the original of the igraphGraph provided,
	 * take the values provided as parameters, and use them as the attributes' values in the 
	 * genlab graph.
	 * @param genlabGraph
	 * @param igraphGraph
	 * @param attributeName
	 * @param attributesValues
	 */
	public static void addAttributesToEdgesGenlabGraphFromIgraph(
			IGenlabGraph genlabGraph, 
			IGraphGraph igraphGraph,
			String attributeName,
			double[] attributesValues
			) {
	
		// check data size
		if (genlabGraph.getEdgesCount() != attributesValues.length)
			throw new ProgramException("wrong number of edges");
			
		// declare the resulting parameter
		genlabGraph.declareEdgeAttribute(attributeName, Double.class);
	
		
		// TODO is the ID of edges unique enough ???
		
		// transfert data
		int i=0;
		if (genlabGraph.isMultiGraph()) {
			
			// in the case of multi graphs, we may discover such a situtation:
			// 0 -- 1
			// 0 -- 2	// several times !
			// 0 -- 2   // several times !
			// 0 -- 3
			
			// So to solve this case, we build a black list of edges already processed
			HashSet<String> alreadyProcessedEdges = new HashSet<String>();
			
			for (IGraphEdge edge: igraphGraph) {
				
				if (i >= attributesValues.length)
					throw new ProgramException("wrong number of attributes, they are less numerous ("+i+") than edges in the graph");
				
				Collection<String> edgesId = genlabGraph.getEdgesBetween(
						igraphGraph.getGenlabIdForIGraphNode(edge.node1id),
						igraphGraph.getGenlabIdForIGraphNode(edge.node2id)
						);
				
				if (edgesId == null || edgesId.isEmpty())
					throw new ProgramException("unable to find an edge: "+edge.node1id+" "+edge.node2id);
				
				// don't reuse the edges already used
				edgesId.removeAll(alreadyProcessedEdges);			
				
				if (edgesId.isEmpty())
					throw new ProgramException("unable to find an edge: "+edge.node1id+" "+edge.node2id);
				
				// and use the first one
				String edgeId = edgesId.iterator().next();
				alreadyProcessedEdges.add(edgeId);
				
				genlabGraph.setEdgeAttribute(
						edgeId, 
						attributeName, 
						attributesValues[i]
						);
								
				i++;
				
			}
			if (i != attributesValues.length)
				throw new ProgramException("wrong number of attributes, they are more numerous ("+i+") than edges in the graph");
			
			
		} else {
		
			
			for (IGraphEdge edge: igraphGraph) {
				
				if (i >= attributesValues.length)
					throw new ProgramException("wrong number of attributes, they are less numerous ("+i+") than edges in the graph");
				
				String edgeId = genlabGraph.getEdgeBetween(
						igraphGraph.getGenlabIdForIGraphNode(edge.node1id),
						igraphGraph.getGenlabIdForIGraphNode(edge.node2id)
						);
				
				if (edgeId == null)
					throw new ProgramException("unable to find an edge: "+edge.node1id+" "+edge.node2id);
				
				genlabGraph.setEdgeAttribute(
						edgeId, 
						attributeName, 
						attributesValues[i]
						);
								
				i++;
				
			}
			if (i != attributesValues.length)
				throw new ProgramException("wrong number of attributes, they are more numerous ("+i+") than edges in the graph");
			
			
		}
		

		// done.
		
	}
	

	public static IGraphGraph getIGraphGraphForGenlabGraph(IGenlabGraph genlabGraph, IExecution execution) {
		
		return getIGraphGraphForGenlabGraph(
				genlabGraph, 
				execution, 
				new IGraphLibrary()
				);

	}
	
	private IGraph2GenLabConvertor() {
		
	}

}

