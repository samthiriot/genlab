package genlab.core.model.meta;

import genlab.core.commons.WrongParametersException;

public class InputOutput<JavaType> implements IInputOutput<JavaType> {

	private final IFlowType<JavaType> type;
	private final String id;
	private final String name;
	private final String desc;
	private final boolean acceptMultipleInputs;
	private final boolean facultative;
	private boolean isContinuousOutput = false;
	protected JavaType defaultValue = null;
	
	public InputOutput(IFlowType<JavaType> type, String id, String name, String desc) {
		super();
		this.type = type;
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.acceptMultipleInputs = false;
		this.facultative = false;
	}
	
	public InputOutput(IFlowType<JavaType> type, String id, String name, String desc, boolean acceptMultipleInputs) {
		super();
		this.type = type;
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.acceptMultipleInputs = acceptMultipleInputs;
		this.facultative = false;
	}
	

	public InputOutput(IFlowType<JavaType> type, String id, String name, String desc, boolean acceptMultipleInputs, boolean facultative) {
		super();
		this.type = type;
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.acceptMultipleInputs = acceptMultipleInputs;
		this.facultative = facultative;
	}

	public InputOutput(IFlowType<JavaType> type, String id, String name, String desc, JavaType defaultValue) {
		super();
		this.type = type;
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.defaultValue = defaultValue;
		this.acceptMultipleInputs = false;
		this.facultative = false;

	}
	
	public InputOutput(IFlowType<JavaType> type, String id, String name, String desc, JavaType defaultValue, boolean acceptMultipleInputs) {
		super();
		this.type = type;
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.defaultValue = defaultValue;
		this.acceptMultipleInputs = acceptMultipleInputs;
		this.facultative = false;
	}
	

	public InputOutput(IFlowType<JavaType> type, String id, String name, String desc, JavaType defaultValue, boolean acceptMultipleInputs, boolean facultative) {
		super();
		this.type = type;
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.defaultValue = defaultValue;
		this.acceptMultipleInputs = acceptMultipleInputs;
		this.facultative = facultative;
	}
	
	
	public void setIsContinuousOutput(boolean isContinuousOutput) {
		this.isContinuousOutput = isContinuousOutput;
	}
	
	
	@Override
	public boolean isContinuousOutput() {
		return isContinuousOutput;
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

	@Override
	public final JavaType getDefaultValue() {
		return defaultValue;
	}

	@Override
	public boolean isFacultative() {
		return facultative;
	}

	@Override
	public boolean equals(Object arg0) {
		try {
			return id.equals(((InputOutput<JavaType>)arg0).id);
		} catch (ClassCastException e) {
			return false;
		}
		
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	

}
