package genlab.bayesianinference.smile;

import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.CertPathTrustManagerParameters;

import genlab.bayesianinference.BayesianNetworkException;
import genlab.bayesianinference.IBayesianNode;
import genlab.bayesianinference.IConditionalProbabilityTable;
import genlab.core.commons.NotImplementedException;

/**
 * Encapsulates a Smile node as a IBayesian node. 
 * In fact, everything is delegated to the network.
 * 
 * @author Samuel THiriot
 *
 */
public final class SMILENode implements IBayesianNode {

	protected final String name;
	protected final SMILEBayesianNetwork network;
	protected final SMILECPT cpt;
	
	/**
	 * Will be loaded on demand and kept
	 */
	private List<String> domain = null;
	
	
	public SMILENode(String name, SMILEBayesianNetwork network) {
		
		this.name = name;
		this.network = network;
		
		this.cpt = new SMILECPT(this);
	}


	@Override
	public int inDegree() {
		return network.smileNet.getParents(name).length;
	}

	@Override
	public int outDegree() {
		
		return network.smileNet.getChildren(name).length;
	}

	@Override
	public int degree() {
		return inDegree()+outDegree();
	}

	@Override
	public String getID() {
		return name;
	}

	@Override
	public String getLabel() {
		return network.smileNet.getNodeName(name);
	}

	@Override
	public List<String> getDomain() {
		if (domain == null) 
			domain = Arrays.asList(network.smileNet.getOutcomeIds(name));
		
		return domain;
	}

	@Override
	public int getIdxInDomain(String value) {
		
		List<String> dom = getDomain();
		for (int i=0; i<dom.size(); i++) {
			if (dom.get(i).equals(value))
				return i;
		}
		throw new BayesianNetworkException("value '"+value+"' not found in node '"+name+'"');
	}

	@Override
	public int getDomainSize() {
		return network.smileNet.getOutcomeCount(name);
	}

	@Override
	public IConditionalProbabilityTable getCPT() {
		
		return cpt;
	}

	@Override
	public void clearCPT() {
		// TODO ???
	}

	@Override
	public void setID(String id) {

		throw new NotImplementedException();
	}

	@Override
	public void setLabel(String id) {
		throw new NotImplementedException();
		
	}

	@Override
	public Point getLocation() {

		return null;
	}

	@Override
	public void setLocation(Point point) {
		// TODO Auto-generated method stub

	}

	@Override
	public void validate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCPT(IConditionalProbabilityTable cpt) {

		throw new NotImplementedException();
	}

}
