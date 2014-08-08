package genlab.bayesianinference.implementations.inflib;

import edu.ucla.belief.FiniteVariable;
import edu.ucla.belief.Variable;
import edu.ucla.belief.io.NetworkIO;
import edu.ucla.belief.io.hugin.HuginNetImpl;
import edu.ucla.belief.io.hugin.HuginNodeImpl;
import genlab.bayesianinference.BayesianNetworkException;
import genlab.bayesianinference.IBayesianNetwork;
import genlab.bayesianinference.IBayesianNode;
import genlab.bayesianinference.IConditionalProbabilityTable;
import genlab.core.usermachineinteraction.GLLogger;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The Inflib implementation of a Bayesian network.
 * Also manages the static instances of files (else its creating issues for inflib).
 * 
 * @author Samuel Thiriot
 */
public class InflibBaysianNetwork implements IBayesianNetwork {

	protected static Map<String, HuginNetImpl> filename2HuginNetImpl = new HashMap<String, HuginNetImpl>();
	protected static Map<HuginNetImpl, String> huginNetImpl2filename = new HashMap<HuginNetImpl,String>();

	/**
	 * THe locker to ensure two inflib IO are not going to be concurrent.
	 */
	private static Object syncNetworkIO = new Object();

	protected HuginNetImpl huginNetwork;
	
	/**
	 * creates an empty network
	 */
	public InflibBaysianNetwork () {
		this.huginNetwork = new HuginNetImpl();
	}
	
	public InflibBaysianNetwork (HuginNetImpl huginNetImpl) {
		this.huginNetwork = huginNetImpl;
		getAllNodes();	// inits internal structures
		
	}	

	
	@Override
	public boolean containsEdge(String idFrom, String idTo) {
		return containsEdge(getForID(idFrom), getForID(idTo));
	}

	@Override
	public boolean containsEdge(IBayesianNode nodeFrom, IBayesianNode nodeTo) {		
		return huginNetwork.containsEdge(((InflibNode)nodeFrom).huginNode, ((InflibNode)nodeTo).huginNode);
	}

	@Override
	public IConditionalProbabilityTable createProbabilityTableForNode(IBayesianNode node) {
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IBayesianNode> getAllNodes() {
		List<IBayesianNode> res = new LinkedList<IBayesianNode>();
		Set<FiniteVariable> all = huginNetwork.vertices();
		for (FiniteVariable c : all)
			res.add(getOrCreateNodeForVariable(c));
		return res;
	}

	@Override
	public Collection<String> getAllNodesIds() {
		List<String> res = new LinkedList<String>();
		Set<FiniteVariable> all = huginNetwork.vertices();
		for (FiniteVariable c : all)
			res.add(c.getID());
		return res;
	}

	private final IBayesianNode getOrCreateNodeForVariable(Variable var) {
		
		IBayesianNode res = (IBayesianNode)var.getUserObject();
		if (res == null) {
			res = new InflibNode(this, (HuginNodeImpl)var);
			var.setUserObject(res);
		}
		return res;
	}
	
	@Override
	public IBayesianNode getForID(String id) {
		
		Variable var = huginNetwork.forID(id);
		if (var == null)
			return null;
		return getOrCreateNodeForVariable(var);
		
	}

	@Override
	public List<IBayesianNode> getIncoming(IBayesianNode node) {
		List<IBayesianNode> res = new LinkedList<IBayesianNode>();
		Set<FiniteVariable> in = huginNetwork.inComing(((InflibNode)node).huginNode);
		for (FiniteVariable c : in)
			res.add(getOrCreateNodeForVariable(c));
		return res;
	}

	@Override
	public List<IBayesianNode> getIncoming(String nodeId) {
		return getIncoming(getForID(nodeId));
	}

	@Override
	public List<IBayesianNode> getNeighboors(IBayesianNode node) {
		List<IBayesianNode> res = new LinkedList<IBayesianNode>();
		res.addAll(getIncoming(node));
		res.addAll(getOutgoing(node));
		return res;
	}

	@Override
	public List<IBayesianNode> getNeighboors(String nodeId) {
		return getNeighboors(getForID(nodeId));
	}

	@Override
	public List<IBayesianNode> getOutgoing(IBayesianNode node) {
		List<IBayesianNode> res = new LinkedList<IBayesianNode>();
		Set<FiniteVariable> in = huginNetwork.outGoing(((InflibNode)node).huginNode);
		for (FiniteVariable c : in)
			res.add(getOrCreateNodeForVariable(c));
		return res;
	}

	@Override
	public List<IBayesianNode> getOutgoing(String nodeId) {
		return getOutgoing(getForID(nodeId));
	}

	@Override
	public int getDegree(IBayesianNode node) {
		return huginNetwork.degree(((InflibNode)node).huginNode);
	}

	@Override
	public int getInDegree(IBayesianNode node) {
		return huginNetwork.inDegree(((InflibNode)node).huginNode);
	}

	@Override
	public int getOutDegree(IBayesianNode node) {
		return huginNetwork.outDegree(((InflibNode)node).huginNode);
	}

	@Override
	public void addNode(IBayesianNode node) {
		boolean created = huginNetwork.addVariable(((InflibNode)node).huginNode, true);
		/*if (!created)
			throw new RuntimeException("error returned by the Inflib library while attempting to add node "+node);
		*/
	}
	
	@Override
	public IBayesianNode createNode(String id) {
		
		if (huginNetwork.forID(id) != null)
			throw new RuntimeException("a node named \""+id+"\" already exists");
		
		InflibNode res = new InflibNode(this, id);
		addNode(res);
		return res;
	}

	@Override
	public IBayesianNode createNode(String id, List<String> domain) {
		
		if (huginNetwork.forID(id) != null)
			throw new RuntimeException("a node named \""+id+"\" already exists");
		if (domain == null)
			throw new RuntimeException("null domain !");
		
		
		//if (domain.size() < 1)
		//	throw new RuntimeException("domain should at least contain one state");
		
		InflibNode res = new InflibNode(this, id, domain);
		addNode(res);
		return res;
	}

	@Override
	public void addEdge(IBayesianNode from, IBayesianNode to) {
		if (from.equals(to))
			throw new RuntimeException("loops are not allowed");
		
		boolean added = huginNetwork.addEdge(
				((InflibNode)from).huginNode,
				((InflibNode)to).huginNode
				);
		if (!added)
			throw new RuntimeException("Inflib refused to add the edge for unknown reasons");
		else
			((InflibNode)to).huginNode.resetSpecifiedDimension();
	}

	@Override
	public void addEdge(String fromId, String toId) {
		addEdge(getForID(fromId), getForID(toId));
	}

	@Override
	public void removeEdge(IBayesianNode from, IBayesianNode to) {
		
		boolean removed = huginNetwork.removeEdge(
				((InflibNode)from).huginNode,
				((InflibNode)to).huginNode
				);
		if (!removed)
			throw new RuntimeException("Inflib refused to remove the edge for unknown reasons");
	}

	@Override
	public void removeEdge(String fromId, String toId) {
		removeEdge(getForID(fromId), getForID(toId));
	}

	@Override
	public void identifierChanged(String newName, IBayesianNode node) {
		huginNetwork.identifierChanged(newName, ((InflibNode)node).huginNode);
	}

	@Override
	public List<IBayesianNode> topologicalOrder() {
		List<IBayesianNode> res = new LinkedList<IBayesianNode>();
		List<FiniteVariable> all = huginNetwork.topologicalOrder();
		for (FiniteVariable c : all)
			res.add(getOrCreateNodeForVariable(c));
		return res;
	}

	@Override
	public void clear() {
		huginNetwork.clear();
	}

	@Override
	public boolean insertState(IBayesianNode node, int index, String domainStr) {
		
		//huginNetwork.setAutoCPTInvalidation(true);
		
		boolean res = huginNetwork.insertState(
				((InflibNode)node).huginNode, 
				index, 
				domainStr
				);
		
		FiniteVariable variable = ((InflibNode)node).huginNode;

		
		return res;
	}

	@Override
	public void removeState(IBayesianNode node, int index) {
		
		huginNetwork.removeState(
				((InflibNode)node).huginNode, 
				index
				);
		
	}
	
	public Dimension getDimension() {
		
		if (huginNetwork.vertices().isEmpty())
			return new Dimension(0,0);
		
		int xMin = Integer.MAX_VALUE;
		int xMax = Integer.MIN_VALUE;
		int yMin = Integer.MAX_VALUE;
		int yMax = Integer.MIN_VALUE;
		
		Set<HuginNodeImpl> all = huginNetwork.vertices();
		for (HuginNodeImpl node : all) {
			Point location = node.getLocation(null);
			xMin = Math.min(xMin, location.x);
			xMax = Math.max(xMax, location.x);
			yMin = Math.min(yMin, location.y);
			yMax = Math.max(yMax, location.y);
		}
		
		return new Dimension(
				Math.abs(xMin-xMax), 
				Math.abs(yMin-yMax)
				);
	}

	@Override
	public void deleteNode(IBayesianNode node) {
		boolean res = huginNetwork.removeVariable(((InflibNode)node).huginNode);
		if (!res)
			throw new BayesianNetworkException("unable to delete node "+node+" from the network");
		
	}

	/**
	 * Loads an inflib network from the file passed as parameter. Will ensure the same 
	 * file is not opened several times (else inflib will have a problem). So keep in memory
	 * every network will be a singleton: for a filename, only one instanciation of the corresponding 
	 * instanciation.
	 * @param filename2
	 * @param createIfNotFound
	 * @return
	 */
	public static HuginNetImpl loadFromFile(String filename2, boolean createIfNotFound) {
		
		String filename = null;
		try {
			File f = new File(filename2);
			filename = f.getCanonicalPath();
			
		} catch (IOException e1) {
			
			
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		synchronized (filename2HuginNetImpl) {
	
			HuginNetImpl res = null;
	
			
			res = filename2HuginNetImpl.get(filename);
					
			
			GLLogger.debugTech("loading "+filename, InflibBaysianNetwork.class);
			//bn = new HuginReader(new FileInputStream(fileBN));
			
			try {
				
				if (res != null) {
					// already exists, return
					GLLogger.debugTech("BN already loaded: "+filename, InflibBaysianNetwork.class);
					//return res;
					
					GLLogger.debugTech("clears previous: "+filename, InflibBaysianNetwork.class);
					//res.
					res.clear();
					
				} //else {
				
				HuginNetImpl res2 = null;
				res2 = (HuginNetImpl)NetworkIO.read(filename);
				if (res2 == null) {
					
					if (createIfNotFound) {
						GLLogger.infoTech("unable to load a Bayesian network from the file "+filename+"; attempting to create one", InflibBaysianNetwork.class);
						createEmptyFile(filename);
						res2 = (HuginNetImpl)NetworkIO.read(filename);
					} else {
						GLLogger.errorTech("Retrieved null while loading BN... !", InflibBaysianNetwork.class);
						throw new RuntimeException("Retrieved null while loading BN... !");
					}
					// try to return previous instance... 
					//return (HuginNetImpl)res.deepClone(); 
				}
				res = res2;
				
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Unable to parse BN "+filename, e);
			}
			
			filename2HuginNetImpl.put(filename, res);
			huginNetImpl2filename.put(res, filename);
			return res;
		}
		
	}
	

	
	public static HuginNetImpl createEmptyFile(String filename2) {
		HuginNetImpl bn = new HuginNetImpl();
		
		saveHuginToFile(bn, filename2);
		
		return bn;
	}
	
	
	protected static void saveHuginToFile(HuginNetImpl net, String filename) {
		
		HuginNetImpl net2 = filename2HuginNetImpl.get(filename);
		
		if (net2 != net && net2 != null)
			GLLogger.warnTech("Asked to close a file that wasn't loaded from this function; arg.", InflibBaysianNetwork.class);
		
		GLLogger.debugTech("saving the bn into "+filename, InflibBaysianNetwork.class);
		
		try {
			FileWriter fileWriter = new FileWriter(filename);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			//logger.debug("proba: "+bnToSave.checkValidProbabilities());
			//NetworkIO.normalizeEnumProperties(bnAppariementAgents);
			synchronized (syncNetworkIO) {
				NetworkIO.writeNetwork(net, printWriter);
			}
			/*
			for (int i=0; i<4; i++) {
				try { 
					NetworkIO.writeNetwork(net, printWriter);
					break;
				} catch (ConcurrentModificationException e) {
					loggerLoader.warn("concurrent exception, attempting to retry", e);
					Thread.yield();
				}
			}*/
			printWriter.flush();
			fileWriter.flush();
			fileWriter.close();
			GLLogger.debugTech("Bayesian network saved in "+filename, InflibBaysianNetwork.class);
		} catch (IOException e) {
			throw new RuntimeException("Error while trying to save a Bayesian network into file "+filename, e);
		}
		
		
	}

	@Override
	public boolean containsNode(String id) {
		return huginNetwork.forID(id) != null;
	}

	@Override
	public void deleteNode(String id) {
		huginNetwork.removeVertex(id);
	}
	
}
