package genlab.populations.implementations.basic;

import genlab.populations.bo.IAgent;
import genlab.populations.bo.IAgentType;

public class Agent extends AttributesHolderInstance implements IAgent {

	protected final Object id;
	protected final IAgentType agentType;
	
	public Agent(Object id, IAgentType agentType, Object[] attributeValues) {

		super(agentType, attributeValues);
		
		this.id = id;
		this.agentType = agentType;
	}
	
	public Agent(Object id, IAgentType agentType) {

		super(agentType);
		
		this.id = id;
		this.agentType = agentType;
	}

	@Override
	public final Object getId() {
		return id;
	}


	@Override
	public IAgentType getAgentType() {
		return agentType;
	}

	@Override
	public IAgent cloneAgent() {
		return new Agent(id, agentType, attributeValues.clone());
	}
	
}
