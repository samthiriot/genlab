package genlab.basics.flow;

import genlab.core.algos.WrongParametersException;
import genlab.core.flow.IFlowType;

public class IntegerFlowType implements IFlowType<Integer> {

	@Override
	public String getShortName() {
		return "integer";
	}

	@Override
	public String getDescription() {
		return "an integer value";
	}

	@Override
	public String getHtmlDescription() {
		return getDescription();
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
