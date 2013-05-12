package genlab.basics.flow;

import genlab.core.algos.WrongParametersException;
import genlab.core.flow.IFlowType;

public class DoubleFlowType implements IFlowType<Double> {

	@Override
	public String getShortName() {
		return "double";
	}

	@Override
	public String getDescription() {
		return "a double value";
	}

	@Override
	public String getHtmlDescription() {
		return getDescription();
	}
	
	@Override
	public Double decodeFrom(Object value) {
		if (value instanceof String)
			try {
				return Double.parseDouble((String)value);
			} catch (NumberFormatException e) {
				throw new WrongParametersException("unable to decode integer from "+value);
			}
		else
			try {
				return (Double)value;
			} catch (ClassCastException e) {
				throw new WrongParametersException("unable to cast integer from "+value);
			}
		
	}

}
