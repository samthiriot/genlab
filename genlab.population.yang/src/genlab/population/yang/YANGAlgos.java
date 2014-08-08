package genlab.population.yang;

import genlab.bayesianinference.IBayesianNetwork;
import genlab.bayesianinference.IBayesianNode;
import genlab.bayesianinference.IInferenceEngine;
import genlab.bayesianinference.smile.SMILEInferenceEngine;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationState;
import genlab.core.random.IRandomNumberGenerator;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.populations.bo.Attribute;
import genlab.populations.bo.IAgent;
import genlab.populations.bo.IAgentType;
import genlab.populations.bo.IPopulation;
import genlab.random.colt.ColtRandomGenerator;

import java.util.HashMap;
import java.util.Map;

public class YANGAlgos {

	private YANGAlgos() {
		
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
								Map<IBayesianNode,Attribute> node2attribute
								) {
		
		
		// reset evidence
		infBN.resetEvidence();
		
		Map<Attribute,Object> attribute2value = new HashMap<Attribute, Object>(type.getAttributesCount());
		
		for (IBayesianNode node: infBN.getBayesianNetwork().topologicalOrder()) {
			
			Attribute attribute = node2attribute.get(node);
			
			int idx = infBN.getAttributeRandomnlyGiven(node.getID());
			
			// save that value
			attribute2value.put(attribute, infBN.getValueOf(node.getID(), idx));
			
			// and add the corresponding evidence 
			infBN.addEvidenceForAttribute(node.getID(), idx);
			
		}
		
		return attribute2value;

	}
	
	public static void setAttributeRandom(
			ComputationProgressWithSteps progress,
			IPopulation population,
			IAgentType type,
			ListOfMessages messages,
			Attribute attribute,
			Integer min,
			Integer max
			) {
		
		progress.setProgressTotal(population.getAgentsCount(type));
		progress.setProgressMade(0);
		
		IRandomNumberGenerator rand = new ColtRandomGenerator();
		
		int i=0;
		for (IAgent agent: population.getAgents(type)) {
			Integer value = rand.nextIntBetween(min, max);
			agent.setValueForAttribute(attribute, value);
			
			if (i%20 == 1) {
				progress.setProgressMade(i);
			}
		}
		
		progress.setProgressMade(i);
		
	}
	
	public static void fillPopulationFromBN(
			ComputationProgressWithSteps progress,
			IPopulation population,
			IAgentType type,
			ListOfMessages messages,
			IBayesianNetwork bn,
			Integer count
			) {
		
		progress.setProgressTotal(count);
		progress.setProgressMade(0);
		
		messages.debugUser("will create "+count+" individuals from this Bayesian network", YANGAlgos.class);


		// build mapping
		Map<Attribute,IBayesianNode> attribute2node = new HashMap<Attribute, IBayesianNode>(type.getAttributesCount());
		Map<IBayesianNode,Attribute> node2attribute = new HashMap<IBayesianNode,Attribute>(type.getAttributesCount());

		boolean mappingProblem = false;
		
		for (Attribute currentAttribute : type.getAllAttributes()) {
			
			// detect attributes which cannot be mapped
			if (!bn.containsNode(currentAttribute.getID())) {
				mappingProblem = true;
				messages.errorUser("the attribute named \""+currentAttribute.getID()+"\" has no Bayesian network counterpart; please add one", YANGAlgos.class);
				continue;
			}
			
			// no problem; store this attribute in mapping
			IBayesianNode node = bn.getForID(currentAttribute.getID());
			attribute2node.put(currentAttribute, node);
			node2attribute.put(node, currentAttribute);
			
		}
		if (mappingProblem) {
			messages.errorUser("please correct the Bayesian network and rerun", YANGAlgos.class);
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
			return;
		}

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
			Map<Attribute,Object> novelAttributes = createIndividualFromBN(type, infBN, node2attribute); 

			population.createAgent(type, novelAttributes);
			
		}
		population.endManyOperations();
		
		infBN.exportPropagationStatistics(System.out);
		
	}

}
