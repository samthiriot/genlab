package genlab.arithmetics;

import genlab.arithmetics.javaScripting.JavaScriptingExpressionParser;

public class ParserFactory {

	private ParserFactory() {
		
	}
	
	public static IExpressionsParser getDefaultExpressionParser() {
		return new JavaScriptingExpressionParser();
	}

}
