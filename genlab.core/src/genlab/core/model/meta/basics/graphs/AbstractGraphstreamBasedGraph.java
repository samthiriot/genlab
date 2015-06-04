package genlab.core.model.meta.basics.graphs;

import genlab.core.commons.NotImplementedException;
import genlab.core.commons.ProgramException;
import genlab.core.commons.WrongParametersException;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
public abstract class AbstractGraphstreamBasedGraph implements IGenlabGraph, Externalizable {

	protected Graph gsGraph;
	
	@SuppressWarnings("rawtypes")
	protected Map<String,Class> edgeAttributes2type = new HashMap<String, Class>();
	@SuppressWarnings("rawtypes")
	protected Map<String,Class> vertexAttributes2type = new HashMap<String, Class>();	
	@SuppressWarnings("rawtypes")
	protected Map<String,Class> graphAttribute2type = new HashMap<String, Class>();
	
	public static final String KEY_TECHNICAL_INFO_COUNT_CLONES = "core / count of graphs cloned";
	public static final String KEY_TECHNICAL_INFO_COUNT_CREATED = "core / count of graphs created";
	
	public boolean ignoreGraphAttributeErrors = false;
	
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
	
	public String addVertex() {
		
		int idx = gsGraph.getNodeCount();
		String id = Integer.toString(idx);
		while (containsVertex(id)) {
			id = Integer.toString(++idx);
		} 
		
		gsGraph.addNode(id);
		
		return id;
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
		if (!attributeType.isAssignableFrom(value.getClass())) {
			// special case of numbers
			if ( (attributeType.equals(Double.class)) && (value instanceof Number)) {
				Double valueDouble = ((Number)value).doubleValue();
				gsNode.setAttribute(attributeId, valueDouble);
			} else 
				throw new WrongParametersException("type "+attributeType.getSimpleName()+" is expected for attribute "+attributeId);
		}
		// finally add value
		gsNode.setAttribute(attributeId, value);
		 
	}



	@Override
	public void setVertexAttributes(String vertexId, Map<String, Object> values) {
		// ensure existence of the node
		Node gsNode = gsGraph.getNode(vertexId); 
		if (gsNode == null) {
			throw new WrongParametersException("no vertex "+vertexId+" declared");
		}
		// ensure compliance of parameters
		for (Entry<String,Object> entry: values.entrySet()) {

			Class attributeType = vertexAttributes2type.get(entry.getKey());
			if (attributeType == null) {
				throw new WrongParametersException("no vertex attribute "+entry.getKey()+" defined for this graph");
			}
			if (!attributeType.isInstance(entry.getValue())) {
				throw new WrongParametersException("type "+attributeType.getSimpleName()+" is expected for attribute "+entry.getKey());
			}

			// finally add value
			gsNode.setAttribute(entry.getKey(), entry.getValue());	
		}
		
	}

	@Override
	public void setEdgeAttributes(String edgeId, Map<String, Object> values) {
		
		// ensure existence of the node
		Edge gsEdge = gsGraph.getEdge(edgeId); 
		if (gsEdge == null) {
			throw new WrongParametersException("no edge  "+edgeId+" declared");
		}
		// ensure compliance of parameters
		for (Entry<String,Object> entry: values.entrySet()) {

			Class attributeType = edgeAttributes2type.get(entry.getKey());
			if (attributeType == null) {
				throw new WrongParametersException("no edge attribute "+entry.getKey()+" defined for this graph");
			}
			if (!attributeType.isInstance(entry.getValue())) {
				throw new WrongParametersException("type "+attributeType.getSimpleName()+" is expected for attribute "+entry.getKey());
			}

			// finally add value
			gsEdge.setAttribute(entry.getKey(), entry.getValue());	
		}
		
	}

	@Override
	public List<String> getVertices() {
		
		if (gsGraph.getNodeCount() == 0) // quick exit
			return Collections.EMPTY_LIST;
		
		List<String> res = new ArrayList<String>(gsGraph.getNodeCount());
		
		for (int i=0; i<gsGraph.getNodeCount(); i++) {
			res.add(gsGraph.getNode(i).getId());
		}
		
		return res;
				
	}

	@Override
	public void addEdge(String id, String vertexIdFrom, String vertexIdTo) {
		
		if (getDirectionality() == GraphDirectionality.MIXED)
			throw new WrongParametersException("As this graph is mixed, you have to precise for each new edge if it is directed or not.");
		
		addEdge(
				id, 
				vertexIdFrom, 
				vertexIdTo, 
				getDirectionality()==GraphDirectionality.DIRECTED
				);
	}

	@Override
	public String addEdge(String vertexIdFrom, String vertexIdTo, boolean directed) {
		String edgeId = vertexIdFrom+"_to_"+vertexIdTo;
		addEdge(
				edgeId, 
				vertexIdFrom, 
				vertexIdTo, 
				directed
				);
		return edgeId;
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
			if (!ignoreGraphAttributeErrors)
				throw new WrongParametersException("no graph attribute "+attributeId+" defined for this graph");
			else 
				return;
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
		
		if (vertexAttributes2type.isEmpty())
			return Collections.EMPTY_MAP;
		
		Map<String, Object> map = new HashMap<String, Object>(vertexAttributes2type.size());
		
		for (String attribute: vertexAttributes2type.keySet()) {
			Object value = n.getAttribute(attribute);
			if (value != null)
				map.put(attribute, value);
		}
		
		return map;
	}
	
	@Override
	public Object getVertexAttributeValue(String vertexId, String attributeName) {
		
		final Node n = gsGraph.getNode(vertexId);
		if (n == null)
			throw new WrongParametersException("unknown vertex "+vertexId);
		
		return n.getAttribute(attributeName);
		
	}

	@Override
	public Map<String, Object> getEdgeAttributes(String vertexId) {
		
		Edge gsEdge = gsGraph.getEdge(vertexId); 
		if (gsEdge == null) {
			throw new WrongParametersException("no edge "+vertexId+" declared");
		}
		
		final int count = gsEdge.getAttributeCount();
		if (count == 0)
			return Collections.EMPTY_MAP;
		
		HashMap<String,Object> res = new HashMap<String, Object>(gsEdge.getAttributeCount());
		Iterator<String> itKey = gsEdge.getAttributeKeyIterator();
		while (itKey.hasNext()) {
			String key = itKey.next();
			res.put(
					key, 
					gsEdge.getAttribute(key)
					);
		}
		return res;
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
	public List<String> getEdges() {
		
		List<String> edgesIds = new ArrayList<String>(gsGraph.getEdgeCount());
		for (int i=0; i<gsGraph.getEdgeCount(); i++) {
			edgesIds.add(gsGraph.getEdge(i).getId());
		}
		return edgesIds;
	}


	@Override
	public boolean removeEdge(String id) {
		try {
			return (gsGraph.removeEdge(id) != null);
		} catch (org.graphstream.graph.ElementNotFoundException e) {
			throw new WrongParametersException("this edge does not exists: "+id, e);
		}
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
	public boolean removeVertex(int index) {
		return gsGraph.removeNode(index) != null;
	}

	@Override
	public boolean removeEdge(int index) {
		return gsGraph.removeEdge(index) != null;
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
			res.add(e.getOpposite(n).getId());
		}
		return res;
	}

	@Override
	public Collection<String> getOutNeighboors(String vertexId) {
		Node n = gsGraph.getNode(vertexId);
		Collection<String> res = new ArrayList<String>(n.getOutDegree());
		for (Edge e: n.getLeavingEdgeSet()) {
			res.add(e.getOpposite(n).getId());
		}
		return res;
	}


	@Override
	public Collection<String> getAllIncidentEdges(String vertexId) {
		Node n = gsGraph.getNode(vertexId);
		Collection<String> res = new ArrayList<String>(n.getDegree());
		for (Edge e: n.getEdgeSet()) {
			res.add(e.getId());
		}
		return res;
	}

	@Override
	public void addAll(IGenlabGraph otherGraph, boolean copyGraphAttributes,
			boolean copyNodeAttributes, boolean copyEdgesAttributes) {
	
		// copy graph attributes
		if (copyGraphAttributes) {
			Map<String,Object> attributes = otherGraph.getGraphAttributes();
			for (String key: attributes.keySet()) {
				
				if (hasGraphAttribute(key))
					continue;
				
				Object value = attributes.get(key);
				setGraphAttribute(key, value);
				
			}
		}
		
		// copy node attributes
		if (copyNodeAttributes) {
			Map<String,Class> attributes = otherGraph.getDeclaredVertexAttributesAndTypes();
			for (String key: attributes.keySet()) {
				
				if (hasVertexAttribute(key))
					continue;
				
				Class type = attributes.get(key);
				declareVertexAttribute(key, type);
			}
		}
		// copy edge attributes
		if (copyEdgesAttributes) {
			Map<String,Class> attributes = otherGraph.getDeclaredEdgeAttributesAndTypes();
			for (String key: attributes.keySet()) {
				
				if (hasEdgeAttribute(key))
					continue;
				
				Class type = attributes.get(key);
				declareEdgeAttribute(key, type);
			}
		}
		
		// copy nodes
		Map<String,String> other2thisId = new HashMap<String, String>((int)otherGraph.getVerticesCount());
		for (String nodeId: otherGraph.getVertices()) {
			String createdId = addVertex();
			other2thisId.put(
					nodeId, 
					createdId
					);
			if (copyNodeAttributes) {
				for (Map.Entry<String,Object> attValue : otherGraph.getVertexAttributes(nodeId).entrySet()) {
					setVertexAttribute(createdId, attValue.getKey(), attValue.getValue());
				}
			}
		}
		
		// copy edges
		for (String edgeId: otherGraph.getEdges()) {
			
			String nodeIdFrom = other2thisId.get(otherGraph.getEdgeVertexFrom(edgeId));
			String nodeIdTo = other2thisId.get(otherGraph.getEdgeVertexTo(edgeId));
			
			String createdEdgeId = addEdge(nodeIdFrom, nodeIdTo, otherGraph.isEdgeDirected(edgeId));
				
			if (copyEdgesAttributes) {
				for (Map.Entry<String,Object> attValue : otherGraph.getEdgeAttributes(edgeId).entrySet()) {
					setEdgeAttribute(createdEdgeId, attValue.getKey(), attValue.getValue());
				}
			}
		}
		
	}

	@Override
	public String getVertex(int index) {

		return gsGraph.getNode(index).getId();
	}

	@Override
	public String getEdge(int index) {
		
		return gsGraph.getEdge(index).getId();
	}

	
	public AbstractGraphstreamBasedGraph() {
		gsGraph = null;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(gsGraph);
		out.writeObject(graphAttribute2type);
		out.writeObject(edgeAttributes2type);
		out.writeObject(vertexAttributes2type);
		
	}
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {

		gsGraph = (Graph) in.readObject();
		graphAttribute2type = (Map<String, Class>) in.readObject();
		edgeAttributes2type = (Map<String, Class>) in.readObject();
		vertexAttributes2type = (Map<String, Class>) in.readObject();
	}

}
