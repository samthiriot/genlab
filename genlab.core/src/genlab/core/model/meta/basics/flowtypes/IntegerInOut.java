package genlab.core.model.meta.basics.flowtypes;

import genlab.core.model.meta.InputOutput;

/**
 * Convenience class to make easier the creation of in or out connection.
 * 
 * @author Samuel Thiriot
 *
 */
public class IntegerInOut extends InputOutput<Integer> {

	private Integer min = null;
	private Integer max = null;
	
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

	public IntegerInOut(String id, String name, String desc, Integer defaultValue, Integer min) {
		super(IntegerFlowType.SINGLETON, id, name, desc, defaultValue);
		this.min = min;
	}

	public IntegerInOut(String id, String name, String desc, Integer defaultValue, Integer min, Integer max) {
		super(IntegerFlowType.SINGLETON, id, name, desc, defaultValue);
		this.min = min;
		this.max = max;
	}

	public IntegerInOut(String id, String name, String desc, Integer defaultValue,
			boolean acceptMultipleInputs) {
		super(IntegerFlowType.SINGLETON, id, name, desc, defaultValue, acceptMultipleInputs);
	}
	
	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}
	

}
