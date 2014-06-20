package genlab.core.model.exec;

/**
 * A listener with an high priority: between an high priority listener and a simple one, 
 * the high priority one will always be called first.
 * 
 * @author Samuel Thiriot
 *
 */
public interface IComputationProgressListenerSimpleHighPriority extends
		IComputationProgressSimpleListener {

}
