package genlab.core.parameters;

public class NumberParameterConstraint<TypeClass extends Number> implements IParameterConstraint<TypeClass>{

	public TypeClass minimumValue = null;
	public TypeClass maximumValue = null;
	
	/**
	 * For GUI: from how much to step
	 */
	public TypeClass stepValueSmall = null;
	public TypeClass stepValueHigh = null;
			
	public NumberParameterConstraint() {
		
	}

	@Override
	public boolean isValid(TypeClass valueToTest) {
		
		if (minimumValue != null && valueToTest.doubleValue() < minimumValue.doubleValue())
			return false;
		
		if (maximumValue != null && valueToTest.doubleValue() > maximumValue.doubleValue())
			return false;
		
		return true;
	}
	
	

}
