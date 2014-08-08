package genlab.population.yang;

import genlab.bayesianinference.IBayesianNetwork;
import genlab.bayesianinference.smile.SmileUtils;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.usermachineinteraction.ListsOfMessages;
import genlab.populations.bo.Attribute;
import genlab.populations.bo.AttributeType;
import genlab.populations.bo.PopulationDescription;
import genlab.populations.implementations.basic.AgentType;
import genlab.populations.implementations.basic.Population;

import org.junit.Test;

public class TestYangAlgos {

	public static final String fileWeather = "../genlab.population.yang/tests/genlab/population/yang/weather.net";


	@Test
	public void testGenerateIndividual() {
		
		// create type
		AgentType t = new AgentType("type1", "type test");
		t.addAttribute(new Attribute("weather", AttributeType.STRING));
		t.addAttribute(new Attribute("rain", AttributeType.STRING));
		
		// create pop description
		PopulationDescription pd = new PopulationDescription();
		pd.addAgentType(t);
		
		Population pop = new Population(pd);
		
		IBayesianNetwork bn = SmileUtils.readFromFile(fileWeather);
				
		YANGAlgos.fillPopulationFromBN(
				new ComputationProgressWithSteps(), 
				pop, 
				t, 
				ListsOfMessages.getGenlabMessages(), 
				bn, 
				100
				);
		
		
	}

}
