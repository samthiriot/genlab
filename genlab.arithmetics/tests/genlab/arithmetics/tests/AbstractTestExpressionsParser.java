package genlab.arithmetics.tests;

import static org.junit.Assert.assertEquals;
import genlab.arithmetics.IExpressionsParser;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;
import genlab.core.usermachineinteraction.MessageLevel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AbstractTestExpressionsParser {

	public final IExpressionsParser ep;

	public AbstractTestExpressionsParser(IExpressionsParser ep) {
		this.ep = ep;
	}
	
	@Before
	public void setUp() throws Exception {

		ListOfMessages.DEFAULT_RELAY_TO_LOG4J = true;
		ListsOfMessages.getGenlabMessages().setFilterIgnoreBelow(MessageLevel.TRACE);
		ListsOfMessages.getGenlabMessages().debugTech("test", getClass());

		
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		
		ListOfMessages.DEFAULT_RELAY_TO_LOG4J = true;
		ListsOfMessages.getGenlabMessages().setFilterIgnoreBelow(MessageLevel.TRACE);
		ListsOfMessages.getGenlabMessages().infoTech("test", AbstractTestExpressionsParser.class);

		
	}

	protected void testShouldWork(String p, Object expectedResult) {
		
		testShouldWork(p, expectedResult, Collections.EMPTY_MAP);
		
	}
	protected void testShouldWork(String p, Object expectedResult, Map<String, Object> variables) {
	
		final Object runId = new Object();
				
		try {
			
			// compute value
			Object res = ep.evaluate(p, ListsOfMessages.getGenlabMessages(), variables);
			
			// check it
			assertEquals(
					"the result of the computation is does the one expected", 
					expectedResult,
					res
					);
		} finally {
			// nothing ? 
		}
		
	}
	
	
	
	@Test
	public void testPlus() {
		
		// official, 1 space
		testShouldWork("1 + 1", 2);
		testShouldWork("1 + 0", 1);
		testShouldWork("-1 + 1", 0);
		
		// non official, 2 spaces
		testShouldWork("1  +  1", 2);
		testShouldWork("-1  +  1", 0);

		// non official, 0 space
		// TODO space ??? testShouldWork("1+1", 2);

	}


	@Test
	public void testMinus() {
	
		// official, 1 space
		testShouldWork("1 - 1", 0);
		testShouldWork("0 - 0", 0);
		testShouldWork("-1 - 1", -2);
		
	}
	

	@Test
	public void testMultSpace() {
		
		// official, 1 space
		testShouldWork("1 * 1", 1);
		testShouldWork("0 * 0", 0);
		testShouldWork("0 * 100", 0);
		testShouldWork("2 * 2", 4);
		testShouldWork("-2 * 2", -4);
		testShouldWork("-2 * -2", 4);
	}

	@Test
	public void testMultNoSpace() {
		
		testShouldWork("1*1", 1);
		testShouldWork("0*0", 0);
		testShouldWork("0*100", 0);
		testShouldWork("2*2", 4);
		testShouldWork("-2*2", -4);
		testShouldWork("-2*-2", 4);
	}


	@Test
	public void testDiv() {
		
		testShouldWork("1 / 1", 1);
		testShouldWork("0 / 12", 0);
		testShouldWork("12 / 3", 4);
		
		testShouldWork("12 / -3", -4);
		testShouldWork("12 / 3", 4);

		testShouldWork("-(12/3)", -4);

	}
	

	@Test
	public void testBoolTypes() {
		
		testShouldWork("true", true);
		testShouldWork("false", false);
				
	}

	@Test
	public void testBoolOR() {
		
		testShouldWork("true || false", true);
		testShouldWork("false || false", false);
		
		testShouldWork("false || false", false);
		testShouldWork("false || true", true);
		testShouldWork("true || true", true);
		
		
	}

	/*
	@Test
	public void testBoolXOR() {
		
		testShouldWork("true XOR false", true);
		testShouldWork("true XOR true", false);
		testShouldWork("false XOR true", true);
		
		
	}
	*/

	@Test
	public void testBoolAnd() {
		
		testShouldWork("true && false", false);
		testShouldWork("false && false", false);
		
		testShouldWork("false && false", false);
		testShouldWork("false && true", false);
		testShouldWork("true && true", true);
		
	}

	@Test
	public void testFunctions() {
		
		testShouldWork("floor(3.25)", 3);
		testShouldWork("ceil(3.25)", 4);
		testShouldWork("round(3.25)", 3);
		testShouldWork("round(3.25)", 3);

		testShouldWork("cos(0)", 1);
		// TODO testShouldWork("cos(PI/4)", 0);
		// TODO testShouldWork("cos(0.5)", 0);

		// TODO to be completed
	}
	
	@Test
	public void testVariables() {
		
		Map<String,Object> vars1 = new HashMap<String,Object>();
		vars1.put("f", 2.1231);
		vars1.put("i", 144);
		vars1.put("s1", "123");
		

		testShouldWork("f", 2.1231, vars1);
		testShouldWork("i", 144, vars1);
		
		testShouldWork("f+0", 2.1231, vars1);
		testShouldWork("i+0+1-1", 144, vars1);
		
		testShouldWork("s1", "123", vars1);
		
		
	}
	

	@Test
	public void testConstants() {
		
		testShouldWork("PI", Math.PI);
		testShouldWork("E", Math.E);
		
		
	}
	

	@Test
	public void testVariablesAndFunctions() {
		
		Map<String,Object> vars1 = new HashMap<String,Object>();
		vars1.put("f", 2.1231);
		vars1.put("i", 144);
		
		testShouldWork("floor(f)", 2, vars1);
		testShouldWork("ceil(f)", 3, vars1);
		testShouldWork("round(i)", 144, vars1);
		testShouldWork("round(f)", 2, vars1);
		testShouldWork("sqrt(i)", 12, vars1);
		testShouldWork("( f>=2 ? \"sup1\" : \"moins1\")", "sup1", vars1);
		
		// TODO to be completed
	}
	

	@Test
	public void testParenthesis() {
		
		testShouldWork("(2+2)*3", 12);
		testShouldWork("2+(2*3)", 8);
		testShouldWork("2+2*3", 8);
		testShouldWork("((2+(2))*3)", 12);
		
	}
	

	@Test
	public void testArrays() {
		
		Map<String,Object> vars1 = new HashMap<String,Object>();
		vars1.put("v", 2);
		vars1.put("s", 0);
		
		testShouldWork("[1,2,3,4][v]", 3, vars1);
		testShouldWork("[1,2,3,4][0]", 1, vars1);
		testShouldWork("[1,2,3,4][s]", 1, vars1);
		
		testShouldWork("['1','2','3','4'][s]", "1", vars1);
		testShouldWork("['1','2','3','4'][v]", "3", vars1);
		
	}

	@Test
	public void testSwitch() {
		
		testShouldWork("switch (12) { case 12: true; break; } ", Boolean.TRUE);
		
	}


}
