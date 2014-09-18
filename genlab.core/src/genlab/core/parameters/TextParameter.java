package genlab.core.parameters;

import java.util.Collections;
import java.util.Map;

/**
 * A parameter which accepts a long text (several lines) and 
 * might have a long description, which will be totally displayed.
 * @author Samuel Thiriot
 *
 */
public class TextParameter extends StringBasedParameter<String> {

	public TextParameter(String id, String name, String desc,
			String defaultValue) {
		super(id, name, desc, defaultValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String,Boolean> check(String something) {
		return Collections.EMPTY_MAP;
	}

	@Override
	public String parseFromString(String value) {
		return value;
	}

}
