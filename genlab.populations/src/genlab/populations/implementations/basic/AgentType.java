package genlab.populations.implementations.basic;

import genlab.populations.bo.AttributesHolder;
import genlab.populations.bo.IAgentType;

public class AgentType extends AttributesHolder implements IAgentType {

	protected final String name;
	
	public AgentType(String name, String desc) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public final String getName() {
		return name;
	}
	
}
