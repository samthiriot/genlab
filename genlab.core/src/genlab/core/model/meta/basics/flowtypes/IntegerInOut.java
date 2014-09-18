package genlab.core.model.meta.basics.flowtypes;

import genlab.core.model.meta.InputOutput;

/**
 * Convenience class to make easier the creation of in or out connection.
 * 
 * @author Samuel Thiriot
 *
 */
public class IntegerInOut extends InputOutput<Integer> {

	public IntegerInOut(String id, String name, String desc) {
		super(IntegerFlowType.SINGLETON, id, name, desc);
	}

	public IntegerInOut(String id, String name, String desc,
			boolean acceptMultipleInputs) {
		super(IntegerFlowType.SINGLETON, id, name, desc, acceptMultipleInputs);
	}
	
	public IntegerInOut(String id, String name, String desc, Integer defaultValue) {
		super(IntegerFlowType.SINGLETON, id, name, desc, defaultValue);
	}

	public IntegerInOut(String id, String name, String desc, Integer defaultValue,
			boolean acceptMultipleInputs) {
		super(IntegerFlowType.SINGLETON, id, name, desc, defaultValue, acceptMultipleInputs);
	}

}
