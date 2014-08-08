package genlab.populations.bo;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public interface IPopulation {

	/**
	 * Returns the meta model for this population
	 * @return
	 */
	public PopulationDescription getPopulationDescription();
	
	/**
	 * Starts a set of actions. Could be used as transactions in a database for the 
	 * performance aspect; it might be used to create a buffer.
	 */
	public void startManyOperations();
	public void endManyOperations();
	
	/**
	 * Creates an individual with an automatic id, adds it to the population and returns it. 
	 * @param type
	 * @param values
	 * @return
	 */
	public IAgent createAgent(IAgentType type, Object[] values);
	public IAgent createAgent(IAgentType type, Map<Attribute,Object> values);
	
	public int getTotalAgentsCount();
	public int getAgentsCount(IAgentType type);
	
	public Iterator<IAgent> getAgentsIterator();
	
	public Iterator<IAgent> getAgentsIterator(IAgentType type);

	public Collection<IAgent> getAgents(IAgentType type);

	public Collection<IAgent> getAgents();

}
