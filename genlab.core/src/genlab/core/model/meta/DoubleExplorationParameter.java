package genlab.core.model.meta;

import genlab.core.model.meta.basics.flowtypes.DoubleInOut;

public class DoubleExplorationParameter extends AbstractExplorationParameter {

	public static final DoubleInOut OUT_VALUE = new DoubleInOut("out_value", "value", "the value explored");
	
	public DoubleExplorationParameter() {
		
		super("double parameter", "explore a double parameter", OUT_VALUE);
		
		
	}

	@Override
	public Object castValue(Object value) {
		try {
			return (Double)value;
		} catch (ClassCastException e) {
			return ((Integer)value).doubleValue();
		}
	}

}
