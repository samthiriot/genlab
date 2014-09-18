package genlab.population.yang;

import genlab.bayesianinference.IBayesianNetwork;
import genlab.bayesianinference.smile.SmileUtils;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.usermachineinteraction.ListsOfMessages;
import genlab.populations.bo.Attribute;
import genlab.populations.bo.AttributeType;
import genlab.populations.bo.IAgent;
import genlab.populations.bo.PopulationDescription;
import genlab.populations.implementations.basic.AgentType;
import genlab.populations.implementations.basic.Population;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests directly the YANG algorithms. "Direct" means: not their encapsulation inside 
 * genlab.
 * 
 * @author Samuel Thiriot
 *
 */
public class TestYangAlgosDirect {

	public static final String fileWeather = "../genlab.population.yang/tests/genlab/population/yang/weather.net";


	protected void checkScratchGenerationOfN(int popSize) {

		// create type
		AgentType t = new AgentType("type1", "type test");
		t.addAttribute(new Attribute("weather", AttributeType.STRING));
		t.addAttribute(new Attribute("rain", AttributeType.STRING));
		
		// create pop description
		PopulationDescription pd = new PopulationDescription();
		pd.addAgentType(t);
		
		Population pop = new Population(pd);
		
		IBayesianNetwork bn = SmileUtils.readFromFile(fileWeather);
				
		YANGAlgosIndividuals.fillPopulationFromBN(
				new ComputationProgressWithSteps(), 
				pop, 
				t, 
				ListsOfMessages.getGenlabMessages(), 
				bn, 
				popSize
				);
		
		Assert.assertEquals(
				"population has not the expected size", 
				popSize, 
				pop.getTotalAgentsCount()
				);
		
		for (IAgent a: pop.getAgents()) {
			Assert.assertNotNull(a.getId());
			Assert.assertNotNull(a.getAgentType());
			Assert.assertEquals("type1", a.getAgentType().getName());
			Assert.assertNotNull(a.getValueForAttribute("weather"));
			Assert.assertNotNull(a.getValueForAttribute("rain"));
		}
		
	}
	
	@Test
	public void testScratchGenerateIndividuals() {
		checkScratchGenerationOfN(100);
	}

	@Test
	public void testScratchGenerateOneIndividual() {
		checkScratchGenerationOfN(1);
	}

	@Test
	public void testScratchGenerateNoIndividual() {
		checkScratchGenerationOfN(0);
	}

	@Test
	public void testScratchGenerateManyIndividuals() {
		checkScratchGenerationOfN(50000);
	}

}
