package genlab.core.model.meta;

import genlab.core.model.meta.basics.flowtypes.IntegerInOut;

public class IntegerExplorationParameter extends AbstractExplorationParameter {

	public static final IntegerInOut OUT_VALUE = new IntegerInOut("out_value", "value", "the value explored");
	
	public IntegerExplorationParameter() {
		
		super("integer parameter", "explore an integer parameter", OUT_VALUE);
		
		
	}

	@Override
	public Object castValue(Object value) {
		try {
			return (Integer)value;
		} catch (ClassCastException e) {
			return ((Double)value).intValue();
		}
	}

}
