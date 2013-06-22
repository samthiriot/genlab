package genlab.core.parameters;


public class DoubleParameter extends NumberParameter<Double> {

	public int getPrecision() {
		return 3;
	}
	
	public DoubleParameter(String id, String name, String desc, Double defaultValue) {
		super(id, name, desc, defaultValue);
	}

}
