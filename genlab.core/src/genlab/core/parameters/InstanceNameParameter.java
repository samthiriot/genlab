package genlab.core.parameters;

import genlab.core.model.instance.IAlgoInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Specific parameter which enabling the tuning of the name 
 * of an algo instance as a parameter.
 * Will not be save as a standard parameter.
 * 
 * @author Samuel Thiriot
 *
 */
public class InstanceNameParameter extends StringParameter {

	private final IAlgoInstance algoInstance;
	
	public InstanceNameParameter(IAlgoInstance algoInstance, String id, String name, String desc,
			String defaultValue) {
		super(id, name, desc, defaultValue);
		this.algoInstance = algoInstance;
		this.shouldSave = false;
	}

	@Override
	public Map<String, Boolean> check(String something) {
		
		if (algoInstance.getWorkflow().getAlgoInstanceForName(something)!=null) {
			HashMap<String,Boolean> res = new HashMap<String, Boolean>();
			res.put("the name "+something+" is already used by another algorithm; please choose another name", Boolean.TRUE);
			return res;
		} else {
			return super.check(something);
		}
	}
	
	

}
