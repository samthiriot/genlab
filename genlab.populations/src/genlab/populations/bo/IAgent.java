package genlab.populations.bo;

public interface IAgent extends IAttributesHolderInstance {

	public Object getId();
	
	public IAgentType getAgentType();
	
	public IAgent cloneAgent();
}
