package genlab.igraph.algos.generation;

import genlab.core.parameters.StringBasedParameter;

import java.util.HashMap;
import java.util.Map;

public class LCFParameter extends StringBasedParameter<LCF> {

	
	public LCFParameter(String id, String name, String desc,  LCF defaultValue) {
		super(id, name, desc, defaultValue);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map<String, Boolean> check(LCF value) {
		Map<String, Boolean> res = new HashMap<String, Boolean>(10);
		if (value.count < 0) {
			res.put("the count should be positive", true);
		}
		if (value.shifts.length < 1) {
			res.put("at least one shift should be provided", true);
		}
		return res;
	}

	@Override
	public LCF parseFromString(String str) {
		
		return LCF.parseFromString(str);
		
	}

	
}
