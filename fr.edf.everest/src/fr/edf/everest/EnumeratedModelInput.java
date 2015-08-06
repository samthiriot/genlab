package fr.edf.everest;

import java.util.List;

import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;

/**
 * Represents an Everest model input that maps to an enumarated type. 
 * It means it will be published in Genlab as an Integer input with the 
 * relevant min and max; then the value will be converted to the corresponding value
 * before transmission to Everest 
 * 
 * @author B12772
 *
 */
public abstract class EnumeratedModelInput extends ModelInput {

	protected final List<String> possibleValues; 
	
	public EnumeratedModelInput(
			String PIDname, String PID,
			String attributeTechName,
			List<String> possibleValues
			) {
		super(PIDname, PID, attributeTechName);

		this.possibleValues = possibleValues;
		
		this.genlabInputs.add(new IntegerInOut(
				"in_"+PID+"_"+attributeTechName, 
				PIDname+"/"+attributeTechName, 
				"", 
				0, 
				0, 
				possibleValues.size()-1
				)
		);
	}
	
	protected String getEverestEnumValueForIndex(Integer idx) {
		return this.possibleValues.get(idx);
	}

}
