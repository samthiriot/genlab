package genlab.core.parameters;

import java.util.Map;

public abstract class Parameter<Type extends Object> {

	protected final String id;
	protected final String name;
	protected final String desc;
	protected boolean required = true;
	protected Type defaultValue = null;
	
	/**
	 * if false, the value of this parameter will NOT be saved. Only for very specific cases 
	 */
	protected boolean shouldSave = true;
	
	public Parameter(String id, String name, String desc, Type defaultValue) {
		this.id = id; // TODO ensure unicity !
		this.name = name;
		this.desc = desc;
		this.defaultValue = defaultValue;
	}
	
	public Type getDefaultValue() {
		return this.defaultValue;
	}
	
	public boolean isRequired() {
		return required;
	}
	
	public void setRequired(boolean b) {
		this.required = b;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDesc() {
		return desc;
	}
	/**
	 * Check the value of this parameter; returns tuples (message, blocking)
	 * @param value
	 */
	public abstract Map<String,Boolean> check(Type value);
	
	public abstract Type parseFromString(String value);

	public boolean shouldSave() {
		return shouldSave;
	}

	public void setShouldSave(boolean shouldSave) {
		this.shouldSave = shouldSave;
	}
		

}
