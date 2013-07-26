package genlab.core.model.meta.basics.graphs;

import genlab.core.commons.NotImplementedException;
import genlab.core.commons.ProgramException;
import genlab.core.commons.WrongParametersException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;
import org.graphstream.stream.GraphReplay;

/**
 * Abstract class for graphstream based operations on graphs.
 * 
 * TODO make it multithread compliant !
 * 
 * @author Samuel Thiriot
 *
 */
public abstract class AbstractGraphstreamBasedGraph implements IGenlabGraph {

	protected final Graph gsGraph;
	
	@SuppressWarnings("rawtypes")
	protected final Map<String,Class> edgeAttributes2type = new HashMap<String, Class>();
	@SuppressWarnings("rawtypes")
	protected final Map<String,Class> vertexAttributes2type = new HashMap<String, Class>();	
	@SuppressWarnings("rawtypes")
	protected final Map<String,Class> graphAttribute2type = new HashMap<String, Class>();
	
	/**
	 * Please always active strict checking for the graphstream graph.
	 * @param gsGraph
	 */
	public AbstractGraphstreamBasedGraph(Graph gsGraph) {
		
		this.gsGraph = gsGraph;
		
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void declareVertexAttribute(String attributeId, Class type) {
		if (vertexAttributes2type.containsKey(attributeId))
			throw new WrongParametersException("a node attribute "+attributeId+" was already declared for this graph");
		vertexAttributes2type.put(attributeId, type);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void declareEdgeAttribute(String attributeId, Class type) {
		if (edgeAttributes2type.containsKey(attributeId))
			throw new WrongParametersException("an edge attribute "+attributeId+" was already declared for this graph");
		edgeAttributes2type.put(attributeId, type);
	}
	
	@Override
	public boolean hasVertexAttribute(String attributeId) {
		return vertexAttributes2type.containsKey(attributeId);
	}

	@Override
	public boolean hasEdgeAttribute(String attributeId) {
		return edgeAttributes2type.containsKey(attributeId);
	}

	@Override
	public Collection<String> getDeclaredVertexAttributes() {
		return vertexAttributes2type.keySet();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Map<String, Class> getDeclaredVertexAttributesAndTypes() {
		return Collections.unmodifiableMap(vertexAttributes2type);
	}

	@Override
	public Collection<String> getDeclaredEdgeAttributes() {
		return edgeAttributes2type.keySet();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Map<String, Class> getDeclaredEdgeAttributesAndTypes() {
		return Collections.unmodifiableMap(edgeAttributes2type);

	}

	@Override
	public void addVertex(String id) {
		try {
			gsGraph.addNode(id);
		} catch (IdAlreadyInUseException e) {
			throw new WrongParametersException("a vertex was already added in this graph with id "+id);
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void setVertexAttribute(String vertexId, String attributeId,
			Object value) {
		// ensure existence of the node
		Node gsNode = gsGraph.getNode(vertexId); 
		if (gsNode == null) {
			throw new WrongParametersException("no vertex "+vertexId+" declared");
		}
		// ensure compliance of parameters
		Class attributeType = vertexAttributes2type.get(attributeId);
		if (attributeType == null) {
			throw new WrongParametersException("no vertex attribute "+attributeId+" defined for this graph");
		}
		if (!attributeType.isInstance(value)) {
			throw new WrongParametersException("type "+attributeType.getSimpleName()+" is expected for attribute "+attributeId);
		}
		// finally add value
		gsNode.setAttribute(attributeId, value);
		 
	}

	@Override
	public Collection<String> getVertices() {
		
		if (gsGraph.getNodeCount() == 0) // quick exit
			return Collections.EMPTY_LIST;
		
		LinkedList<String> res = new LinkedList<String>();
		Iterator itNodes = gsGraph.getNodeIterator();
		while (itNodes.hasNext()) {
			res.add(((Node)itNodes.next()).getId());
		}
		return res;
				
	}

	@Override
	public void addEdge(String id, String vertexIdFrom, String vertexIdTo) {
		
		if (getDirectionality() == GraphDirectionality.MIXED)
			throw new WrongParametersException("As this graph is mixed, you have to precise for each new edge if it is directed or not.");
		
		addEdge(
				vertexIdFrom+"_to_"+vertexIdTo, 
				vertexIdFrom, 
				vertexIdTo, 
				getDirectionality()==GraphDirectionality.DIRECTED
				);
	}

	@Override
	public void addEdge(String vertexIdFrom, String vertexIdTo, boolean directed) {
		addEdge(
				vertexIdFrom+"_to_"+vertexIdTo, 
				vertexIdFrom, 
				vertexIdTo, 
				directed
				);
	}

	@Override
	public void addEdge(String id, String vertexIdFrom, String vertexIdTo, boolean directed) {
		
		if (directed) {
			
			if (getDirectionality() == GraphDirectionality.UNDIRECTED)
				throw new WrongParametersException("can not add a directed edge into an undirected graph");
		} else {
			if (getDirectionality() == GraphDirectionality.DIRECTED)
				throw new WrongParametersException("can not add an undirected edge into a directed graph");
		}
			
		if (gsGraph.getEdge(id) != null)
			throw new WrongParametersException("the edge "+id+" was already defined");
		
		if (!isMultiGraph() && containsEdge(vertexIdFrom, vertexIdTo))
			throw new WrongParametersException("an edge was already defined between "+vertexIdFrom+" and "+vertexIdTo);
		
		gsGraph.addEdge(id, vertexIdFrom, vertexIdTo, directed);
		 
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void setEdgeAttribute(String vertexId, String attributeId,
			Object value) {
		// ensure existence of the node
		Edge gsEdge = gsGraph.getEdge(vertexId); 
		if (gsEdge == null) {
			throw new WrongParametersException("no edge "+vertexId+" declared");
		}
		// ensure compliance of parameters
		Class attributeType = edgeAttributes2type.get(attributeId);
		if (attributeType == null) {
			throw new WrongParametersException("no edge attribute "+attributeId+" defined for this graph");
		}
		if (!attributeType.isInstance(value)) {
			throw new WrongParametersException("type "+attributeType.getSimpleName()+" is expected for attribute "+attributeId);
		}
		// finally add value
		gsEdge.setAttribute(attributeId, value);
		 
	}

	@Override
	public boolean isVertexAttributed() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEdgeAttributed() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean containsEdge(String vertexFrom, String vertexTo) {
		
		switch (getDirectionality()) {
		
		case DIRECTED:
		case MIXED:
			return gsGraph.getNode(vertexFrom).hasEdgeToward(vertexTo);
		case UNDIRECTED:
			return gsGraph.getNode(vertexFrom).hasEdgeBetween(vertexTo) || gsGraph.getNode(vertexTo).hasEdgeBetween(vertexFrom);
		
		default:
			throw new ProgramException("unknown enum type for directionality: "+getDirectionality());
		}
		
	}
	
	@Override
	public boolean containsVertex(String vertexId) {
		return (gsGraph.getNode(vertexId) != null);
	}


	@Override
	public Collection<String> getNeighboors(String vertexId) {
		
		Node gsNode = gsGraph.getNode(vertexId);
		if (gsNode == null)
			throw new WrongParametersException("unknown vertex: "+vertexId);
		
		if (gsNode.getEdgeSet().isEmpty())	// quick exit
			return Collections.EMPTY_LIST;
		
		LinkedList<String> res = new LinkedList<String>();
		Iterator itNodes = gsNode.getNeighborNodeIterator();
		while (itNodes.hasNext()) {
			res.add(((Node)itNodes.next()).getId());
		}
		return res;
	}
	
	public Graph _getInternalGraphstreamGraph() {
		return gsGraph;
	}


	@Override
	public Collection<String> getDeclaredGraphAttributes() {
		return graphAttribute2type.keySet();
	}

	@Override
	public Map<String, Class> getDeclaredGraphAttributesAndTypes() {
		return Collections.unmodifiableMap(graphAttribute2type);
	}

	@Override
	public void setGraphAttribute(String attributeId, Object value) {
		// ensure compliance of parameters
		Class attributeType = graphAttribute2type.get(attributeId);
		if (attributeType == null) {
			throw new WrongParametersException("no graph attribute "+attributeId+" defined for this graph");
		}
		if (!attributeType.isInstance(value)) {
			throw new WrongParametersException("type "+attributeType.getSimpleName()+" is expected for attribute "+attributeId);
		}
		// finally add value
		gsGraph.setAttribute(attributeId, value);
		 
	}

	@Override
	public Object getGraphAttribute(String attributeId) {
		return gsGraph.getAttribute(attributeId);
	}

	@Override
	public void declareGraphAttribute(String attributeId, Class type) {
		if (graphAttribute2type.containsKey(attributeId))
			throw new WrongParametersException("a graph attribute "+attributeId+" was already declared for this graph");
		graphAttribute2type.put(attributeId, type);
	}

	@Override
	public boolean hasGraphAttribute(String attribute) {
		return graphAttribute2type.containsKey(attribute);
	}
	
	@Override
	public String getGraphId() {
		return gsGraph.getId();
	}


	@Override
	public long getVerticesCount() {
		return gsGraph.getNodeCount();
	}

	@Override
	public long getEdgesCount() {
		return gsGraph.getEdgeCount();
	}
	
	@Override
	public Map<String, Object> getVertexAttributes(String vertexId) {
		
		// TODO very slow; optimize it ?
		
		final Node n = gsGraph.getNode(vertexId);
		
		if (n == null)
			throw new WrongParametersException("unknown vertex "+vertexId);
		
		Map<String, Object> map = new HashMap<String, Object>(vertexAttributes2type.size());
		
		for (String attribute: vertexAttributes2type.keySet()) {
			Object value = n.getAttribute(attribute);
			if (value != null)
				map.put(attribute, value);
		}
		
		return map;
	}

	@Override
	public Map<String, Object> getEdgeAttributes(String vertexId) {
		// TODO edge attributtes !
		return Collections.EMPTY_MAP;
	}

	@Override
	public Map<String, Object> getGraphAttributes() {
		// TODO graph attributes !
		return Collections.EMPTY_MAP;
	}

	
	@Override
	public String getEdgeVertexFrom(String edgeId) {
		return gsGraph.getEdge(edgeId).getNode0().getId();
	}

	@Override
	public String getEdgeVertexTo(String edgeId) {
		return gsGraph.getEdge(edgeId).getNode1().getId();
	}

	@Override
	public boolean isEdgeDirected(String edgeId) {
		if (getDirectionality() == GraphDirectionality.DIRECTED)
			return true;
		else if (getDirectionality() == GraphDirectionality.UNDIRECTED)
			return false;
		else
			return gsGraph.getEdge(edgeId).isDirected();
	}

	@Override
	public Collection<String> getEdges() {
		Set<String> edgesIds = new HashSet<String>();
		for (Edge e : gsGraph.getEachEdge()) {
			edgesIds.add(e.getId());
		}
		return edgesIds;
	}


	@Override
	public boolean removeEdge(String id) {
		return gsGraph.removeEdge(id) != null;
	}	


	protected abstract AbstractGraphstreamBasedGraph getGraphImplementationForClone(String cloneId);

	@Override
	public IGenlabGraph clone(String cloneId) {
	
		final AbstractGraphstreamBasedGraph clone = getGraphImplementationForClone(cloneId);
		
		// --- copy genlab internal data ---
		
		// copy attributes
		// ... graph attributes
		for (String a: graphAttribute2type.keySet()) {
			Class type = graphAttribute2type.get(a);
			clone.declareGraphAttribute(a, type);
		}
		// .. node attributes
		for (String a: vertexAttributes2type.keySet()) {
			Class type = vertexAttributes2type.get(a);
			clone.declareVertexAttribute(a, type);
		} 
		// .. edge attributes
		for (String a: edgeAttributes2type.keySet()) {
			Class type = edgeAttributes2type.get(a);
			clone.declareEdgeAttribute(a, type);
		}
		
		// --- copy graphstream implementation ---
		
		// copy graph content
		GraphReplay replay = new GraphReplay("replay");
		replay.addSink(clone.gsGraph);
		replay.replay(gsGraph);
		replay.removeSink(clone.gsGraph);
		
		return clone;
	}	

	@Override
	public String toString() {
		
		StringBuffer sb = new StringBuffer();
		
		if (isMultiGraph())
			sb.append("multiplex");
		else 
			sb.append("simple");
		
		sb.append(" graph with ");
		sb.append(getVerticesCount()).append(" vertices and ").append(getEdgesCount()).append(" edges.\n");
		sb.append("\t- ");
		if (isVertexAttributed()) {
			sb.append("vertex attributes: ");
			sb.append(vertexAttributes2type.keySet());
		} else {
			sb.append("no vertex attributes");
		}
		sb.append("\n\t-");
		if (isEdgeAttributed()) {
			sb.append("edge attributes: ");
			sb.append(edgeAttributes2type.keySet());
		} else {
			sb.append("no edge attributes");
		}
		sb.append("\n");
		// TODO graph attributes
		return sb.toString();
	}
	
	@Override
	public boolean removeVertex(String id) {
		return gsGraph.removeNode(id) != null;
	}

	@Override
	public boolean containsEdge(String edgeId) {
		return gsGraph.getEdge(edgeId) != null;
	}

	@Override
	public Collection<String> getEdgesFrom(String vertexId) {
		Node n = gsGraph.getNode(vertexId);
		Collection<String> res = new ArrayList<String>(n.getOutDegree());
		for (Edge e : n.getLeavingEdgeSet()) {
			res.add(e.getId());
		}
		return res;
	}

	@Override
	public int getEdgesCountFrom(String vertexId) {
		Node n = gsGraph.getNode(vertexId);
		return n.getOutDegree();
	}

	@Override
	public Collection<String> getEdgesTo(String vertexId) {
		Node n = gsGraph.getNode(vertexId);
		Collection<String> res = new ArrayList<String>(n.getInDegree());
		for (Edge e : n.getEnteringEdgeSet()) {
			res.add(e.getId());
		}
		return res;
	}

	@Override
	public int getEdgesCountTo(String vertexId) {
		Node n = gsGraph.getNode(vertexId);
		return n.getInDegree();
	}

	@Override
	public int getNeighboorsCount(String vertexId) {
		Node n = gsGraph.getNode(vertexId);
		return n.getDegree();
		
	}

	@Override
	public String getEdgeOtherVertex(String edgeId, String vertex1) {
		Edge e = gsGraph.getEdge(edgeId);
		if (e.getNode0().getId().equals(vertex1))
			return e.getNode1().getId();
		else 
			return e.getNode0().getId();
	}


	@Override
	public boolean isEdgeLoop(String edgeId) {
		Edge e = gsGraph.getEdge(edgeId);
		return e.isLoop();
	}

	@Override
	public String getEdgeBetween(String nodeId1, String nodeId2) {
		Node n1 = gsGraph.getNode(nodeId1);
		return n1.getEdgeBetween(nodeId2).getId();
	}

	@Override
	public Collection<String> getEdgesBetween(String nodeId1, String nodeId2) {
		// TODO implement
		throw new NotImplementedException();
	}

	@Override
	public int getDegree(String nodeId) {
		Node n = gsGraph.getNode(nodeId);
		return n.getDegree();
	}

	@Override
	public int getInDegree(String nodeId) {
		Node n = gsGraph.getNode(nodeId);
		return n.getInDegree();
	}

	@Override
	public int getOutDegree(String nodeId) {
		Node n = gsGraph.getNode(nodeId);
		return n.getOutDegree();
	}
	

	@Override
	public Collection<String> getInNeighboors(String vertexId) {
		Node n = gsGraph.getNode(vertexId);
		Collection<String> res = new ArrayList<String>(n.getInDegree());
		for (Edge e: n.getEnteringEdgeSet()) {
			res.add(e.getNode0().getId());
		}
		return res;
	}

	@Override
	public Collection<String> getOutNeighboors(String vertexId) {
		Node n = gsGraph.getNode(vertexId);
		Collection<String> res = new ArrayList<String>(n.getOutDegree());
		for (Edge e: n.getLeavingEdgeSet()) {
			res.add(e.getNode1().getId());
		}
		return res;
	}


	@Override
	public Collection<String> getAllIncidentEdges(String vertexId) {
		Node n = gsGraph.getNode(vertexId);
		Collection<String> res = new ArrayList<String>(n.getDegree());
		for (Edge e: n.getEdgeSet()) {
			res.add(e.getNode0().getId());
		}
		return res;
	}


	

}
