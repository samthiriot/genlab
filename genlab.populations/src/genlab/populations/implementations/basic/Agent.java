package genlab.populations.implementations.basic;

import genlab.populations.bo.Attribute;
import genlab.populations.bo.IAgent;
import genlab.populations.bo.IAgentType;

public class Agent implements IAgent {

	protected final Object id;
	protected final IAgentType agentType;
	protected final Object[] attributeValues;
	
	public Agent(Object id, IAgentType agentType, Object[] attributeValues) {

		this.id = id;
		this.agentType = agentType;
		this.attributeValues = attributeValues;
	}

	@Override
	public final Object getId() {
		return id;
	}

	@Override
	public Object getValueForAttribute(Attribute attribute) {
	
		return attributeValues[agentType.getAllAttributes().indexOf(attribute)];
	}

	@Override
	public Object getValueForAttribute(int idx) {
		return attributeValues[idx];
	}

	@Override
	public void setValueForAttribute(Attribute attribute, Object value) {
		attributeValues[agentType.getAllAttributes().indexOf(attribute)] = value;
	}

	@Override
	public void setValueForAttribute(int idx, Object value) {
		attributeValues[idx] = value;
	}
	

}
