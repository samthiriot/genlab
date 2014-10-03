package genlab.populations.implementations.basic;

import genlab.core.commons.NotImplementedException;
import genlab.core.commons.WrongParametersException;
import genlab.core.model.meta.basics.flowtypes.IGenlabTable;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.populations.bo.AccessAgentsCollectionAsTable;
import genlab.populations.bo.Attribute;
import genlab.populations.bo.IAgent;
import genlab.populations.bo.IAgentType;
import genlab.populations.bo.IPopulation;
import genlab.populations.bo.PopulationDescription;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Population implements IPopulation {

	protected final PopulationDescription popDesc;
	
	
	protected final Object lockerPopulation = new Object();
	protected final Map<Object,IAgent> id2agent = new HashMap<Object, IAgent>();
	protected final Map<IAgentType,List<IAgent>> type2agents = new HashMap<IAgentType, List<IAgent>>();
	
	
	public Population(PopulationDescription popDesc) {

		this.popDesc = popDesc;
		
		// initialize internal structures
		for (IAgentType type: popDesc.getAllAgentTypes()) {
			
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

	private void _addAgent(IAgent a) {
		
		// index the agent from its id
		{
			IAgent previous = id2agent.put(a.getId(), a);
			if (previous != null)
				throw new WrongParametersException("another agent having id "+a.getId()+" already exists in the population");
		}
		
		// associate agent with its type
		if (!type2agents.containsKey(a.getAgentType()))
			throw new WrongParametersException("agent type "+a.getAgentType()+" was not declared in the population description");
		
		type2agents.get(a.getAgentType()).add(a);
		
		// associate the agent for its parent types
		if (! a.getAgentType().getInheritedTypes().isEmpty()) {
			Set<IAgentType> processedParentTypes = new HashSet<IAgentType>();
			Set<IAgentType> toProcessParentTypes = new HashSet<IAgentType>();
			toProcessParentTypes.addAll(a.getAgentType().getInheritedTypes());
		
			while (!toProcessParentTypes.isEmpty()) {
				
				Iterator<IAgentType> it = toProcessParentTypes.iterator();
				IAgentType currentType = it.next();
				it.remove();
		
				// add this 
				if (!type2agents.containsKey(currentType))
					throw new WrongParametersException("agent type "+currentType+" was not declared in the population description");
				
				type2agents.get(currentType).add(a);
				
				// don't study this case anymore
				processedParentTypes.add(currentType);
				
				// add all the parents types to analyze
				for (IAgentType currentParent: currentType.getInheritedTypes()) {
					if (!processedParentTypes.contains(currentParent))
						toProcessParentTypes.add(currentParent);
				}
			}
		}
	}
	@Override
	public IAgent createAgent(IAgentType type, Object[] values) {
		
		if (popDesc.hasInheritanceChildren(type))
			throw new WrongParametersException("cannot create an agent of a type which is inherited by other types");
		
		IAgent agent ;
		synchronized (lockerPopulation) {
			
			Integer id = id2agent.size();
			
			agent = new Agent(id, type, values);
			
			// add it to the population
			_addAgent(agent);
		}
		
		return agent;
	}

	@Override
	public IAgent createAgent(IAgentType type) {
		
		if (popDesc.hasInheritanceChildren(type))
			throw new WrongParametersException("cannot create an agent of a type which is inherited by other types");
		
		Object[] values = new Object[type.getAllAttributesCount()];
		
		IAgent agent ;
		synchronized (lockerPopulation) {
			
			Integer id = id2agent.size();
			
			agent = new Agent(id, type, values);
			
			// add it to the population
			_addAgent(agent);	
		}
		
		return agent;
	}

	@Override
	public IAgent createAgent(IAgentType type, Map<Attribute, Object> values) {
		
		if (popDesc.hasInheritanceChildren(type))
			throw new WrongParametersException("cannot create an agent of a type which is inherited by other types");
		
		Object[] aValues = new Object[type.getAllAttributesCount()];
		for (int i=0; i<type.getAllAttributesCount(); i++) {
			aValues[i] = values.get(type.getAllAttributes().get(i));
		}
		return createAgent(type, aValues);
	}
	
	protected void addAgent(Agent agent) {
		
		synchronized (lockerPopulation) {
			
			if (id2agent.containsKey(agent.id))
				throw new WrongParametersException("an agent already exists with id "+agent.id);
			
			// add it to the population
			_addAgent(agent);
		}
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("population containing ").append(id2agent.size()).append(" individuals: ");
		boolean comma = false;
		for (IAgentType t: popDesc.getAllAgentTypes()) {
			if (comma)
				sb.append(", ");
			else
				comma = true;
			sb.append(t.getName()).append(": ").append(type2agents.get(t).size());	
		}
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

	@Override
	public IPopulation clonePopulation() {

		Population clone = new Population(popDesc);
		
		// assumes the population description is never going to be changed in the clone
		// nor original
		
		synchronized (lockerPopulation) {
			for (Object id: id2agent.keySet()) {
				Agent originalAgent = (Agent)id2agent.get(id);
				clone.addAgent((Agent)originalAgent.cloneAgent());
			}
		}
		
		return clone;
	}

	/**
	 * Provides an iterator over a Population which actually iterates accross 
	 * the agent collections of each agent type.
	 * 
	 * @author Samuel Thiriot
	 *
	 */
	private final class AgentTypesAsTablesIterator implements Iterator<IGenlabTable> {

		private Iterator<IAgentType> itAgentTypes;
		
		public AgentTypesAsTablesIterator() {
			itAgentTypes = type2agents.keySet().iterator();
		}
		
		@Override
		public boolean hasNext() {
			return itAgentTypes.hasNext();
		}

		@Override
		public IGenlabTable next() {
			
			IAgentType type = itAgentTypes.next();
			return new AccessAgentsCollectionAsTable(type, type2agents.get(type));
		}

		@Override
		public void remove() {
			// TODO provide remove features for tables
			throw new NotImplementedException();
		}
		
	}
	
	@Override
	public final Iterator<IGenlabTable> iterator() {
		return new AgentTypesAsTablesIterator();
	}

	@Override
	public String getGraphId() {
		
		return "population_"+this.hashCode();
	}

	@Override
	public long getVerticesCount() {
		return getTotalAgentsCount();
	}

	@Override
	public long getEdgesCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void declareGraphAttribute(String attributeId, Class type) {
		throw new NotImplementedException("this population is in readonly mode");
	}

	@Override
	public boolean hasGraphAttribute(String attribute) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<String> getDeclaredGraphAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Class> getDeclaredGraphAttributesAndTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGraphAttribute(String attributeId, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getGraphAttribute(String attributeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void declareVertexAttribute(String attributeId, Class type) {
		// TODO Auto-generated method stub
		throw new NotImplementedException("this population is in readonly mode");

	}

	@Override
	public boolean hasVertexAttribute(String attributeId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void declareEdgeAttribute(String attributeId, Class type) {
		throw new NotImplementedException("this population is in readonly mode");

	}

	@Override
	public boolean hasEdgeAttribute(String attributeId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<String> getDeclaredVertexAttributes() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public Map<String, Class> getDeclaredVertexAttributesAndTypes() {
		return Collections.EMPTY_MAP;
	}

	@Override
	public Collection<String> getDeclaredEdgeAttributes() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public Map<String, Class> getDeclaredEdgeAttributesAndTypes() {
		return Collections.EMPTY_MAP;
	}

	@Override
	public Map<String, Object> getVertexAttributes(String vertexId) {
		
		IAgent agent = id2agent.get(vertexId);
		
		if (agent == null) {
			// here we are saw as a graph; maybe try with the string version ?
			for (Object id: id2agent.keySet()) {
				if (vertexId.equals(id.toString())) {
					agent = id2agent.get(id);
				}
			}
		}
		
		if (agent == null)
			throw new WrongParametersException("no agent having vertex Id "+vertexId);
		
		return agent.getValuesOfAttributesAsMap();
		
	}

	@Override
	public Map<String, Object> getEdgeAttributes(String vertexId) {
		return Collections.EMPTY_MAP;
	}

	@Override
	public Map<String, Object> getGraphAttributes() {
		return Collections.EMPTY_MAP;
	}

	@Override
	public void addVertex(String id) {
		throw new NotImplementedException("this population is in readonly mode");
	}

	@Override
	public boolean removeVertex(String id) {
		throw new NotImplementedException("this population is in readonly mode");
	}

	@Override
	public String getVertex(int index) {
		throw new NotImplementedException("this population is in readonly mode");
	}

	@Override
	public boolean removeVertex(int index) {
		throw new NotImplementedException("this population is in readonly mode");
	}

	@Override
	public void setVertexAttribute(
						String vertexId, 
						String attributeId,
						Object value) {
		
		IAgent agent = id2agent.get(vertexId);
		if (agent == null)
			throw new WrongParametersException("no agent with id "+vertexId);
		if (!agent.getAgentType().containsAttribute(attributeId))
			throw new WrongParametersException(
					"agent type "+agent.getAgentType()
					+" does not contain any attribute "
					+attributeId
					);
		agent.setValueForAttribute(attributeId, value);
	}

	@Override
	public void setVertexAttributes(String vertexId, Map<String, Object> values) {

		IAgent agent = id2agent.get(vertexId);
		if (agent == null)
			throw new WrongParametersException("no agent with id "+vertexId);
		
		IAgentType type = agent.getAgentType();
		
		for (String attributeId: values.keySet()) {
			
			if (!type.containsAttribute(attributeId))
				throw new WrongParametersException(
						"agent type "+agent.getAgentType()
						+" does not contain any attribute "
						+attributeId
						);
			agent.setValueForAttribute(
					attributeId, 
					values.get(attributeId)
					);
		}
		
	}

	@Override
	public Collection<String> getVertices() {
		Collection<String> res = new ArrayList<String>(id2agent.size());
		for (Object id: id2agent.keySet()) {
			res.add(id.toString());
		}
		return res;
	}

	@Override
	public void addEdge(String id, String vertexIdFrom, String vertexIdTo) {
		throw new NotImplementedException("this population is in readonly mode");

	}

	@Override
	public String addEdge(String vertexIdFrom, String vertexIdTo,
			boolean directed) {
		
		throw new NotImplementedException("this population is in readonly mode");
	}

	@Override
	public void addEdge(String id, String vertexIdFrom, String vertexIdTo,
			boolean directed) {
		
		throw new NotImplementedException("this population is in readonly mode");

	}

	@Override
	public void setEdgeAttribute(String edgeId, String attributeId, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEdgeAttributes(String edgeId, Map<String, Object> values) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean removeEdge(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeEdge(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsEdge(String vertexFrom, String vertexTo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsEdge(String edgeId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getEdge(int index) {
		
		throw new NotImplementedException("this population is in readonly mode");
	}

	@Override
	public boolean isMultiGraph() {
		return true;
	}

	@Override
	public GraphDirectionality getDirectionality() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isVertexAttributed() {
		return true;
	}

	@Override
	public boolean isEdgeAttributed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsVertex(String vertexId) {
		return id2agent.containsKey(vertexId);
	}

	@Override
	public Collection<String> getAllIncidentEdges(String vertexId) {
		// TODO
		return Collections.EMPTY_LIST;
	}

	@Override
	public Collection<String> getEdgesFrom(String vertexId) {
		// TODO Auto-generated method stub
		return Collections.EMPTY_LIST;
	}

	@Override
	public int getEdgesCountFrom(String vertexId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<String> getEdgesTo(String vertexId) {
		// TODO Auto-generated method stub
		return Collections.EMPTY_LIST;
	}

	@Override
	public int getEdgesCountTo(String vertexId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<String> getNeighboors(String vertexId) {
		return Collections.EMPTY_LIST;
	}

	@Override
	public int getNeighboorsCount(String vertexId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<String> getInNeighboors(String vertexId) {
		// TODO Auto-generated method stub
		return Collections.EMPTY_LIST;
	}

	@Override
	public Collection<String> getOutNeighboors(String vertexId) {
		// TODO Auto-generated method stub
		return Collections.EMPTY_LIST;
	}

	@Override
	public String getEdgeOtherVertex(String edgeId, String vertex1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEdgeVertexFrom(String edgeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEdgeVertexTo(String edgeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEdgeDirected(String edgeId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEdgeLoop(String edgeId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getEdgeBetween(String nodeId1, String nodeId2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getEdgesBetween(String nodeId1, String nodeId2) {
		// TODO Auto-generated method stub
		return Collections.EMPTY_LIST;
	}

	@Override
	public int getDegree(String nodeId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInDegree(String nodeId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOutDegree(String nodeId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<String> getEdges() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public IGenlabGraph clone(String cloneId) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();

	}

	@Override
	public void addAll(IGenlabGraph otherGraph, boolean copyGraphAttributes,
			boolean copyNodeAttributes, boolean copyEdgesAttributes) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	
	
}
