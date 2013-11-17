package genlab.core.exec;

/**
 * A place where computations may be driven (like: local computer. 
 * Or the computer of the neighboor, which is far more efficient). 
 * May later provide something like distributed computations.
 * Not the case right now.
 * 
 * @author Samuel Thiriot
 *
 */
public interface IComputationNode {

	public IRunner getRunner();
	
}
