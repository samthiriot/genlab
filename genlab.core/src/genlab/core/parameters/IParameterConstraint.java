package genlab.core.parameters;

public interface IParameterConstraint<ClassType extends Object> {

	public boolean isValid(ClassType valueToTest);
	
	
}
