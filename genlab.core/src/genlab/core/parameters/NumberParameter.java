package genlab.core.parameters;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class NumberParameter<Type extends Number> extends Parameter<Type> {

	protected Type minValue = null;
	protected Type maxValue = null;
	protected Type step = null;

	
	public NumberParameter(String id, String name, String desc, Type defaultValue) {
		super(id, name, desc, defaultValue);
	}
	
	public NumberParameter(String id, String name, String desc, Type defaultValue, Type minValue) {
		super(id, name, desc, defaultValue);
		
		this.minValue = minValue;
	}
	
	public NumberParameter(String id, String name, String desc, Type defaultValue, Type minValue, Type maxValue) {
		super(id, name, desc, defaultValue);
		
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public NumberParameter(String id, String name, String desc, Type defaultValue, Type minValue, Type maxValue, Type step) {
		super(id, name, desc, defaultValue);
		
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.step = step;
	}
	
	public Map<String,Boolean> check(Type value) {
		
		Map<String,Boolean> res = null;
		
		if (minValue != null && (value.doubleValue() < minValue.doubleValue())) {
			if (res == null)
				res = new HashMap<String, Boolean>();
			res.put("value should be >= "+minValue, true);
		}
		if (maxValue != null && (value.doubleValue() > minValue.doubleValue())) {
			if (res == null)
				res = new HashMap<String, Boolean>();
			res.put("value should be >= "+minValue, true);
		}
		if (res == null)
			return Collections.EMPTY_MAP;
		else
			return res;
	}

	public Type getMinValue() {
		return minValue;
	}

	public void setMinValue(Type minValue) {
		this.minValue = minValue;
	}

	public Type getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Type maxValue) {
		this.maxValue = maxValue;
	}

	public Type getStep() {
		return step;
	}

	public void setStep(Type step) {
		this.step = step;
	}

	
	
}
