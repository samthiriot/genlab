package genlab.core.model.meta.basics.flowtypes;

import genlab.core.commons.WrongParametersException;
import genlab.core.model.meta.IFlowType;

/**
 * A number flow type is compliant with Double or Integers
 * 
 * @author Samuel Thiriot
 *
 */
public class NumberFlowType extends AbstractFlowType<Number> {

	public static NumberFlowType SINGLETON = new NumberFlowType();
	
	protected NumberFlowType() {
		super(
				"core.types.number",
				"number", 
				"a number (float or integer)"
				);
	}
	
	@Override
	public Number decodeFrom(Object value) {
		if (value instanceof String) {
			// maybe we can decode it as a Integer ?
			try {
				return Integer.parseInt((String)value);
			} catch (NumberFormatException e) {
			}
			try {
				return Double.parseDouble((String)value);
			} catch (NumberFormatException e) {
				throw new WrongParametersException("unable to decode integer from "+value);
			}
		} else
			try {
				return (Number)value;
			} catch (ClassCastException e) {
				throw new WrongParametersException("unable to cast integer from "+value);
			}
		
	}

	@Override
	public boolean compliantWith(IFlowType<?> other) {
		return other.getId().equals(id) 
				|| other.getId().equals(DoubleFlowType.SINGLETON.id) 
				|| other.getId().equals(IntegerFlowType.SINGLETON.id);
	}


}
