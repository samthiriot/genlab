package genlab.core.model.meta;

import genlab.core.commons.WrongParametersException;

/**
 * An input, or output, of an algo.
 * 
 * @author Samuel Thiriot
 *
 * @param <JavaType>
 */
public interface IInputOutput<JavaType> {

	public String getId();
	
	public String getName();
	
	public String getDesc();
	
	public IFlowType<JavaType> getType();
	
	public boolean acceptsMultipleInputs();
	
	/**
	 * Returns true if this is an output able to export successive updates of the same value.
	 * @return
	 */
	public boolean isContinuousOutput();

		
	/**
	 * 	for this given Input or output type, checks that this flow type is described into values with a relevant type
	 * and other constraints respected. Also adds some transtyping possibilities
	 */
	public JavaType decodeFromParameters(Object value) throws WrongParametersException;
	
	// TODO ? public IInputOutputInstance createInstance(IAlgoInstance algoInstance);
	
}
