package genlab.core.model.meta.basics.flowtypes;

import genlab.core.commons.WrongParametersException;

public class DoubleFlowType extends AbstractFlowType<Double> {

	public static DoubleFlowType SINGLETON = new DoubleFlowType();
	
	protected DoubleFlowType() {
		super(
				"core.types.double",
				"double", 
				"a double value"
				);
	}
	
	@Override
	public Double decodeFrom(Object value) {
		if (value instanceof String)
			try {
				return Double.parseDouble((String)value);
			} catch (NumberFormatException e) {
				throw new WrongParametersException("unable to decode double from String "+value, e);
			}
		else
			try {
				return (Double)value;
			} catch (ClassCastException e) {
				throw new WrongParametersException("unable to cast double from value "+value, e);
			}
		
	}



}
