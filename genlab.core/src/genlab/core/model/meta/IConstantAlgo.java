package genlab.core.model.meta;

import genlab.core.parameters.Parameter;

/**
 * Tags the algos which are in fact constants.
 * 
 * @author Samuel Thiriot
 *
 */
public interface IConstantAlgo extends IAlgo {

	/**
	 * Returns the output that exports the constant value
	 * @return
	 */
	public IInputOutput getConstantOuput();
	
	/**
	 * Returns the parameter that stores the constant value
	 * @return
	 */
	public Parameter<?> getConstantParameter();
	
}
