package genlab.core.model.meta;

import java.util.Set;

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
	
}
