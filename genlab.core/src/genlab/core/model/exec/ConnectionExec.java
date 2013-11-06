package genlab.core.model.exec;

import genlab.core.commons.ProgramException;
import genlab.core.exec.ITask;
import genlab.core.model.instance.IConnection;

/**
 * A connection instance in the execution world: links two algo executions.
 * In practice, the connection is in charge of storing and making the result accessible for the 
 * next algo.
 * 
 * This standard connection works to link two algos at the same level of aggregation: 
 * once algo A linked to B finished, its value is readen by the connection, then the algo B is notified 
 * of the availability of the data.
 * 
 * @author Samuel Thiriot
 *
 */
public class ConnectionExec extends AbstractConnectionExec<IAlgoExecution, IAlgoExecutionOneshot> {

	public ConnectionExec(IConnection c, IAlgoExecution from, IAlgoExecutionOneshot to, boolean check) {

		super(c, from, to);
		
		// check parameters
		if (check 
				&& (
						c.getFrom().getAlgoInstance() != from.getAlgoInstance() 
						|| 
						c.getTo().getAlgoInstance() != to.getAlgoInstance()
						)
			)
			throw new ProgramException("inconsistant executable connection");
		
		
	}
	
	public ConnectionExec(IConnection c, IAlgoExecution from, IAlgoExecutionOneshot to) {
		this(c, from, to, true);
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
		to.notifyInputAvailable(c.getTo());

		
	}
	
	
	/* (non-Javadoc)
	 * @see genlab.core.model.exec.IConnectionExecution#forceValue(java.lang.Object)
	 */
	@Override
	public void forceValue(Object value) {
		
		// store the value
		this.value = value;

		// warn children
		to.notifyInputAvailable(c.getTo());
	}



}
