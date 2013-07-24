package genlab.core.model.meta.basics.flowtypes;

import genlab.core.model.meta.InputOutput;

/**
 * Convenience class to make easier the creation of in or out connection.
 * 
 * @author Samuel Thiriot
 *
 */
public class BooleanInOut extends InputOutput<Boolean> {

	public BooleanInOut(String id, String name, String desc) {
		super(BooleanFlowType.SINGLETON, id, name, desc);
	}

	public BooleanInOut(String id, String name, String desc,
			boolean acceptMultipleInputs) {
		super(BooleanFlowType.SINGLETON, id, name, desc, acceptMultipleInputs);
	}

}
