package genlab.populations.bo;

import genlab.core.commons.WrongParametersException;

import java.util.Collection;

public interface IMultipleInheritance<JavaClass extends IAttributesHolder> {


	/**
	 * Retrieve the agent types these types inherit from.
	 * @return
	 */
	public Collection<JavaClass> getInheritedTypes();
	
	/**
	 * 
	 * @param inheritedClass
	 * @throws WrongParametersException in case of a loop in inheritance
	 */
	public void addInheritedTypes(JavaClass inheritedClass) throws WrongParametersException;

	
	
}
