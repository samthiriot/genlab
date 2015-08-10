package fr.edf.everest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IInputOutput;

/**
 * Represents an input for an Everest model.
 * Manages the creation of the corresponding Genlab inputs for the algorithm, 
 * how to change from the Genlab input to an Everest model input, etc.
 * 
 * @author B12772
 *
 */
public abstract class ModelInput {

	public final String PIDname;
	public final String PID;
	public final String attributeTechName;
	
	protected final List<IInputOutput<?>> genlabInputs;
	
	
	/**
	 * Inherited classes should feed the genlab inputs genlabInputs
	 * @param PIDname
	 * @param PID
	 * @param attributeTechName
	 */
	public ModelInput(String PIDname, String PID, String attributeTechName) {
		this.PIDname = PIDname;
		this.PID = PID;
		this.attributeTechName = attributeTechName;
		
		// 
		this.genlabInputs = new ArrayList<>();
	}

	/**
	 * returns the genlab inputs to create for the algorithm for this Everest model input.
	 * Usually it will just be one input, but sometimes it might be required to have several; 
	 * for instance to define the novel value and when to change it.
	 * @return
	 */
	public List<IInputOutput<?>> getGenlabInputs() {
		return genlabInputs;
	}
	
	/**
	 * From a Genlab value received by the genlab algorithm at runtime, 
	 * returns the value to send to Everest. It should always be a timeserie, 
	 * but it might be a constant one  
	 * @return
	 */
	public abstract List<?> getEverestValue(Map<IInputOutput<?>,Object> genlabInput2value);
	
	
}
