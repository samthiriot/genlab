package genlab.bayesianinference.smile;

import genlab.bayesianinference.BNUtils;
import genlab.bayesianinference.BayesianNetworkException;
import genlab.bayesianinference.IBayesianNetwork;
import genlab.bayesianinference.IBayesianNode;
import genlab.bayesianinference.IConditionalProbabilityTable;
import genlab.core.commons.NotImplementedException;

import java.awt.Dimension;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import smile.Network;

/**
 * Encapsulates a SMILE network as a genlab bayesian network
 * 
 * @author Samuel Thiriot
 *
 */
public final class SMILEBayesianNetwork implements IBayesianNetwork {

	protected final Network smileNet;
	
	protected final Map<String,IBayesianNode> nodeId2genlabNode = new HashMap<String, IBayesianNode>();
	
	public SMILEBayesianNetwork(Network smileNet) {
		this.smileNet = smileNet;
		updateMappingFromSmile();
	}

	public SMILEBayesianNetwork() {
		this.smileNet = new Network();
	}
	
	public SMILEBayesianNetwork(IBayesianNetwork bn) {
		
		this.smileNet = new Network();
		
		// copy everything from one network to the other one
		BNUtils.copyContentTo(bn, this);
		
	}
	

	protected void updateMappingFromSmile() {
	
		for (String nodeId: smileNet.getAllNodeIds()) {
		
			IBayesianNode node = nodeId2genlabNode.get(nodeId);
			if (node == null) {
				node = new SMILENode(nodeId, this);
				nodeId2genlabNode.put(nodeId, node);
			}
		}
		
	}
	
	@Override
	public IBayesianNode getForID(String id) {
		
		return nodeId2genlabNode.get(id);
	}

	@Override
	public boolean containsEdge(String idFrom, String idTo) {

		int idToHandler = smileNet.getNode(idTo);
		
		int[] ids = smileNet.getChildren(idFrom);
		
		for (int id: ids) {
			if (id == idToHandler)
				return true;
		}
		
		return false;
		
	}

	@Override
	public boolean containsEdge(IBayesianNode nodeFrom, IBayesianNode nodeTo) {

		return containsEdge(nodeFrom.getID(), nodeTo.getID());
	}

	@Override
	public int getInDegree(IBayesianNode node) {
		return smileNet.getParents(node.getID()).length;
	}

	@Override
	public List<IBayesianNode> getIncoming(IBayesianNode node) {
		
		return getIncoming(node.getID());
		
	}

	@Override
	public List<IBayesianNode> getIncoming(String nodeId) {

		String[] parents = smileNet.getParentIds(nodeId);
		
		// quick exit ?
		if (parents.length == 0)
			return Collections.EMPTY_LIST;
		
		List<IBayesianNode> res = new LinkedList<IBayesianNode>();
		for (String c: parents)
			res.add(nodeId2genlabNode.get(c));
		
		return res;
	}

	@Override
	public int getOutDegree(IBayesianNode node) {
		
		return smileNet.getChildren(node.getID()).length;
	}

	@Override
	public List<IBayesianNode> getOutgoing(IBayesianNode node) {
		return getOutgoing(node.getID());
	}

	@Override
	public List<IBayesianNode> getOutgoing(String nodeId) {

		String[] children = smileNet.getParentIds(nodeId);
		
		// quick exit ?
		if (children.length == 0)
			return Collections.EMPTY_LIST;
		
		List<IBayesianNode> res = new LinkedList<IBayesianNode>();
		for (String c: children)
			res.add(nodeId2genlabNode.get(c));
		
		return res;
	}

	@Override
	public int getDegree(IBayesianNode node) {
		return getInDegree(node)+getOutDegree(node);
	}

	@Override
	public List<IBayesianNode> getNeighboors(IBayesianNode node) {
		
		List<IBayesianNode> res = getIncoming(node);
		res.addAll(getOutgoing(node));
		return res;
	}

	@Override
	public List<IBayesianNode> getNeighboors(String nodeId) {
		List<IBayesianNode> res = getIncoming(nodeId);
		res.addAll(getOutgoing(nodeId));
		return res;
	}

	@Override
	public void addEdge(IBayesianNode from, IBayesianNode to) {

		addEdge(from.getID(), to.getID());
	}

	@Override
	public void addEdge(String fromId, String toId) {

		smileNet.addArc(fromId, toId);
	}

	@Override
	public void removeEdge(IBayesianNode from, IBayesianNode to) {
		removeEdge(from.getID(), to.getID());
	}

	@Override
	public void removeEdge(String fromId, String toId) {
		smileNet.deleteArc(fromId, toId);
	}

	@Override
	public Collection<String> getAllNodesIds() {
				
		return nodeId2genlabNode.keySet();
	}

	@Override
	public Collection<IBayesianNode> getAllNodes() {
		return nodeId2genlabNode.values();
	}

	@Override
	public List<IBayesianNode> topologicalOrder() {

		// quick exit
		if (smileNet.getAllNodes().length == 0)
			return Collections.EMPTY_LIST;
		
		List<IBayesianNode> res = new LinkedList<IBayesianNode>();
		
		int currentNodeHandler = smileNet.getFirstNode();
		
		while (currentNodeHandler > -1) {
			
			// add this node
			res.add(nodeId2genlabNode.get(smileNet.getNodeId(currentNodeHandler)));
		
			currentNodeHandler = smileNet.getNextNode(currentNodeHandler);
		}
		
		return res;
	}

	@Override
	public IConditionalProbabilityTable createProbabilityTableForNode(IBayesianNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBayesianNode createNode(String id) {

		if (nodeId2genlabNode.containsKey(id))
			throw new BayesianNetworkException("a node named "+id+" already exists");
		
		// first create the SMILE one
		int nodeHandler = smileNet.addNode(Network.NodeType.Cpt, id);
		smileNet.setNodeName(id, id);
		
		// then create the wrapper node
		SMILENode node = new SMILENode(id, this);
				
		nodeId2genlabNode.put(id, node);
		
		return node;
	}

	@Override
	public IBayesianNode createNode(String id, List<String> domain) {
		
		if (nodeId2genlabNode.containsKey(id))
			throw new BayesianNetworkException("a node named "+id+" already exists");
		
		// first create the SMILE one
		int nodeHandler = smileNet.addNode(Network.NodeType.Cpt, id);
		
		
		// add user domains
		for (String d : domain) {
			smileNet.addOutcome(nodeHandler, d);
		}

		// clean the domain of any default domain
		for (int i=0; i<=smileNet.getOutcomeCount(nodeHandler)-domain.size(); i++) {
			smileNet.deleteOutcome(nodeHandler, 0);
		}
		
		// then create the wrapper node
		SMILENode node = new SMILENode(id, this);
				
		nodeId2genlabNode.put(id, node);
		
		
		return node;
	}

	@Override
	public void addNode(IBayesianNode node) {
		//nodeId2genlabNode.put(node.getID(), node);
		throw new NotImplementedException();
	}

	@Override
	public void deleteNode(IBayesianNode node) {
		deleteNode(node.getID());
	}
	

	@Override
	public void deleteNode(String id) {
		nodeId2genlabNode.remove(id);
		smileNet.deleteNode(id);
		
	}


	@Override
	public void identifierChanged(String newName, IBayesianNode node) {
		throw new NotImplementedException();
	}

	@Override
	public void clear() {
		smileNet.dispose();
		
	}

	@Override
	public boolean insertState(IBayesianNode node, int index, String domainStr) {
		throw new NotImplementedException();
	}

	@Override
	public void removeState(IBayesianNode node, int index) {
		smileNet.deleteOutcome(node.getID(), index);
	}

	@Override
	public Dimension getDimension() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containsNode(String id) {
		return nodeId2genlabNode.containsKey(id);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Bayesian network (SMILE implementation): ");
		sb.append(getAllNodesIds().size()).append(" nodes");
		return sb.toString();
	}

	
}
