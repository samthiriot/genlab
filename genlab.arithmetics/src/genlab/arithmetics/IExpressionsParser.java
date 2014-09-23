package genlab.arithmetics;

import java.util.Map;

import genlab.core.usermachineinteraction.ListOfMessages;

public interface IExpressionsParser {

	public Object evaluate(String expr, ListOfMessages messages, Map<String,Object> variables);
	
	/**
	 * Returns the short version of the syntax (only operators without details)
	 * @return
	 */
	public String getAllPossibleSyntaxesShort();

	Object evaluate(String expr, ListOfMessages messages);
	
}
