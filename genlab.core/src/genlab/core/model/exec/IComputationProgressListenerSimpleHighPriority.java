package genlab.core.model.exec;

/**
 * A listener with an high priority: between an high priority listener and a simple one, 
 * the high priority one will always be called first.
 * 
 * @author B12772
 *
 */
public interface IComputationProgressListenerSimpleHighPriority extends
		IComputationProgressSimpleListener {

}
