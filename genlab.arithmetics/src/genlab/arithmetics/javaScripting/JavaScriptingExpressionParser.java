package genlab.arithmetics.javaScripting;

import genlab.arithmetics.IExpressionsParser;
import genlab.core.commons.ProgramException;
import genlab.core.commons.WrongParametersException;
import genlab.core.usermachineinteraction.ListOfMessages;

import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JavaScriptingExpressionParser implements IExpressionsParser {

	public static final String DEFAULT_SCRIPTING_ENGINE = "js";
	
	protected final ScriptEngineManager manager;
	
	protected boolean printedInfos = false;
	
	/**
	 * list of Math.op operators to import
	 */
	protected static final String[] importJSMathOperators = new String[]{
		"ceil", "floor", "round",
		"PI","E",
		"sqrt",
		"cos","sin","tan",
		"acosh","asinh","atanh",
		"abs"
	};
	
	protected static final String importJSMathOperatorsStr;
	
	static {
	
		StringBuffer sb = new StringBuffer("var ");
		boolean comma = false;
		for (String s: importJSMathOperators) {
			
			if (comma)
				sb.append(", \n");
			else
				comma = true;
			
			sb.append(s).append("=Math.").append(s);
		}
		
		sb.append(";");
		importJSMathOperatorsStr = sb.toString();

	}
	
	
	public JavaScriptingExpressionParser() {
		
		manager = new ScriptEngineManager();
		
	}
	
	protected void printInfos(ListOfMessages messages, ScriptEngine engine) {
		
		messages.infoUser("for evaluation, using the scripting engine "+engine.NAME+" ("+engine.ENGINE_VERSION+")", getClass());
		
		printedInfos = true;
	}

	@Override
	public Object evaluate(
			String expr, 
			ListOfMessages messages,
			Map<String, Object> variables
			) {
		
		final ScriptEngine engine = manager.getEngineByName(DEFAULT_SCRIPTING_ENGINE);
		
		if (!printedInfos)
			printInfos(messages, engine);
		
		
		// add all the variables
		for (String key: variables.keySet()) {
			engine.put(key, variables.get(key));
		}
		
		// add required functions
		try {
			engine.eval(importJSMathOperatorsStr);
			
			
		} catch (ScriptException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new ProgramException("error while trying to declare basic functions: "+e1.getMessage(), e1);
		}
		
		Object res  = null;
		try {
		
			res = engine.eval(expr);
		
		} catch (ScriptException e) {
			String msg = "An error occured during the evaluation of the expression \""
						+expr
						+"\": "
						+e.getMessage();
			messages.errorUser(msg, getClass(), e);
			throw new WrongParametersException(msg, e);
		}

		// post process value
		Object ppRes = res;
		
		if (res instanceof Double) {
			Double dRes = (Double) res;
			if (dRes == Math.rint(dRes)) {
				// can be considered as int !
				ppRes = dRes.intValue();
			}
		}
		
		//System.out.println(res.getClass());
		//System.out.println(res);
		
		return ppRes;
		
	}

	@Override
	public String getAllPossibleSyntaxesShort() {
		StringBuffer sb = new StringBuffer();
		sb.append("+,-,*,/");
		// TODO describe syntax
		return sb.toString();
	}

}
