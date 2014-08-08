package genlab.bayesianinference.implementations.inflib;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.ucla.belief.io.dsl.DSLNodeType;
import edu.ucla.belief.io.hugin.HuginNodeImpl;
import edu.ucla.util.InferenceValidProperty;
import genlab.bayesianinference.IBayesianNode;
import genlab.bayesianinference.IConditionalProbabilityTable;

/**
 * The inflib implementation of a Bayesian node
 * @author Samuel Thiriot
 *
 */
public final class InflibNode implements IBayesianNode {

	protected InflibBaysianNetwork network;
	protected HuginNodeImpl huginNode;
	private InflibConditionalProbabilityTable cpt = null;
	
	
	public InflibNode(InflibBaysianNetwork network, String id) {

		// TODO check syntax for id ???
		
		this.network = network;
		
		{
			HashMap<String, String> hashmap = new HashMap<String, String>();
			hashmap.put(HuginNodeImpl.KEY_HUGIN_LABEL, id);
			this.huginNode = new HuginNodeImpl(id, getDefaultDomain(), hashmap);
		}
		
		huginNode.setUserObject(this);		
	}
	
	public InflibNode(InflibBaysianNetwork network, String id, List<String> domain) {
		this.network = network;
		
		{
			HashMap<String, String> hashmap = new HashMap<String, String>();
			hashmap.put(HuginNodeImpl.KEY_HUGIN_LABEL, id);
			this.huginNode = new HuginNodeImpl(id, domain, hashmap);
		}
		huginNode.setUserObject(this);		
	}
	
	public InflibNode(InflibBaysianNetwork network, HuginNodeImpl huginNode) {
		this.network = network;
		this.huginNode = huginNode;
		huginNode.setUserObject(this);		
	}
	
	private List<String> getDefaultDomain() {
		List<String> res = new LinkedList<String>();
		res.add("value0");
		res.add("value1");
		return res;
	}
	
	@Override
	public int degree() {
		return network.getDegree(this);
	}

	private InflibConditionalProbabilityTable createCPT() {
		return new InflibConditionalProbabilityTable (huginNode.getCPTShell());
	}
	
	@Override
	public IConditionalProbabilityTable getCPT() {
		if (cpt == null)
			cpt = createCPT();
		return cpt;
	}

	@Override
	public List<String> getDomain() {
		return huginNode.instances();
	}

	@Override
	public int getDomainSize() {
		return huginNode.size();
	}

	@Override
	public int inDegree() {
		return network.getInDegree(this);
	}

	@Override
	public int outDegree() {
		return network.getOutDegree(this);
	}

	@Override
	public String toString() {
		return huginNode.getID();
	}

	@Override
	public String getID() {
		return huginNode.getID();
	}

	@Override
	public void clearCPT() {
		this.cpt = null;	
	}

	@Override
	public String getLabel() {
		return huginNode.getLabel();
	}

	@Override
	public void setID(String id) {
		huginNode.setID(id);
	}

	@Override
	public void setLabel(String id) {
		huginNode.setLabel(id);
	}

	@Override
	public Point getLocation() {
		return huginNode.getLocation(null);
	}

	@Override
	public void validate() {
		huginNode.setProperty(InferenceValidProperty.PROPERTY, InferenceValidProperty.PROPERTY.TRUE);
	}

	@Override
	public void setCPT(IConditionalProbabilityTable cpt) {
		InflibConditionalProbabilityTable cptCasted = (InflibConditionalProbabilityTable) cpt;
		huginNode.setCPTShell(DSLNodeType.CPT, cptCasted.cpt);
		this.cpt = cptCasted;
	}

	@Override
	public void setLocation(Point point) {
		huginNode.setLocation(point);
	}

	@Override
	public int getIdxInDomain(String value) {
		return huginNode.index(value);
		//return ((VariableInstance)huginNode.instance(value)).getIndex();
	}
	
}
