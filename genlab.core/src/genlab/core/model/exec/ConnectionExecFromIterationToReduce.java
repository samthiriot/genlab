package genlab.core.model.exec;

import genlab.core.commons.NotImplementedException;
import genlab.core.commons.ProgramException;
import genlab.core.model.instance.IConnection;

/**
 * A connection instance in the execution world: links two algo executions.
 * In practice, the connection is in charge of storing and making the result accessible for the 
 * next algo.
 * 
 * This connection relates an iteration and a reduce algorithm.
 * Once the computation is finished in the "from" part, the "reduce" destination algo
 * receives the corresponding data.
 * 
 * @author Samuel Thiriot
 *
 */
public class ConnectionExecFromIterationToReduce extends AbstractConnectionExec<IAlgoExecution, IReduceAlgoExecution> {

	public ConnectionExecFromIterationToReduce(IConnection c, IAlgoExecution from, IReduceAlgoExecution to) {

		super(c, from, to);
		
		// TODO check ?
		
	}
	
	/* (non-Javadoc)
	 * @see genlab.core.model.exec.IConnectionExecution#computationStateChanged(genlab.core.model.exec.IComputationProgress)
	 */
	@Override
	public void computationStateChanged(IComputationProgress progress) {
		
		if (progress != from.getProgress())
			return;	// no reason for this case...

		final ComputationState state = progress.getComputationState();
		
		if (state != ComputationState.FINISHED_OK) {
			value = null;
			return;
		}
		
		// there should be a value
		
		// retrieve the value
		try {
			value = from.getResult().getResults().get(c.getFrom());
		} catch (NullPointerException e) {
			throw new ProgramException("an executable announced a finished with success, but does not publish results.");
		}
		
		// ensure we got one
		if (value == null)
			exec.getListOfMessages().errorUser("received a null value...", getClass());

		// warn children
		to.receiveInput(
				(IAlgoExecution)from.getParent(), 
				this, 
				value
				);

		
	}
	
	
	/* (non-Javadoc)
	 * @see genlab.core.model.exec.IConnectionExecution#forceValue(java.lang.Object)
	 */
	@Override
	public void forceValue(Object value) {
		
		throw new NotImplementedException();
		
	}
	
	

}
