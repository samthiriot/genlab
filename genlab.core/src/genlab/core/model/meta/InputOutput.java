package genlab.core.model.meta;

import genlab.core.commons.WrongParametersException;

public class InputOutput<JavaType> implements IInputOutput<JavaType> {

	private final IFlowType<JavaType> type;
	private final String id;
	private final String name;
	private final String desc;
	private final boolean acceptMultipleInputs;
	
	public InputOutput(IFlowType<JavaType> type, String id, String name, String desc) {
		super();
		this.type = type;
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.acceptMultipleInputs = false;
	}
	
	public InputOutput(IFlowType<JavaType> type, String id, String name, String desc, boolean acceptMultipleInputs) {
		super();
		this.type = type;
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.acceptMultipleInputs = acceptMultipleInputs;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDesc() {
		return desc;
	}

	@Override
	public boolean acceptsMultipleInputs() {
		return acceptMultipleInputs;
	}
	
	@Override
	public IFlowType<JavaType> getType() {
		return type;
	}

	@Override
	public JavaType decodeFromParameters(Object value) throws WrongParametersException {
		
		return type.decodeFrom(value);
		
	}

	@Override
	public String toString() {
		return id;
	}


}
