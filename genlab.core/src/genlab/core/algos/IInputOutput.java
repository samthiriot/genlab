package genlab.core.algos;

import genlab.core.flow.IFlowType;

import java.util.Map;

public interface IInputOutput<JavaType> {

	public String getId();
	
	public String getName();
	
	public String getDesc();
	
	public IFlowType<JavaType> getType();
	
	/**
	 * 	for this given algo, checks that this flow type is described into values with a relevant type
	 * and other constraints respected.
	 */
	public JavaType decodeFromParameters(Map<IInputOutput,Object> values) throws WrongParametersException;
	
}
