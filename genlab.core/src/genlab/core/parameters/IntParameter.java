package genlab.core.parameters;

import genlab.core.commons.WrongParametersException;


public class IntParameter extends NumberParameter<Integer> {

	public IntParameter(String id, String name, String desc, Integer defaultValue) {
		super(id, name, desc, defaultValue);
	}
	
	public IntParameter(String id, String name, String desc, Integer defaultValue, Integer min) {
		super(id, name, desc, defaultValue, min);
	}

	public IntParameter(String id, String name, String desc, Integer defaultValue, Integer min, Integer max) {
		super(id, name, desc, defaultValue, min, max);
	}

	public IntParameter(String id, String name, String desc, Integer defaultValue, Integer min, Integer max, Integer step) {
		super(id, name, desc, defaultValue, min, max, step);
	}

	public Integer parseFromString(String value) {
		try {
			return Integer.parseInt(value);
		} catch (RuntimeException e) {
			throw new WrongParametersException("Integer value expected (like: 3)");
		}
	}
	
}
