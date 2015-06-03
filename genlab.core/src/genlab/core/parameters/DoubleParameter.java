package genlab.core.parameters;

import genlab.core.commons.WrongParametersException;


public class DoubleParameter extends NumberParameter<Double> {

	public Integer precision = null;
	
	public int getPrecision() {
		return precision == null ? 4: precision;
	}
	
	public DoubleParameter(String id, String name, String desc, Double defaultValue) {
		super(id, name, desc, defaultValue);
	}

	
	public DoubleParameter(String id, String name, String desc,
			Double defaultValue, Double minValue, Double maxValue, Double step) {
		super(id, name, desc, defaultValue, minValue, maxValue, step);
	}

	public DoubleParameter(String id, String name, String desc,
			Double defaultValue, Double minValue, Double maxValue) {
		super(id, name, desc, defaultValue, minValue, maxValue);
	}

	public DoubleParameter(String id, String name, String desc,
			Double defaultValue, Double minValue) {
		super(id, name, desc, defaultValue, minValue);
	}

	public Double parseFromString(String value) {
		try {
			return Double.parseDouble(value);
		} catch (RuntimeException e) {
			throw new WrongParametersException("Double value expected (like: "+(new Double(0.1)).toString()+")");
		}
	}
	
}
