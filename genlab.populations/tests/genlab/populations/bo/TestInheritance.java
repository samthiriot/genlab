package genlab.populations.bo;

import static org.junit.Assert.*;
import genlab.core.commons.WrongParametersException;
import genlab.populations.bo.Attribute;
import genlab.populations.bo.AttributeType;
import genlab.populations.implementations.basic.AgentType;
import genlab.populations.implementations.basic.Population;

import org.junit.Test;

public class TestInheritance {


	@Test
	public void testAutoLoopRaisesErrors() {

		AgentType a1 = new AgentType("t1", "t1");
		
		try {
			a1.addInheritedTypes(a1);
			fail("a WrongParameterException should have been raised because of the loop");
		} catch (WrongParametersException e) {
			// is expected
		}
	}


	@Test
	public void testSimpleLoopRaisesErrors() {

		AgentType a1 = new AgentType("t1", "t1");
		AgentType a2 = new AgentType("t2", "t2");
		AgentType a3 = new AgentType("t3", "t3");
		
		a3.addInheritedTypes(a2);
		a2.addInheritedTypes(a1);
		try {
			a1.addInheritedTypes(a3);
			fail("a WrongParameterException should have been raised because of the loop");
		} catch (WrongParametersException e) {
			// is expected
		}
	}


	@Test
	public void testMultipleLoopRaisesErrors() {

		AgentType a1 = new AgentType("t1", "t1");
		AgentType a2a = new AgentType("t2a", "t2a");
		AgentType a2b = new AgentType("t2a", "t2a");
		AgentType a3 = new AgentType("t3", "t3");
		
		a2a.addInheritedTypes(a1);
		a2b.addInheritedTypes(a1);
		a3.addInheritedTypes(a2a);
		a3.addInheritedTypes(a2b);
		try {
			a1.addInheritedTypes(a3);
			fail("a WrongParameterException should have been raised because of the loop");
		} catch (WrongParametersException e) {
			// is expected
		}
	}


	@Test
	public void testAttributeRedefinitionAtInheritanceRaisesErrors() {

		AgentType a1 = new AgentType("t1", "t1");
		a1.addAttribute(new Attribute("aa", AttributeType.DOUBLE));
		AgentType a2 = new AgentType("t2", "t2");
		AgentType a3 = new AgentType("t3", "t3");
		a3.addAttribute(new Attribute("aa", AttributeType.DOUBLE));
		
		// should work
		a2.addInheritedTypes(a1);
		try {
			a3.addInheritedTypes(a2);
			fail("a WrongParameterException should have been raised because of the redondancy on the attribute aa");
		} catch (WrongParametersException e) {
			// is expected
		}
	}

	@Test
	public void testAttributeRedefinitionAtAddRaisesErrors() {

		// should work
		AgentType a1 = new AgentType("t1", "t1");
		a1.addAttribute(new Attribute("aa", AttributeType.DOUBLE));
		AgentType a2 = new AgentType("t2", "t2");
		a2.addInheritedTypes(a1);
		AgentType a3 = new AgentType("t3", "t3");
		a3.addInheritedTypes(a2);

		try {
			a3.addAttribute(new Attribute("aa", AttributeType.DOUBLE));
			fail("a WrongParameterException should have been raised because of the redondancy on the attribute aa");
		} catch (WrongParametersException e) {
			// is expected
		}
	}
	

	@Test
	public void testAgentTypesIndexesd() {

		// should work
		AgentType a1 = new AgentType("t1", "t1");
		AgentType a2 = new AgentType("t2", "t2");
		a2.addInheritedTypes(a1);
		AgentType a2b = new AgentType("t2b", "t2b");
		AgentType a3 = new AgentType("t3", "t3");
		a3.addInheritedTypes(a2);
		a3.addInheritedTypes(a2b);

		PopulationDescription pd = new PopulationDescription();
		pd.addAgentType(a1);
		pd.addAgentType(a2);
		pd.addAgentType(a2b);
		pd.addAgentType(a3);
		
		Population p = new Population(pd);
		
		try {
			IAgent type3a1 = p.createAgent(a1);
			fail("should raise an exception because this type is abstract");
		} catch (WrongParametersException e) {
			// is expected
		}
		
		IAgent type3a1 = p.createAgent(a3);
		IAgent type3a2 = p.createAgent(a3);
		
		assertEquals(p.getTotalAgentsCount(), 2);
		assertEquals(p.getAgentsCount(a1), 2);
		assertEquals(p.getAgentsCount(a2), 2);
		assertEquals(p.getAgentsCount(a2b), 2);
		assertEquals(p.getAgentsCount(a3), 2);
		
	}




}
