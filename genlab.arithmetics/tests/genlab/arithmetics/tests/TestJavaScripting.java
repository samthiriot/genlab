package genlab.arithmetics.tests;

import genlab.arithmetics.IExpressionsParser;
import genlab.arithmetics.javaScripting.JavaScriptingExpressionParser;

public class TestJavaScripting extends AbstractTestExpressionsParser {

	public TestJavaScripting() {
		super(new JavaScriptingExpressionParser());
		
	}

}
