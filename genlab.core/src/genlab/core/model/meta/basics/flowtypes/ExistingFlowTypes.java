package genlab.core.model.meta.basics.flowtypes;

import java.util.HashMap;
import java.util.Map;

public class ExistingFlowTypes {

	private static Map<String,AbstractFlowType> id2type = new HashMap<String,AbstractFlowType>();
	
	public static void registerType(AbstractFlowType t) {
		id2type.put(t.getId(), t);
	}
	
	public static AbstractFlowType getType(String id) {
		return id2type.get(id);
	}
	
	private ExistingFlowTypes() {
		
	}

}
