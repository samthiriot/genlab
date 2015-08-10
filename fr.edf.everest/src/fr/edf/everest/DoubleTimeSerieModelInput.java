package fr.edf.everest;

import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DoubleTimeSerieModelInput extends ModelInput {

	private final Double[] baseValues;
	
	private final int whenToChange;
	
	/**
	 * 
	 * @param PIDname
	 * @param PID
	 * @param attributeTechName
	 * @param min
	 * @param max
	 * @param baseValue
	 * @param defaultValue
	 * @param whenToChange
	 */
	public DoubleTimeSerieModelInput(
			String PIDname, String PID, String attributeTechName, 
			Double min, Double max, 
			Double[] baseValues,
			Double defaultValue,
			int whenToChange
			) {
		
		super(PIDname, PID, attributeTechName);
		
		this.baseValues = baseValues;
		this.whenToChange = whenToChange;
		
		this.genlabInputs.add(new DoubleInOut(
				"in_"+PID+"_"+attributeTechName, 
				PIDname+"/"+attributeTechName, 
				"", 
				defaultValue, 
				min, 
				max
				)
		);
	}

	@Override
	public List<?> getEverestValue(Map<IInputOutput<?>,Object> genlabInput2value) {
		
		// we have only one input here, let's load its value
		Object inputValueGenlabRaw = genlabInput2value.get(this.genlabInputs.get(0));
		
		// the value for this parameter should be a double
		Double inputValueGenlab = (Double)inputValueGenlabRaw; 
		
		// the result is a timeserie of the same length as the default time serie
		ArrayList<Double> res = new ArrayList<>(baseValues.length);
		
		// we copy inside it the beginning of the time serie...
		int i = 0;
		for (; i<this.whenToChange; i++) {
			res.add(baseValues[i]);
		}
		// ... then we complete with the novel value 
		for (; i< baseValues.length; i++) {
			res.add(inputValueGenlab);
		}
		
		return res;
	}

}
