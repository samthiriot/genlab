package genlab.core.model.meta.basics.flowtypes;

import genlab.core.commons.WrongParametersException;

public class BooleanFlowType extends AbstractFlowType<Boolean> {

	public BooleanFlowType() {
		super(
				"core.types.boolean",
				"boolean", 
				"a boolean value (true or false)"
				);
	}
	
	@Override
	public Boolean decodeFrom(Object value) {
		if (value instanceof Boolean)
			try {
				return (Boolean)value;
			} catch (ClassCastException e) {
				throw new WrongParametersException("unable to decode boolean from "+value);
			}
		else if (value instanceof String)
			try {
				return Boolean.parseBoolean((String)value);
			} catch (ClassCastException e) {
				throw new WrongParametersException("unable to decode boolean from "+value);
			}
		else 
			throw new WrongParametersException("unable to decode boolean from "+value);
		
	}



}
