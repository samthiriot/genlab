package genlab.core.parameters;

import java.util.Collections;
import java.util.Map;

public class StringParameter extends StringBasedParameter<String> {

	public StringParameter(String id, String name, String desc,
			String defaultValue) {
		super(id, name, desc, defaultValue);
	}

	@Override
	public Map check(String something) {
		return Collections.EMPTY_MAP;
	}

	@Override
	public String parseFromString(String value) {
		return value;
	}

}
