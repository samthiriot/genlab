package genlab.jung.utils;

import java.util.ArrayList;
import java.util.Collection;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import genlab.core.commons.NotImplementedException;
import genlab.core.commons.WrongParametersException;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

/**
 * A JUNG graph implementation which is based on the genlab graph
 * 
 * @author Samuel Thiriot
 *
 */
public class JungGenlabGraphReadonly implements Graph<String,String> {

	final protected IGenlabGraph glGraph;
	
	public JungGenlabGraphReadonly(IGenlabGraph glGraph) {

		this.glGraph = glGraph;
		
		// TODO warn if mixed ?
	}

	@Override
	public boolean addEdge(String arg0, Collection<? extends String> arg1) {
		throw new NotImplementedException();
	}

	@Override
	public boolean addEdge(String arg0, Collection<? extends String> arg1,
			EdgeType arg2) {
		throw new NotImplementedException();

	}

	@Override
	public boolean addVertex(String arg0) {
		throw new WrongParametersException();
	}

	@Override
	public boolean containsEdge(String arg0) {
		return glGraph.containsEdge(arg0);
	}

	@Override
	public boolean containsVertex(String arg0) {
		return glGraph.containsVertex(arg0);
	}

	@Override
	public int degree(String arg0) {
		return glGraph.getDegree(arg0);
	}

	@Override
	public String findEdge(String arg0, String arg1) {
		return glGraph.getEdgeBetween(arg0, arg1);
	}

	@Override
	public Collection<String> findEdgeSet(String arg0, String arg1) {
		return glGraph.getEdgesBetween(arg0, arg1);
	}

	@Override
	public EdgeType getDefaultEdgeType() {
		if (glGraph.getDirectionality() == GraphDirectionality.UNDIRECTED)
			return EdgeType.UNDIRECTED;
		else 
			return EdgeType.DIRECTED;
	}

	@Override
	public int getEdgeCount() {
		return (int)glGraph.getEdgesCount();
	}

	@Override
	public int getEdgeCount(EdgeType arg0) {
		return (int)glGraph.getEdgesCount();
	}

	@Override
	public EdgeType getEdgeType(String arg0) {

		if (glGraph.isEdgeDirected(arg0)) {
			return EdgeType.DIRECTED;
		} else {
			return EdgeType.UNDIRECTED;
		}
	}

	@Override
	public Collection<String> getEdges() {
		
		return glGraph.getEdges();
	}

	@Override
	public Collection<String> getEdges(EdgeType arg0) {
		return glGraph.getEdges();
	}

	@Override
	public int getIncidentCount(String arg0) {
		if (glGraph.isEdgeLoop(arg0))
			return 1;
		else
			return 2;
	}

	@Override
	public Collection<String> getIncidentEdges(String arg0) {
		return glGraph.getAllIncidentEdges(arg0);
	}

	@Override
	public Collection<String> getIncidentVertices(String arg0) {
		Collection<String> res = new ArrayList<String>(2);
		res.add(glGraph.getEdgeVertexFrom(arg0));
		res.add(glGraph.getEdgeVertexTo(arg0));
		return res;
	}

	@Override
	public int getNeighborCount(String arg0) {
		return glGraph.getNeighboorsCount(arg0);
	}

	@Override
	public Collection<String> getNeighbors(String arg0) {
		return glGraph.getNeighboors(arg0);
	}

	@Override
	public int getVertexCount() {
		return (int)glGraph.getVerticesCount();
	}

	@Override
	public Collection<String> getVertices() {
		return glGraph.getVertices();
	}

	@Override
	public boolean isIncident(String arg0, String arg1) {
		return glGraph.getEdgeVertexFrom(arg0).equals(arg1) || glGraph.getEdgeVertexTo(arg0).equals(arg1);
	}

	@Override
	public boolean isNeighbor(String arg0, String arg1) {
		return glGraph.containsEdge(arg0, arg1);
	}

	@Override
	public boolean removeEdge(String arg0) {
		throw new WrongParametersException();
	}

	@Override
	public boolean removeVertex(String arg0) {
		throw new WrongParametersException();
	}

	@Override
	public boolean addEdge(String arg0, String arg1, String arg2) {
		throw new WrongParametersException();

	}

	@Override
	public boolean addEdge(String arg0, String arg1, String arg2, EdgeType arg3) {
		throw new WrongParametersException();

	}

	@Override
	public String getDest(String arg0) {
		if (glGraph.isEdgeDirected(arg0))
			return glGraph.getEdgeVertexTo(arg0);
		else
			return null;
	}

	@Override
	public Pair<String> getEndpoints(String arg0) {
		return new Pair<String>(glGraph.getEdgeVertexFrom(arg0), glGraph.getEdgeVertexTo(arg0));
	}

	@Override
	public Collection<String> getInEdges(String arg0) {
		
		return glGraph.getEdgesTo(arg0);
	}

	@Override
	public String getOpposite(String arg0, String arg1) {
		return glGraph.getEdgeOtherVertex(arg0, arg1);
	}

	@Override
	public Collection<String> getOutEdges(String arg0) {
		return glGraph.getEdgesFrom(arg0);
	}

	@Override
	public int getPredecessorCount(String arg0) {
		return glGraph.getEdgesCountTo(arg0);
	}

	@Override
	public Collection<String> getPredecessors(String arg0) {
		return glGraph.getInNeighboors(arg0);
	}

	@Override
	public String getSource(String arg0) {
		if (glGraph.isEdgeDirected(arg0))
			return glGraph.getEdgeVertexFrom(arg0);
		else
			return null;
	}

	@Override
	public int getSuccessorCount(String arg0) {
		return glGraph.getEdgesCountFrom(arg0);
	}

	@Override
	public Collection<String> getSuccessors(String arg0) {
		return glGraph.getOutNeighboors(arg0);
	}

	@Override
	public int inDegree(String arg0) {
		return glGraph.getInDegree(arg0);
	}

	@Override
	public boolean isDest(String arg0, String arg1) {
		return glGraph.getEdgeVertexTo(arg0).equals(arg1);
	}

	@Override
	public boolean isPredecessor(String arg0, String arg1) {
		return glGraph.containsEdge(arg0, arg1);
	}

	@Override
	public boolean isSource(String arg0, String arg1) {
		return glGraph.getEdgeVertexFrom(arg0).equals(arg1);
	}

	@Override
	public boolean isSuccessor(String arg0, String arg1) {
		return glGraph.containsEdge(arg1, arg0);
	}

	@Override
	public int outDegree(String arg0) {
		return glGraph.getOutDegree(arg0);
	}

}
