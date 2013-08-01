package genlab.core.parameters;

import genlab.core.commons.WrongParametersException;

import java.util.Collections;
import java.util.Map;


public class BooleanParameter extends Parameter<Boolean> {

	public BooleanParameter(String id, String name, String desc,
			Boolean defaultValue) {
		super(id, name, desc, defaultValue);
	}

	@Override
	public Map<String, Boolean> check(Boolean value) {
		return Collections.EMPTY_MAP;
	}

	@Override
	public Boolean parseFromString(String value) {
		value = value.toLowerCase().trim();
		if (value.equals("true") || value.equals("1"))
			return true;
		else if (value.equals("false") || value.equals("0"))
			return false;
		else 
			throw new WrongParametersException("unable to decode a Boolean from value: "+value);
	}

	
	
}
