package genlab.core.model.meta.basics.flowtypes;

import genlab.core.model.meta.InputOutput;

/**
 * Convenience class to make easier the creation of in or out connection.
 * 
 * @author Samuel Thiriot
 *
 */
public class NumberInOut extends InputOutput<Number> {

	private Number min = null;
	private Number max = null;
	
	public NumberInOut(String id, String name, String desc) {
		super(NumberFlowType.SINGLETON, id, name, desc);
	}

	public NumberInOut(String id, String name, String desc,
			boolean acceptMultipleInputs) {
		super(NumberFlowType.SINGLETON, id, name, desc, acceptMultipleInputs);
	}
	
	public NumberInOut(String id, String name, String desc, Integer defaultValue) {
		super(NumberFlowType.SINGLETON, id, name, desc, defaultValue);
	}

	public NumberInOut(String id, String name, String desc, Integer defaultValue, Integer min) {
		super(NumberFlowType.SINGLETON, id, name, desc, defaultValue);
		this.min = min;
	}

	public NumberInOut(String id, String name, String desc, Integer defaultValue, Integer min, Integer max) {
		super(NumberFlowType.SINGLETON, id, name, desc, defaultValue);
		this.min = min;
		this.max = max;
	}

	public NumberInOut(String id, String name, String desc, Integer defaultValue,
			boolean acceptMultipleInputs) {
		super(NumberFlowType.SINGLETON, id, name, desc, defaultValue, acceptMultipleInputs);
	}
	
	public Number getMin() {
		return min;
	}

	public void setMin(Number min) {
		this.min = min;
	}

	public Number getMax() {
		return max;
	}

	public void setMax(Number max) {
		this.max = max;
	}
	

}
