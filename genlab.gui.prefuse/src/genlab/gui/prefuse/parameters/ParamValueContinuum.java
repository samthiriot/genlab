package genlab.gui.prefuse.parameters;

import genlab.core.parameters.ColorRGBParameterValue;

public class ParamValueContinuum {
	
	public ColorRGBParameterValue value1;
	public ColorRGBParameterValue value2;
	
	public ParamValueContinuum(ColorRGBParameterValue value1, ColorRGBParameterValue value2) {
		this.value1 = value1;
		this.value2 = value2;
	}

	@Override
	public boolean equals(Object obj) {
		try {
			ParamValueContinuum other = (ParamValueContinuum)obj;
			return other.value1.equals(value1) && other.value1.equals(value2); 
		} catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(value1).append("->");
		sb.append(value2);
		return sb.toString();
	}

	
}
