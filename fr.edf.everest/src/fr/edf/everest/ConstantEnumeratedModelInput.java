package fr.edf.everest;

import genlab.core.model.meta.IInputOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConstantEnumeratedModelInput extends EnumeratedModelInput {

	public ConstantEnumeratedModelInput(String PIDname, String PID,
			String attributeTechName, List<String> possibleValues) {
		super(PIDname, PID, attributeTechName, possibleValues);
	}

	@Override
	public List<?> getEverestValue(Map<IInputOutput<?>,Object> genlabInput2value) {

		// we have only one input here, let's load its value
		Object inputValueGenlabRaw = genlabInput2value.get(this.genlabInputs.get(0));
		
		// for enumarated types, we send a String to Everest
		List<String> res = new ArrayList<String>();
		
		// the input value from Genlab should be a int
		Integer inputValueGenlab = (Integer)inputValueGenlabRaw;
		
		// finds the corresponding mapped value for Everest
		String valueEverest = getEverestEnumValueForIndex(inputValueGenlab);
		
		// because this is a constant time serie, we return only one timestep with the value
		res.add(valueEverest );
		
		return res;
		
	}
	
}
