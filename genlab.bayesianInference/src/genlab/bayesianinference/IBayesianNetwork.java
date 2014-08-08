package genlab.bayesianinference;

import java.awt.Dimension;
import java.util.Collection;
import java.util.List;

public interface IBayesianNetwork {

	/**
	 * returns the corresponding node, or null
	 * if not found
	 * @param id
	 * @return
	 */
	public IBayesianNode getForID(String id);

	public boolean containsNode(String id);

	public boolean containsEdge(String idFrom, String idTo);
	
	public boolean containsEdge(IBayesianNode nodeFrom, IBayesianNode nodeTo);
	
	public int getInDegree(IBayesianNode node);
	public List<IBayesianNode> getIncoming(IBayesianNode node);
	public List<IBayesianNode> getIncoming(String nodeId);
	
	public int getOutDegree(IBayesianNode node);
	public List<IBayesianNode> getOutgoing(IBayesianNode node);
	public List<IBayesianNode> getOutgoing(String nodeId);
	

	public int getDegree(IBayesianNode node);
	public List<IBayesianNode> getNeighboors(IBayesianNode node);
	public List<IBayesianNode> getNeighboors(String nodeId);
	
	//public Map<String, String> getEdges();
	
	public void addEdge(IBayesianNode from, IBayesianNode to);
	public void addEdge(String fromId, String toId);
	public void removeEdge(IBayesianNode from, IBayesianNode to);
	public void removeEdge(String fromId, String toId);
	
	
	public Collection<String> getAllNodesIds();
	
	public Collection<IBayesianNode> getAllNodes();
	
	public List<IBayesianNode> topologicalOrder();
	
	/**
	 * creates an empty probability table for this node given 
	 * its domain and the domain of its parents. 
	 * @param node
	 * @return
	 */
	public IConditionalProbabilityTable createProbabilityTableForNode(IBayesianNode node);
	
	public IBayesianNode createNode(String id);
	public IBayesianNode createNode(String id, List<String> domain);
	
	public void addNode(IBayesianNode node);
	
	public void deleteNode(IBayesianNode node);
	public void deleteNode(String id);
	
	public void identifierChanged(String newName, IBayesianNode node);

	/**
	 * Clears all internal data in order to facilitate
	 * memory freeing. 
	 */
	public void clear();

	public boolean insertState(IBayesianNode node, int index, String domainStr);

	public void removeState(IBayesianNode node, int index);
	
	/**
	 * Returns the display dimension (bounds) of this network
	 * @return
	 */
	public Dimension getDimension() ;
	
}
