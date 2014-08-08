package genlab.populations.implementations.basic;

import genlab.populations.bo.Attribute;
import genlab.populations.bo.IAgent;
import genlab.populations.bo.IAgentType;
import genlab.populations.bo.IPopulation;
import genlab.populations.bo.PopulationDescription;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class Population implements IPopulation {

	protected final PopulationDescription popDesc;
	
	
	protected final Object lockerPopulation = new Object();
	protected final Map<Object,IAgent> id2agent = new HashMap<Object, IAgent>();
	protected final Map<IAgentType,Collection<IAgent>> type2agents = new HashMap<IAgentType, Collection<IAgent>>();
	
	
	public Population(PopulationDescription popDesc) {

		this.popDesc = popDesc;
		
		// initialize internal structures
		for (IAgentType type: popDesc.getAgentTypes()) {
		
			type2agents.put(type, new LinkedList<IAgent>());
		}
		
	}

	@Override
	public PopulationDescription getPopulationDescription() {
		return popDesc;
	}

	@Override
	public void startManyOperations() {
		// nothing to do here
	}

	@Override
	public void endManyOperations() {
		// nothing to do here
	}

	@Override
	public IAgent createAgent(IAgentType type, Object[] values) {
		
		//System.err.println("adding agent "+type+" "+Arrays.toString(values));
		
		IAgent agent ;
		synchronized (lockerPopulation) {
			
			Integer id = id2agent.size();
			
			agent = new Agent(id, type, values);
			
			// add it to the population
			id2agent.put(id, agent);
			type2agents.get(type).add(agent);
				
		}
		
		return agent;
	}

	@Override
	public IAgent createAgent(IAgentType type, Map<Attribute, Object> values) {
		
		Object[] aValues = new Object[type.getAttributesCount()];
		for (int i=0; i<type.getAttributesCount(); i++) {
			aValues[i] = values.get(type.getAllAttributes().get(i));
		}
		return createAgent(type, aValues);
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("population with types ").append(popDesc.getAgentTypes()).append(" and ").append(id2agent.size()).append(" individuals");
		return sb.toString();
	}

	@Override
	public int getTotalAgentsCount() {
		return id2agent.size();
	}

	@Override
	public Iterator<IAgent> getAgentsIterator() {
		return id2agent.values().iterator();
	}

	@Override
	public Iterator<IAgent> getAgentsIterator(IAgentType type) {
		return type2agents.get(type).iterator();
	}

	@Override
	public Collection<IAgent> getAgents(IAgentType type) {
		return type2agents.get(type);
	}

	@Override
	public Collection<IAgent> getAgents() {
		return id2agent.values();
	}

	@Override
	public int getAgentsCount(IAgentType type) {
		return type2agents.get(type).size();
	}

	
	
}
