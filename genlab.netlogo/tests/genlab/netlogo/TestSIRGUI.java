package genlab.netlogo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import genlab.core.usermachineinteraction.ListOfMessages;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class TestSIRGUI extends TestModelBehaviour {

	public TestSIRGUI() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Map<String, Object> runModel() {

		ListOfMessages msg = new ListOfMessages();
		
		Map<String,Object> inputs = new HashMap<String, Object>();
		inputs.put("network-filename", "../../testdata/networks/ws1.net");
		
		inputs.put("initial-outbreak-size", 1);
		inputs.put("virus-spread-chance", 10);
		inputs.put("virus-check-frequency", 1);
		inputs.put("gain-resistance-chance", 100);
		inputs.put("recovery-chance", 10);
		inputs.put("is-graphical", true);

		
		Collection<String> requiredOutputs = new LinkedList<String>();
		requiredOutputs.add("measure-susceptible");
		requiredOutputs.add("measure-infected");
		requiredOutputs.add("measure-resistant");
				
		return RunNetlogoModel.runNetlogoModelGraphical(
				msg, 
				"./ressources/models/Virus on a Network.nlogo", 
				inputs, 
				requiredOutputs,
				500,
				null
				);
	}

	@Override
	protected void checkResult(Map<String, Object> result) {

		assertTrue(result.get("measure-susceptible") instanceof Double);
		assertTrue(result.get("measure-infected") instanceof Double);
		assertTrue(result.get("measure-resistant") instanceof Double);
		assertEquals(0.0, result.get("measure-susceptible"));
		assertEquals(100.0, result.get("measure-resistant"));
	}

}
