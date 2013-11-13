package genlab.core.parameters;

import genlab.core.commons.WrongParametersException;


public class DoubleParameter extends NumberParameter<Double> {

	public int getPrecision() {
		return 4;
	}
	
	public DoubleParameter(String id, String name, String desc, Double defaultValue) {
		super(id, name, desc, defaultValue);
	}

	public Double parseFromString(String value) {
		try {
			return Double.parseDouble(value);
		} catch (RuntimeException e) {
			throw new WrongParametersException("Double value expected (like: "+(new Double(0.1)).toString()+")");
		}
	}
	
}
