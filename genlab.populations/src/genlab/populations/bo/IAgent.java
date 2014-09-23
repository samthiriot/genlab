package genlab.populations.bo;

import java.util.Map;

public interface IAgent extends IAttributesHolderInstance {

	public Object getId();
	
	public IAgentType getAgentType();
	
	public IAgent cloneAgent();

	
}
