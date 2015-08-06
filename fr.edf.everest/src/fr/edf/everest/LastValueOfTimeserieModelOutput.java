package fr.edf.everest;

import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * For a timeserie produced by an Everest model,  
 * takes the very last value and returns it
 * @author B12772
 *
 */
public class LastValueOfTimeserieModelOutput extends ModelOutput {

	public LastValueOfTimeserieModelOutput(String entityTypeName, String PID, String PIDname,
			String KPI) {
		super(entityTypeName, PID, KPI);

		this.genlabOutputs.add(new DoubleInOut(
				"out_"+PID+"_"+KPI, 
				PIDname+"/"+KPI, 
				"KPI "+KPI+" of the "+entityTypeName+" of PID "+PID
				)
		);
	}

	@Override
	public Map<IInputOutput<?>, Object> getOutputValuesForGenlab(List<Double> everestOutput) {
		
		Map<IInputOutput<?>, Object>  res = new HashMap<IInputOutput<?>, Object>();
		
		
		res.put(
				// retrieve the allegedly only output
				genlabOutputs.get(0),
				// and return for this output the corresponding last value 
				everestOutput.get(everestOutput.size()-1)
				);
		
		return res;
	}

}
