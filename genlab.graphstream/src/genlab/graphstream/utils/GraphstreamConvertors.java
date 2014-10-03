package genlab.graphstream.utils;

import genlab.core.GenLab;
import genlab.core.commons.ProgramException;
import genlab.core.commons.WrongParametersException;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.GraphFactory;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.MessageAudience;
import genlab.core.usermachineinteraction.MessageLevel;
import genlab.core.usermachineinteraction.TextMessage;
import genlab.graphstream.algos.generators.IGenlabGraphInitializer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.SinkAdapter;

public class GraphstreamConvertors {

	/**
	 * Receives graph events, and pushes them into a GenLab graph.
	 * TODO a parameter to ignore attributes ???
	 * 
	 * @author Samuel Thiriot
	 *
	 */
	public static class GenLabGraphSink extends SinkAdapter {
		
		protected final IGenlabGraph graph;
		
		protected final ListOfMessages messages;
		
		protected Set<String> ignoredAttributesEdge = new HashSet<String>();
		protected Set<String> ignoredAttributesVertex = new HashSet<String>();

		public GenLabGraphSink (String graphId, ListOfMessages messages,  GraphDirectionality directionality, IGenlabGraphInitializer initializer) {
			
			this.messages = messages;
			
			// TODO hard to predict what will be found into the graph...
			graph = GraphFactory.createGraph(graphId, directionality, false);
			
			if (initializer != null)
				initializer.initGraph(graph);
			
		}
		
		public GenLabGraphSink (String graphId, ListOfMessages messages,  GraphDirectionality directionality) {
			this(graphId, messages, directionality, null);
		}

		
		public void ignoreEdgeAttribute(String name) {
			ignoredAttributesEdge.add(name.toLowerCase());
		}
		
		public void ignoreVertexAttribute(String name) {
			ignoredAttributesVertex.add(name.toLowerCase());
		}
		
		@Override
		public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId,
			String toNodeId, boolean directed) {
		
			graph.addEdge(edgeId, fromNodeId, toNodeId, directed);
		}

		@Override
		public void nodeAdded(String sourceId, long timeId, String nodeId) {
			graph.addVertex(nodeId);
		}

		@Override
		public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value) {
			
			if (ignoredAttributesEdge.contains(attribute.toLowerCase()))
				return;

			try {
				graph.setEdgeAttribute(edgeId, attribute, value);
			} catch (WrongParametersException e) {

				if (!graph.hasEdgeAttribute(attribute)) {
					messages.traceTech("the graph had no edge attribute "+attribute+"; we automatically define it", getClass());
					graph.declareEdgeAttribute(attribute, value.getClass());
					graph.setEdgeAttribute(edgeId, attribute, value);
				} else {
					messages.errorTech("error while attempting to define edge attribute: "+attribute, getClass(), e);
					throw e;
				}
			}
		}

		@Override
		public void graphAttributeAdded(String sourceId, long timeId,
				String attribute, Object value) {
			graph.setGraphAttribute(attribute, value);
		}

		@Override
		public void nodeAttributeAdded(String sourceId, long timeId,
				String nodeId, String attribute, Object value) {
			if (ignoredAttributesVertex.contains(attribute.toLowerCase()))
				return;
			
			try {
				graph.setVertexAttribute(nodeId, attribute, value);
			} catch (WrongParametersException e) {

				if (!graph.hasVertexAttribute(attribute)) {
					messages.traceTech("the graph had no vertex attribute "+attribute+"; we automatically define it", getClass());
					graph.declareVertexAttribute(attribute, value.getClass());
					graph.setVertexAttribute(nodeId, attribute, value);
				} else {
					messages.errorTech("error while attempting to define graph attribute: "+attribute, getClass(), e);
					throw e;
				}
			}			
		}

		@Override
		public void edgeAttributeChanged(String sourceId, long timeId,
				String edgeId, String attribute, Object oldValue,
				Object newValue) {
			

			try {
				graph.setEdgeAttribute(edgeId, attribute, newValue);
			} catch (WrongParametersException e) {

				if (!graph.hasEdgeAttribute(attribute)) {
					messages.traceTech("the graph had no edge attribute "+attribute+"; we automatically define it", getClass());
					graph.declareEdgeAttribute(attribute, newValue.getClass());
					graph.setEdgeAttribute(edgeId, attribute, newValue);
				} else {
					messages.errorTech("error while attempting to define edge attribute: "+attribute, getClass(), e);
					throw e;
				}
			}
			
		}

		@Override
		public void edgeAttributeRemoved(String sourceId, long timeId,
				String edgeId, String attribute) {
			messages.add(new TextMessage(MessageLevel.WARNING, MessageAudience.USER, getClass(), "an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"the attribute '"+attribute+"' of an edge was removed"));
		}

		@Override
		public void graphAttributeChanged(String sourceId, long timeId,
				String attribute, Object oldValue, Object newValue) {
			

			try {
				graph.setGraphAttribute(attribute, newValue);
			} catch (WrongParametersException e) {

				if (!graph.hasGraphAttribute(attribute)) {
					messages.traceTech("the graph had no graph attribute "+attribute+"; we automatically define it", getClass());
					graph.declareGraphAttribute(attribute, newValue.getClass());
					graph.setGraphAttribute(attribute, newValue);
				} else {
					messages.errorTech("error while attempting to define edge graph: "+attribute, getClass(), e);
					throw e;
				}
			}
			
			/*
			messages.add(new TextMessage(MessageLevel.WARNING, MessageAudience.USER, getClass(), "an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"the attribute '"+attribute+"' of the graph changed."));
					*/
		}

		@Override
		public void graphAttributeRemoved(String sourceId, long timeId,
				String attribute) {
			messages.add(new TextMessage(MessageLevel.WARNING, MessageAudience.USER, getClass(), "an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"the attribute '"+attribute+"' of the graph was removed"));
		}

		@Override
		public void nodeAttributeChanged(String sourceId, long timeId,
				String nodeId, String attribute, Object oldValue,
				Object newValue) {
			
			try {
				graph.setVertexAttribute(nodeId, attribute, newValue);
			} catch (WrongParametersException e) {

				if (!graph.hasVertexAttribute(attribute)) {
					messages.traceTech("the graph had no vertex attribute "+attribute+"; we automatically define it as "+newValue.getClass(), getClass());
					graph.declareVertexAttribute(attribute, newValue.getClass());
					graph.setVertexAttribute(nodeId, attribute, newValue);
				} else {
					messages.errorTech("error while attempting to define vertex attribute: "+attribute, getClass(), e);
					throw e;
				}
			}
			
		}

		@Override
		public void nodeAttributeRemoved(String sourceId, long timeId,
				String nodeId, String attribute) {
			messages.add(new TextMessage(MessageLevel.WARNING, MessageAudience.USER, getClass(), "an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"the attribute '"+attribute+"' of a node was removed"));
		}

		@Override
		public void edgeRemoved(String sourceId, long timeId, String edgeId) {
			graph.removeEdge(edgeId);
		}

		@Override
		public void graphCleared(String sourceId, long timeId) {
			messages.add(new TextMessage(MessageLevel.WARNING, MessageAudience.USER, getClass(), "an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"the graph should have been cleaned"));
		}

		@Override
		public void nodeRemoved(String sourceId, long timeId, String nodeId) {
			messages.add(new TextMessage(MessageLevel.WARNING, MessageAudience.USER, getClass(), "an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"a node should have been removed"));
		}

		@Override
		public void stepBegins(String sourceId, long timeId, double step) {
			messages.add(new TextMessage(MessageLevel.WARNING, MessageAudience.USER, getClass(), "an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"new step detected."));
		}
		
		public IGenlabGraph getGraph() {
			return graph; // TODO postprocessing, like define if directed, etc...
		}
		
	}
	
	public static IGenlabGraph loadGraphWithGraphstreamFromGeneratorSource(String graphId, BaseGenerator generator, int maxNodes, ListOfMessages messages, boolean countIterations, GraphDirectionality directionality) {
		return loadGraphWithGraphstreamFromGeneratorSource(graphId, generator, maxNodes, messages, countIterations, directionality, null);
	}
	
	public static IGenlabGraph loadGraphWithGraphstreamFromGeneratorSource(String graphId, BaseGenerator generator, int maxNodes, ListOfMessages messages, boolean countIterations, GraphDirectionality directionality, IGenlabGraphInitializer initializer) {


			messages.debugTech("loading the graph from a source into a genlab graph...", GraphstreamConvertors.class);
		
			GenLabGraphSink ourSink = new GenLabGraphSink(graphId, messages, directionality, initializer);
			// TODO ??? ourSink.ignoreVertexAttribute("xy");
			generator.addSink(ourSink);

			// load the graph

			if ( maxNodes < 0 ) {
				generator.begin();
				while (generator.nextEvents()) {
					// nothing to do
				}
				generator.end();
			} else {
				generator.begin();
				if (countIterations) {
					for (int i = 0; i < maxNodes;  i++ ) {
						if (!generator.nextEvents())
							break;
					}
				} else {
					while (ourSink.graph.getVerticesCount() <= maxNodes) {
						if (!generator.nextEvents())
							break;
					}
				}
				generator.end();
			}

			return ourSink.getGraph();

		}
	
	
	/**
	 * Takes a gama graph as an input, returns a graphstream graph as 
	 * close as possible. Preserves double links (multi graph).
	 * @param gamaGraph
	 * @return
	 */
	public static Graph getGraphstreamGraphFromGenLabGraph(IGenlabGraph genlabGraph, ListOfMessages messages) {
		
		if (genlabGraph == null)
			throw new ProgramException("input graph parameter can not be null");
		
		Graph g = new MultiGraph("tmpGraph", true, false);
		
		messages.debugTech("transcoding from a genlab graph to a graphstream graph...", GraphstreamConvertors.class);
		
		Map<String,Node> genlabNode2graphStreamNode = new HashMap<String, Node>((int) genlabGraph.getVerticesCount());
		
		// TODO graph attributes
		
		// add nodes		
		for (String vertexId : genlabGraph.getVertices()) {
				
			Node n = g.addNode(vertexId);
			
			genlabNode2graphStreamNode.put(
					vertexId,
					n
					);
			
			// copy attributes values of vertices
			for (Map.Entry<String,Object> kv : genlabGraph.getVertexAttributes(vertexId).entrySet()) {
				n.setAttribute(
						kv.getKey(), 
						kv.getValue()
						);
			}		
		}
		
		// add edges
		for (String edgeId : genlabGraph.getEdges()) {
			
			try {
				Edge e = g.addEdge(
						edgeId, 
						genlabGraph.getEdgeVertexFrom(edgeId),
						genlabGraph.getEdgeVertexTo(edgeId), 
						genlabGraph.isEdgeDirected(edgeId)				// till now, directionality of an edge depends on the whole gama graph
						);
				
				// copy attributes
				for (Map.Entry<String,Object> kv: genlabGraph.getEdgeAttributes(edgeId).entrySet()) {
					e.setAttribute(kv.getKey(), kv.getValue());
				}

			} catch (EdgeRejectedException e) {
				messages.add(new TextMessage(
						MessageLevel.WARNING, 
						MessageAudience.USER, 
						GraphstreamConvertors.class, 
						"an edge was rejected during the transformation, probably because it was a double one", 
						e
						));
			} catch (IdAlreadyInUseException e) {
				messages.add(new TextMessage(
						MessageLevel.WARNING, 
						MessageAudience.USER, 
						GraphstreamConvertors.class, 
						"an edge was rejected during the transformation, probably because it was a double one", 
						e));
			}

			
		}

		// some basic tests for integrity
		if (genlabGraph.getVerticesCount() != g.getNodeCount())
			messages.add(
					new TextMessage(
							MessageLevel.WARNING, 
							MessageAudience.USER, 
							GraphstreamConvertors.class,
							"The exportation ran without error, but an integrity test failed: " +
							"the number of vertices is not correct("+g.getNodeCount()+" instead of "+genlabGraph.getVertices().size()+")"
							)
					);
		if (genlabGraph.getEdgesCount() != g.getEdgeCount())
			messages.add(
					new TextMessage(
							MessageLevel.WARNING, 
							MessageAudience.USER,
							GraphstreamConvertors.class,
							"The exportation ran without error, but an integrity test failed: " +
							"the number of vertices is not correct("+g.getNodeCount()+" instead of "+genlabGraph.getVertices().size()+")"
							)
					);								
		return g;
	}

}
