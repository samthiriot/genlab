package genlab.core.model.meta.basics.flowtypes;

import genlab.core.model.meta.IFlowType;

/**
 * A type which can be anything (well, any object)
 * 
 * @author Samuel Thiriot
 *
 */
public class AnythingFlowType extends AbstractFlowType<Object> {

	public static AnythingFlowType SINGLETON = new AnythingFlowType();

	protected AnythingFlowType() {
		super(				
				"core.types.anything",
				"anything", 
				"a value"
				);
	}

	@Override
	public Object decodeFrom(Object value) {
		return value;
	}
	
	@Override
	public boolean compliantWith(IFlowType<?> other) {
		// special case: always accepted :-)
		return true;
	}

}
