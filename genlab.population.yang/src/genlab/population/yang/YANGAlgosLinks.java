package genlab.population.yang;

import java.io.ObjectInputStream.GetField;
import java.util.Map;

import genlab.bayesianinference.IBayesianNetwork;
import genlab.bayesianinference.IBayesianNode;
import genlab.bayesianinference.IInferenceEngine;
import genlab.core.commons.WrongParametersException;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.populations.bo.Attribute;
import genlab.populations.bo.IAgentType;
import genlab.populations.bo.IPopulation;
import genlab.populations.bo.LinkType;

/**
 * The algorithms from YANG for links generation
 * 
 * @author Samuel Thiriot
 *
 */
public class YANGAlgosLinks {

	private YANGAlgosLinks() {
		
	}
	
	public static final String NAME_NODE_CREATE_LINK = "createLink";
	
	// TODO return generation stats
	public static void createLinks(
			IPopulation population,
			LinkType linktype,
			IInferenceEngine infBN,
			ListOfMessages messages
			) {
		
		IAgentType type1 = linktype.getAgentTypeFrom();
		IAgentType type2 = linktype.getAgentTypeTo();
		
		IBayesianNetwork bn = infBN.getBayesianNetwork();
		
		// identify the variable for the creation of the link
		IBayesianNode nodeForCreate = bn.getForID(NAME_NODE_CREATE_LINK);
		if (nodeForCreate == null) 
			throw new WrongParametersException(
					"unable to find the variable "
					+NAME_NODE_CREATE_LINK+ " in the BN"
					);
		// ... ensure it has the right characteristics
		if (nodeForCreate.getDomainSize() != 2) 
			throw new WrongParametersException(
				"variable "
				+NAME_NODE_CREATE_LINK+ " in the BN should contain exactly 2 possibilities, yes and no; "
				+" here it contains "+nodeForCreate.getDomainSize()+" possibilities"
				);
		int nodeForCreateIdxYes, nodeForCreateIdxNo;
		try {
			nodeForCreateIdxYes = nodeForCreate.getIdxInDomain("yes");
			nodeForCreateIdxNo = nodeForCreate.getIdxInDomain("no");
		} catch (RuntimeException e) {
			throw new WrongParametersException(
					"variable "
					+NAME_NODE_CREATE_LINK+ " in the BN should contain exactly 2 possibilities, yes and no; "
					+" here it contains "+nodeForCreate.getDomain().toString()
					);
		}
		
		
		// building mapping
		AttributeToNodeMapping mappingAgent1 = new AttributeToNodeMapping(type1.getAttributesCount());
		AttributeToNodeMapping mappingAgent2 = new AttributeToNodeMapping(type2.getAttributesCount());
		AttributeToNodeMapping mappingLink = new AttributeToNodeMapping(linktype.getAttributesCount());
		
		for (IBayesianNode node: bn.getAllNodes()) {
			
			String strippedNodeName;
			
			if (node.getID().startsWith("a1_")) {
				strippedNodeName = node.getID().substring(3);
				Attribute a = type1.getAttributeForId(strippedNodeName);
				if (a == null) 
					throw new WrongParametersException(
							"because it starts with 'a1_', the variable '"
							+node.getID()+" should have a counterpart in the agent type,"
							+ "but no attribute is named "+strippedNodeName
							);
				mappingAgent1.addMapping(a, node);
			}
			
			// TODO a2
			
			if (node.getID().startsWith("l_")) {
				// TODO for link attribute !
			}	
			
		}
		
		// set evidence for "we focus on agents which can be linked"
		// ... 
		//infBN.addEvidenceForAttribute("createLink", );
		
	}

}
