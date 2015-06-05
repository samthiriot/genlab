package genlab.core.model.meta.basics.graphs;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IGenlabGraph {

	public String getGraphId();
	
	public long getVerticesCount();
	public long getEdgesCount();
	
	public void declareGraphAttribute(String attributeId, Class type);
	public boolean hasGraphAttribute(String attribute);

	public Collection<String> getDeclaredGraphAttributes();
	public Map<String,Class> getDeclaredGraphAttributesAndTypes();
	public void setGraphAttribute(String attributeId, Object value);
	public Object getGraphAttribute(String attributeId);
	

	public void declareVertexAttribute(String attributeId, Class type);
	public boolean hasVertexAttribute(String attributeId);

	public void declareEdgeAttribute(String attributeId, Class type);
	public boolean hasEdgeAttribute(String attributeId);
	public Object getEdgeAttributeValue(String vertexId, String attributeId);

	public Collection<String> getDeclaredVertexAttributes();
	public Map<String,Class> getDeclaredVertexAttributesAndTypes();

	public Collection<String> getDeclaredEdgeAttributes();
	public Map<String,Class> getDeclaredEdgeAttributesAndTypes();

	public Map<String,Object> getVertexAttributes(String vertexId);
	public Map<String,Object> getEdgeAttributes(String vertexId);
	public Map<String,Object> getGraphAttributes();
	public Object getVertexAttributeValue(String vertexId, String attributeName);


	public String addVertex();
	public void addVertex(String id);
	public boolean removeVertex(String id);
	public String getVertex(int index);
	public boolean removeVertex(int index);

	
	public void setVertexAttribute(String vertexId, String attributeId, Object value);
	public void setVertexAttributes(String vertexId, Map<String,Object> values);

	
	public List<String> getVertices();
	

	
	public void addEdge(String id, String vertexIdFrom, String vertexIdTo);
	
	public String addEdge(String vertexIdFrom, String vertexIdTo, boolean directed);

	public void addEdge(String id, String vertexIdFrom, String vertexIdTo, boolean directed);
	public void setEdgeAttribute(String edgeId, String attributeId, Object value);
	public void setEdgeAttributes(String edgeId, Map<String,Object> values);

	public boolean removeEdge(String id);
	public boolean removeEdge(int index);
	public boolean containsEdge(String vertexFrom, String vertexTo);
	public boolean containsEdge(String edgeId);
	public String getEdge(int index);
	
	/**
	 * Returns true if one can create several edges between the 
	 * two same edges
	 * @return
	 */
	public boolean isMultiGraph();
	
	/**
	 * Get the directionnality of the graph (directed, undirected, mixed)
	 * @return
	 */
	public GraphDirectionality getDirectionality();
	
	/**
	 * Returns true if attributes are allowed / declared for nodes
	 * @return
	 */
	public boolean isVertexAttributed();
	
	/**
	 * Returns true if attributes are allowed / declared for nodes
	 * @return
	 */
	public boolean isEdgeAttributed();
	
	
	public boolean containsVertex(String vertexId);
	
	public Collection<String> getAllIncidentEdges(String vertexId);

	public Collection<String> getEdgesFrom(String vertexId);
	public int getEdgesCountFrom(String vertexId);

	public Collection<String> getEdgesTo(String vertexId);
	public int getEdgesCountTo(String vertexId);

	public Collection<String> getNeighboors(String vertexId);
	public int getNeighboorsCount(String vertexId);

	public Collection<String> getInNeighboors(String vertexId);
	public Collection<String> getOutNeighboors(String vertexId);

	public String getEdgeOtherVertex(String edgeId, String vertex1);

	public String getEdgeVertexFrom(String edgeId);
	public String getEdgeVertexTo(String edgeId);
	public boolean isEdgeDirected(String edgeId);

	public boolean isEdgeLoop(String edgeId);

	public String getEdgeBetween(String nodeId1, String nodeId2);
	public Collection<String> getEdgesBetween(String nodeId1, String nodeId2);

	
	public int getDegree(String nodeId);
	public int getInDegree(String nodeId);
	public int getOutDegree(String nodeId);


	/**
	 * If multigraph, will return the set of defined edge types;
	 * else always "edge"
	 * @return
	 */
	public Collection<String> getEdgeTypes();

	/**
	 * returns the edge type of the edge
	 * @param edgeId
	 * @return
	 */
	public String getEdgeType(String edgeId);

	
	public List<String> getEdges();
	
	/**
	 * Returns a deep clone where vertices, edges, attributes values were all duplicated.
	 * @return
	 */
	public IGenlabGraph clone(String cloneId);

	
	public void addAll(IGenlabGraph otherGraph, boolean copyGraphAttributes, boolean copyNodeAttributes, boolean copyEdgesAttributes);




	
}
