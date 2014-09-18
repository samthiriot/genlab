package genlab.populations.bo;

import genlab.core.commons.WrongParametersException;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PopulationDescription {

	protected List<IAgentType> agentTypes = new LinkedList<IAgentType>();
	protected Set<LinkType> linksTypes = new HashSet<LinkType>();
	protected Set<AggregationRelationship> aggregationRelationships = new HashSet<AggregationRelationship>();
	
	public PopulationDescription() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Creates a novel agent type for this name and desc;
	 * TODO will throw an error if the name already exists. 
	 * @param name
	 * @param desc
	 */
	public void addAgentType(IAgentType agentType) {
		if (agentTypes.contains(agentType))
			throw new WrongParametersException("a type named "+agentType+" was already defined");
		agentTypes.add(agentType);
	}
	
	public void addAggregationRelationship(AggregationRelationship aggregationLink) {
		// TODO
	}
	
	
	public List<IAgentType> getAgentTypes() {
		return agentTypes;
	}
	
	public IAgentType getAgentTypeForName(String name) {
		for (IAgentType t: agentTypes) {
			if (t.getName().equals(name)) {
				return t;
			}
		}
		return null;
	}

	public Set<AggregationRelationship> getAggregationRelationship() {
		return Collections.unmodifiableSet(aggregationRelationships);
	}
	
	public void addLinkType(LinkType linkType) {
		
	}
	
	public Set<LinkType> getLinkTypes() {
		return Collections.unmodifiableSet(linksTypes);
	}
	
	
}
