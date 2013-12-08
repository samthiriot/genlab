package genlab.core.model.instance;

import genlab.core.parameters.Parameter;

public interface IParametersListener {

	public void parameterValueChanged(IAlgoInstance ai, String parameterId, Object novelValue);
	
}
