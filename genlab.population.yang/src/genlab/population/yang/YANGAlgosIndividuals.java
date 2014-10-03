package genlab.population.yang;

import genlab.bayesianinference.BayesianNetworkException;
import genlab.bayesianinference.IBayesianNetwork;
import genlab.bayesianinference.IBayesianNode;
import genlab.bayesianinference.IInferenceEngine;
import genlab.bayesianinference.smile.SMILEInferenceEngine;
import genlab.core.commons.WrongParametersException;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationState;
import genlab.core.random.IRandomNumberGenerator;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.populations.bo.Attribute;
import genlab.populations.bo.IAgent;
import genlab.populations.bo.IAgentType;
import genlab.populations.bo.IPopulation;
import genlab.random.colt.ColtRandomGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * All the YANG algorithms for the generation of individuals
 * 
 * @author Samuel Thiriot
 *
 */
public class YANGAlgosIndividuals {

	private YANGAlgosIndividuals() {
		
	}
	
	/**
	 * Creates an individual randomly for a given type, 
	 * an inference engine related to a relevant bayesian network,
	 * and a mapping between the attributes of the type and the bayesian nodes
	 * @param type
	 * @param infBN
	 * @param node2attribute
	 * @return
	 */
	public static Map<Attribute,Object> createIndividualFromBN(
								IAgentType type, 
								IInferenceEngine infBN, 
								AttributeToNodeMapping mapping
								) {
		
		
		// reset evidence
		infBN.resetEvidence();
		
		Map<Attribute,Object> attribute2value = new HashMap<Attribute, Object>(type.getAllAttributesCount());
		
		for (IBayesianNode node: infBN.getBayesianNetwork().topologicalOrder()) {
			
			Attribute attribute = mapping.node2attribute.get(node);
			
			if (attribute == null)
				// TODO log the type we forgot there
				// no attribute counterpart; let's forget it
				continue;
			
			int idx = infBN.getAttributeRandomnlyGiven(node.getID());
			
			// save that value
			attribute2value.put(attribute, infBN.getValueOf(node.getID(), idx));
			
			// and add the corresponding evidence 
			infBN.addEvidenceForAttribute(node.getID(), idx);
			
		}
		
		return attribute2value;

	}
	
	public static void updateIndividualFromBN(
			IAgent agent,
			IAgentType type, 
			IInferenceEngine infBN, 
			AttributeToNodeMapping mapping,
			ListOfMessages messages
			) {

		
		// reset evidence
		infBN.resetEvidence();
		
		
		// first update evidence for attributes which are defined already inside the agent
		for (Attribute a: mapping.attribute2node.keySet()) {
			
			Object v = agent.getValueForAttribute(a);
			
			if (v == null)
				// no value defined for agent
				// => ignore
				continue;
			
			// there is a value there to use !
			IBayesianNode node = mapping.attribute2node.get(a);
			if (node == null)
				// no node for this agent attribute
				// => ignore
				continue;
			
			// is "toString" enough ? Specidic mapping of names ?
			int idx = -1;
			try {
				idx = node.getIdxInDomain(YangUtilities.getBNDomainForJavaValue(v));
			} catch (BayesianNetworkException e) {
				// error: unable to find the value for this Bayesian networ
				String msg = "The attribute named \""+a.getID()+" in the agent type "
						+ "has a counterpart with the same name in the Bayesian network. "
						+ "Still we were not able to find the value \""+v.toString()+"\" "
						+ "in the Bayesian variable. Please either add this value in the domain "
						+ "of the variable, or change the variable name in the Bayesian network "
						+ "if you don't desire to use this variable in this process.";
				messages.errorUser(msg, 
						YANGAlgosIndividuals.class
						);
				throw new WrongParametersException(msg);
				
			}
			
			// add this inference
			infBN.addEvidenceForAttribute(a.getID(), idx);
			
		}
		
		// now really update the values of the individual
		for (IBayesianNode node: infBN.getBayesianNetwork().topologicalOrder()) {
			
			Attribute attribute = mapping.node2attribute.get(node);
			
			if (attribute == null)
				// TODO log the type we forgot there
				// no attribute counterpart; let's forget it
				continue;
			
			if (agent.getValueForAttribute(attribute) != null)
				// don't override values
				// TODO option ?
				continue;
			
			int idx = infBN.getAttributeRandomnlyGiven(node.getID());
			
			// save that value in the agent
			agent.setValueForAttribute(attribute, infBN.getValueOf(node.getID(), idx));
			
			// and add the corresponding evidence 
			infBN.addEvidenceForAttribute(node.getID(), idx);
			
		}
		
		
	}
	
	
	
	protected static AttributeToNodeMapping buildMapping(
			IAgentType type,
			ListOfMessages messages,
			IBayesianNetwork bn
			) {
		
		AttributeToNodeMapping mapping = new AttributeToNodeMapping(type.getAllAttributesCount());
		
		Set<Attribute> ignoredAttributes = new HashSet<Attribute>(type.getAllAttributesCount());
		
		for (Attribute currentAttribute : type.getAllAttributes()) {
			
			// detect attributes which cannot be mapped
			if (!bn.containsNode(currentAttribute.getID())) {
				ignoredAttributes.add(currentAttribute);
				continue;
			}
			
			// no problem; store this attribute in mapping
			IBayesianNode node = bn.getForID(currentAttribute.getID());
			mapping.addMapping(currentAttribute, node);
			
		}
		if (!ignoredAttributes.isEmpty()) {
			messages.infoUser("during the generation of the attributes for agent type "+type.getName()+", several attributes ("+ignoredAttributes.size()+") were not found in the Bayesian network and are thus not generated by this process: "+ignoredAttributes, YANGAlgosIndividuals.class);
		}
		
		return mapping;
	}
	
	/**
	 * Iterates accross the attributes of the type; 
	 * @param progress
	 * @param population
	 * @param type
	 * @param messages
	 * @param bn
	 * @param count
	 */
	public static void fillPopulationFromBN(
			ComputationProgressWithSteps progress,
			IPopulation population,
			IAgentType type,
			ListOfMessages messages,
			IBayesianNetwork bn,
			Integer count
			) {
		
		if (count == 0)
			return;
		
		progress.setProgressTotal(count);
		progress.setProgressMade(0);
		
		messages.debugUser("will create "+count+" individuals from this Bayesian network", YANGAlgosIndividuals.class);

		// build mapping
		AttributeToNodeMapping mapping = buildMapping(type, messages, bn);

		// and init inference engine
		IInferenceEngine infBN = new SMILEInferenceEngine(bn);
		
		// actual creation
		population.startManyOperations();
		for (int i=0; i<count; i++) {
			
			if (i%20 == 0) {
				// update progress
				progress.setProgressMade(i);
				// flush buffer
				population.endManyOperations();
				population.startManyOperations();
				// what if we are canceled ? 
				if (progress.getComputationState() == ComputationState.FINISHED_CANCEL)
					return; // TODO should we do something else ?
			}
			
			// generate one individual
			Map<Attribute,Object> novelAttributes = createIndividualFromBN(type, infBN, mapping); 

			population.createAgent(type, novelAttributes);
			
		}
		population.endManyOperations();
		
		infBN.exportPropagationStatistics(System.out);
		
		
		
	}
	
	/**
	 * Iterates accross the attributes of the type; 
	 * @param progress
	 * @param population
	 * @param type
	 * @param messages
	 * @param bn
	 * @param count
	 */
	public static void updatePopulationFromBN(
			ComputationProgressWithSteps progress,
			IPopulation population,
			IAgentType type,
			ListOfMessages messages,
			IBayesianNetwork bn
			) {
		
		try {
			Collection<IAgent> toUpdate = population.getAgents(type);
			
			if (toUpdate.size() == 0)
				return;
			
			progress.setProgressTotal(toUpdate.size());
			progress.setProgressMade(0);
			
			messages.debugUser("will update the attributes of "+toUpdate.size()+" individuals from this Bayesian network", YANGAlgosIndividuals.class);
	
			// build mapping
			AttributeToNodeMapping mapping = buildMapping(type, messages, bn);
	
			// and init inference engine
			IInferenceEngine infBN = new SMILEInferenceEngine(bn);
			
			// actual creation
			population.startManyOperations();
			int i=0;
			for (IAgent currentAgent : toUpdate) {
				
				if (i%20 == 0) {
					// update progress
					progress.setProgressMade(i);
					// flush buffer
					population.endManyOperations();
					population.startManyOperations();
					// what if we are canceled ? 
					if (progress.getComputationState() == ComputationState.FINISHED_CANCEL)
						return; // TODO should we do something else ?
				}
				
				// generate one individual
				updateIndividualFromBN(
						currentAgent, 
						type, 
						infBN, 
						mapping,
						messages
						); 
			
				i++;
				
			}
			
			infBN.exportPropagationStatistics(System.out);

			
		} finally {
			population.endManyOperations();
			
			
		}
		
	}

}
