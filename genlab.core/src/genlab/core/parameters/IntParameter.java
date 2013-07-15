package genlab.core.parameters;

import genlab.core.commons.WrongParametersException;


public class IntParameter extends NumberParameter<Integer> {

	public IntParameter(String id, String name, String desc, Integer defaultValue) {
		super(id, name, desc, defaultValue);
	}

	public Integer parseFromString(String value) {
		try {
			return Integer.parseInt(value);
		} catch (RuntimeException e) {
			throw new WrongParametersException("Integer value expected (like: 3)");
		}
	}
	
}
