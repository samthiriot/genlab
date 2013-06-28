package genlab.core.model.meta.basics.flowtypes;

import genlab.core.commons.WrongParametersException;

public class IntegerFlowType extends AbstractFlowType<Integer> {

	public static IntegerFlowType SINGLETON = new IntegerFlowType();

	protected IntegerFlowType() {
		super(
				"core.types.integer",
				"integer", 
				"an integer value"
				);
	}

	@Override
	public Integer decodeFrom(Object value) {
		if (value instanceof String)
			try {
				return Integer.parseInt((String)value);
			} catch (NumberFormatException e) {
				throw new WrongParametersException("unable to decode integer from "+value);
			}
		else
			try {
				return (Integer)value;
			} catch (ClassCastException e) {
				throw new WrongParametersException("unable to cast integer from "+value);
			}
		
	}

}
