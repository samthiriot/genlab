package genlab.netlogo;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import genlab.core.usermachineinteraction.ListOfMessages;

import org.junit.Test;

public class TestBasicRunModel {

	@Test
	public void testVirusOnNetwork() {
		ListOfMessages msg = new ListOfMessages();
		
		Map<String,Object> inputs = new HashMap<String, Object>();
		inputs.put("network-filename", "../networks/ws1.net");
		
		inputs.put("initial-outbreak-size", 1);
		inputs.put("virus-spread-chance", 100);
		inputs.put("virus-check-frequency", 1);
		inputs.put("recovery-chance", 10);
		inputs.put("gain-resistance-chance", 100);
		
		
		Collection<String> requiredOutputs = new LinkedList<String>();
		requiredOutputs.add("measure-susceptible");
		requiredOutputs.add("measure-infected");
		requiredOutputs.add("measure-resistant");
				
		Map<String,Object> result = RunNetlogoModel.runNetlogoModel(
				msg, 
				"./testdata/models/Virus on a Network.nlogo", 
				inputs, 
				requiredOutputs,
				500
				);
		
		assertTrue(result.get("measure-susceptible") instanceof Double);
		assertTrue(result.get("measure-infected") instanceof Double);
		assertTrue(result.get("measure-resistant") instanceof Double);
		assertEquals(0.0, result.get("measure-susceptible"));
		assertEquals(100.0, result.get("measure-resistant"));

	}

}
