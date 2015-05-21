package genlab.core.model.meta.basics.flowtypes;

import genlab.core.model.meta.InputOutput;

/**
 * Convenience class to make easier the creation of in or out connection.
 * 
 * @author Samuel Thiriot
 *
 */
public class DoubleInOut extends InputOutput<Double> {


	protected Double min = null;
	protected Double max = null;
	
	public DoubleInOut(String id, String name, String desc) {
		super(DoubleFlowType.SINGLETON, id, name, desc);
	}

	public DoubleInOut(String id, String name, String desc,
			boolean acceptMultipleInputs) {
		super(DoubleFlowType.SINGLETON, id, name, desc, acceptMultipleInputs);
	}

	public DoubleInOut(String id, String name, String desc, Double defaultValue) {
		super(DoubleFlowType.SINGLETON, id, name, desc, defaultValue);
	}
	

	public DoubleInOut(String id, String name, String desc, Double defaultValue, Double min) {
		super(DoubleFlowType.SINGLETON, id, name, desc, defaultValue);
		this.min = min;
	}

	public DoubleInOut(String id, String name, String desc, Double defaultValue, Double min, Double max) {
		super(DoubleFlowType.SINGLETON, id, name, desc, defaultValue);
		this.max = max;
	}


	public DoubleInOut(String id, String name, String desc, Double defaultValue,
			boolean acceptMultipleInputs) {
		super(DoubleFlowType.SINGLETON, id, name, desc, defaultValue, acceptMultipleInputs);
	}

	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}
	

}
