package genlab.graphstream.utils;

import java.util.HashSet;
import java.util.Set;

import genlab.basics.javaTypes.graphs.GraphDirectionality;
import genlab.basics.javaTypes.graphs.GraphFactory;
import genlab.basics.javaTypes.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.MessageAudience;
import genlab.core.usermachineinteraction.MessageLevel;
import genlab.core.usermachineinteraction.TextMessage;

import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.stream.SinkAdapter;

public class GraphstreamConvertors {

	/**
	 * Receives graph events, and pushes them into a GenLab graph.
	 * TODO manage the automatic creation of attributes !
	 * 
	 * @author Samuel Thiriot
	 *
	 */
	public static class GenLabGraphSink extends SinkAdapter {
		
		protected final IGenlabGraph graph;
		
		protected ListOfMessages messages = new ListOfMessages();
		
		protected Set<String> ignoredAttributesEdge = new HashSet<String>();
		protected Set<String> ignoredAttributesVertex = new HashSet<String>();

		public GenLabGraphSink (String graphId) {
			
			// TODO hard to predict what will be found into the graph...
			graph = GraphFactory.createGraph(graphId, GraphDirectionality.MIXED, false);
			
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
			graph.setEdgeAttribute(edgeId, attribute, value);
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
			graph.setVertexAttribute(nodeId, attribute, value);
			
		}

		@Override
		public void edgeAttributeChanged(String sourceId, long timeId,
				String edgeId, String attribute, Object oldValue,
				Object newValue) {
			
			messages.add(new TextMessage(MessageLevel.WARNING, MessageAudience.USER, "an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"the attribute '"+attribute+"' of an edge changed."));
		}

		@Override
		public void edgeAttributeRemoved(String sourceId, long timeId,
				String edgeId, String attribute) {
			messages.add(new TextMessage(MessageLevel.WARNING, MessageAudience.USER, "an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"the attribute '"+attribute+"' of an edge was removed"));
		}

		@Override
		public void graphAttributeChanged(String sourceId, long timeId,
				String attribute, Object oldValue, Object newValue) {
			messages.add(new TextMessage(MessageLevel.WARNING, MessageAudience.USER, "an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"the attribute '"+attribute+"' of the graph changed."));
		}

		@Override
		public void graphAttributeRemoved(String sourceId, long timeId,
				String attribute) {
			messages.add(new TextMessage(MessageLevel.WARNING, MessageAudience.USER, "an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"the attribute '"+attribute+"' of the graph was removed"));
		}

		@Override
		public void nodeAttributeChanged(String sourceId, long timeId,
				String nodeId, String attribute, Object oldValue,
				Object newValue) {
			messages.add(new TextMessage(MessageLevel.WARNING, MessageAudience.USER, "an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"the attribute '"+attribute+"' of a node changed."));
		}

		@Override
		public void nodeAttributeRemoved(String sourceId, long timeId,
				String nodeId, String attribute) {
			messages.add(new TextMessage(MessageLevel.WARNING, MessageAudience.USER, "an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"the attribute '"+attribute+"' of a node was removed"));
		}

		@Override
		public void edgeRemoved(String sourceId, long timeId, String edgeId) {
			messages.add(new TextMessage(MessageLevel.WARNING, MessageAudience.USER, "an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"an edge should have been removed"));
		}

		@Override
		public void graphCleared(String sourceId, long timeId) {
			messages.add(new TextMessage(MessageLevel.WARNING, MessageAudience.USER, "an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"the graph should have been cleaned"));
		}

		@Override
		public void nodeRemoved(String sourceId, long timeId, String nodeId) {
			messages.add(new TextMessage(MessageLevel.WARNING, MessageAudience.USER, "an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"a node should have been removed"));
		}

		@Override
		public void stepBegins(String sourceId, long timeId, double step) {
			messages.add(new TextMessage(MessageLevel.WARNING, MessageAudience.USER, "an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"new step detected."));
		}
		
		public IGenlabGraph getGraph() {
			return graph; // TODO postprocessing, like define if directed, etc...
		}
		
	}
	
	public static IGenlabGraph loadGraphWithGraphstreamFromGeneratorSource(String graphId, BaseGenerator generator, int maxLinks) {


			GenLabGraphSink ourSink = new GenLabGraphSink(graphId);
			ourSink.ignoreVertexAttribute("xy");
			generator.addSink(ourSink);

			// load the graph

			if ( maxLinks < 0 ) {
				generator.begin();
				while (generator.nextEvents()) {
					// nothing to do
				}
				generator.end();
			} else {
				generator.begin();
				for ( int i = 0; i < maxLinks; i++ ) {
					generator.nextEvents();
				}
				generator.end();
			}

			return ourSink.getGraph();

		}

}
