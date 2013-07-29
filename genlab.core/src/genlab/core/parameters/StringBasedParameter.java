package genlab.core.parameters;

import java.util.Map;

/**
 * PArameters based on this parameter will be edited by String, 
 * even if they can have another value
 * 
 * @author Samuel Thiriot
 *
 * @param <ClassSomething>
 */
public abstract class StringBasedParameter<ClassSomething extends Object> extends Parameter<ClassSomething> {

	public StringBasedParameter(String id, String name, String desc,
			ClassSomething defaultValue) {
		super(id, name, desc, defaultValue);

	}

	@Override
	public abstract Map<String,Boolean> check(ClassSomething something);
	
	@Override
	public abstract ClassSomething parseFromString(String value);

}
