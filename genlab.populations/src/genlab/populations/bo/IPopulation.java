package genlab.populations.bo;

import genlab.core.model.meta.basics.flowtypes.IGenlabTablesContainer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * A population is created for one population description, which describes what
 * can be stored inside the population. The population description is supposed to be 
 * unmutable during the use of one instance of IPopulation. 
 * 
 * A population can be seen as a IGenlabTablesContainer, each table being the table 
 * of attributes of one agent type. 
 * 
 * @author Samuel Thiriot
 *
 */
public interface IPopulation extends IGenlabTablesContainer {

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
	public IAgent createAgent(IAgentType type);

	
	public int getTotalAgentsCount();
	public int getAgentsCount(IAgentType type);
	
	public Iterator<IAgent> getAgentsIterator();
	
	public Iterator<IAgent> getAgentsIterator(IAgentType type);

	public Collection<IAgent> getAgents(IAgentType type);

	public Collection<IAgent> getAgents();

	public IPopulation clonePopulation();

}
