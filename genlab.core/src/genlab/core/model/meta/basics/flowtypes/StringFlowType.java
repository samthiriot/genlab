package genlab.core.model.meta.basics.flowtypes;


public class StringFlowType extends AbstractFlowType<String> {

	public static StringFlowType SINGLETON = new StringFlowType();

	public StringFlowType() {
		super(
				"core.types.string", 
				"string", 
				"a string value"
				);
	}

	@Override
	public String decodeFrom(Object value) {
		if (value instanceof String)
			return (String)value;
		else
			return value.toString();
		
	}

}
