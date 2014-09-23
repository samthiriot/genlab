package genlab.arithmetics.parameters;

import java.util.HashMap;
import java.util.Map;

import genlab.arithmetics.javaScripting.JavaScriptingExpressionParser;
import genlab.core.commons.WrongParametersException;
import genlab.core.parameters.Parameter;
import genlab.core.usermachineinteraction.ListOfMessages;

public class JavaScriptParameter extends Parameter<String> {

	public JavaScriptParameter(String id, String name, String desc,
			String defaultValue) {
		super(
				id, 
				name, 
				desc, 
				defaultValue
				);

	}

	@Override
	public Map<String,Boolean> check(String value) {

		ListOfMessages messages = new ListOfMessages();
		JavaScriptingExpressionParser parser = new JavaScriptingExpressionParser();
		
		Map<String,Boolean> res = new HashMap<String, Boolean>();
		
		try {
			Object computedResult = parser.evaluate(value, messages, null);
		} catch (WrongParametersException e) {
			res.put("an error occured during the javascript parsing: "+e.getCause().getLocalizedMessage()+"; it could work at runtime thanks to variables declarations... or not.", false);
		}
		
		return res;
	}

	@Override
	public String parseFromString(String value) {
		return value;
	}

}
