package genlab.bayesianinference;

import java.awt.Point;

public class BNUtils {

	private BNUtils() {
		
	}
	
	/**
	 * Copies the content of one Bayesian network to an empty one.
	 * @param from
	 * @param to
	 */
	public static void copyContentTo(IBayesianNetwork from, IBayesianNetwork to) {
		
		// copy nodes
		for (String id: from.getAllNodesIds()) {
			
			IBayesianNode node = from.getForID(id);
			
			// create a node with same idea
			IBayesianNode nodeCopy = to.createNode(id, node.getDomain());
		
			// other properties
			nodeCopy.setLabel(node.getLabel());
			nodeCopy.setLocation((Point)node.getLocation().clone());

		}
		
		// create edges
		for (String id: from.getAllNodesIds()) {
			
			IBayesianNode node = from.getForID(id);
			
			for (IBayesianNode nodeFrom: from.getIncoming(node)) {
			
				to.addEdge(nodeFrom.getID(), id);
			}
			
		}
		
		// copy CPT
		for (String id: from.getAllNodesIds()) {
			
			IBayesianNode node = from.getForID(id);
			IBayesianNode nodeCopy = to.getForID(id);
		
			for (int i=0; i<nodeCopy.getCPT().getCardinality(); i++) {
				nodeCopy.getCPT().setProbabilityForCellIdx(i, node.getCPT().getProbabilityForCellIdx(i));
					
			}
		}
		
		
	}

}
