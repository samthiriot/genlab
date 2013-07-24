package genlab.core.model.meta.basics.flowtypes;

import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

/**
 * Convenience class to make easier the creation of in or out connection.
 * 
 * @author Samuel Thiriot
 *
 */
public class GraphInOut extends InputOutput<IGenlabGraph> {

	public GraphInOut(String id, String name, String desc) {
		super(SimpleGraphFlowType.SINGLETON, id, name, desc);
	}

	public GraphInOut(String id, String name, String desc,
			boolean acceptMultipleInputs) {
		super(SimpleGraphFlowType.SINGLETON, id, name, desc, acceptMultipleInputs);
	}

}
