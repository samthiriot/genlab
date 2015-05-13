package genlab.algog.algos.meta;

import genlab.core.commons.ProgramException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Crossover methods available
 * 
 * @author Samuel Thiriot
 *
 */
public enum ECrossoverMethod {
	
	SBX ("SBX (Simulated binary crossover)"),
	N_POINTS ("NPOINTS")
	;
	
	public final String label;
	
	private static Map<String, ECrossoverMethod> label2value = new HashMap<String, ECrossoverMethod>();

	private ECrossoverMethod (String label) {
		this.label = label;
	}

	public static ECrossoverMethod parseFromLabel(String label) {
		return label2value.get(label);
	}
	
	public static List<String> getLabelsAsList() {
		
		List<String> res = new LinkedList<String>();
		for (ECrossoverMethod e: values()) {
			res.add(e.label);
		}
		return res;
	}
	
	static {
		
		// cache the map from label to enum value
		for (ECrossoverMethod value : ECrossoverMethod.values()) {
			
			// refuse double values
			if (label2value.containsKey(value.label)) {
				throw new ProgramException("label "+value.label+" was defined several times in "+ECrossoverMethod.class.getCanonicalName());
			}
		
			label2value.put(value.label, value);
		}
		
	}
}
