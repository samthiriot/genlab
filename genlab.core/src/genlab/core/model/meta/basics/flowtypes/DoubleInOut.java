package genlab.core.model.meta.basics.flowtypes;

import genlab.core.model.meta.InputOutput;

/**
 * Convenience class to make easier the creation of in or out connection.
 * 
 * @author Samuel Thiriot
 *
 */
public class DoubleInOut extends InputOutput<Double> {

	public DoubleInOut(String id, String name, String desc) {
		super(DoubleFlowType.SINGLETON, id, name, desc);
	}

	public DoubleInOut(String id, String name, String desc,
			boolean acceptMultipleInputs) {
		super(DoubleFlowType.SINGLETON, id, name, desc, acceptMultipleInputs);
	}

}
