package genlab.core.algos;

import java.util.Map;

import genlab.core.flow.IFlowType;

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
