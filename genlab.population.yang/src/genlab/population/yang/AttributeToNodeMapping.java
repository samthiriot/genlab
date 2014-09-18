package genlab.population.yang;

import genlab.bayesianinference.IBayesianNode;
import genlab.populations.bo.Attribute;

import java.util.HashMap;
import java.util.Map;

public class AttributeToNodeMapping {
	
	public final Map<Attribute,IBayesianNode> attribute2node;
	public final Map<IBayesianNode,Attribute> node2attribute;
	
	public AttributeToNodeMapping(int size) {
		attribute2node = new HashMap<Attribute, IBayesianNode>(size);
		node2attribute = new HashMap<IBayesianNode,Attribute>(size);

	}
	
	public void addMapping(Attribute attribute, IBayesianNode node) {
		attribute2node.put(attribute, node);
		node2attribute.put(node, attribute);
		// TODO detect errors
	}
	
}