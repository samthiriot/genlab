package genlab.core.model.exec;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.instance.IConnection;
import genlab.core.usermachineinteraction.GLLogger;

/**
 * A connection instance in the execution world: links two algo executions.
 * In practice, the connection is in charge of storing and making the result accessible for the 
 * next algo.
 * 
 * @author Samuel Thiriot
 *
 */
public class ConnectionExec implements IComputationProgressSimpleListener {

	public final IConnection c;
	public final IAlgoExecution from;
	public final IAlgoExecution to;
	
	private IExecution exec;
	
	// TODO store the value !
	private Object value;
	
	public ConnectionExec(IConnection c, IAlgoExecution from, IAlgoExecution to, boolean check) {
		
		// check parameters
		if (check && (c.getFrom().getAlgoInstance() != from.getAlgoInstance() || c.getTo().getAlgoInstance() != to.getAlgoInstance()))
			throw new ProgramException("inconsistant executable connection");
		
		// store them
		this.c = c;
		this.from = from;
		this.to = to;
		
		exec = from.getExecution();
		
		// listen for the "from" exec
		from.getProgress().addListener(this);
		
	}
	
	public ConnectionExec(IConnection c, IAlgoExecution from, IAlgoExecution to) {
		this(c, from, to, true);
	}
	
	/**
	 * Used by the target algo exec to retrieve the value
	 * @return
	 */
	public Object getValue() {
		return value;
	}

	@Override
	public void computationStateChanged(IComputationProgress progress) {
		
		if (progress != from.getProgress())
			return;	// no reason for this case...

		final ComputationState state = progress.getComputationState();
		if (state != ComputationState.FINISHED_OK) {
			
			// clear internal state only if the task is not running
			if (state != ComputationState.STARTED) {
				//GLLogger.traceTech("parent task status changed to "+progress.getComputationState()+"; clearing result", getClass());
				value = null;		
			}
			
			// exit, nothing else to do
			return;
		}
				
		
			
		// a computation ended; let's reuse its result !
		
		if (value != null)
			exec.getListOfMessages().errorUser("Unable to load the data", getClass());
		
		// retrieve the value
		value = from.getResult().getResults().get(c.getFrom());
		
		//GLLogger.traceTech("received value "+value, getClass());
		
		// warn children
		to.notifyInputAvailable(c.getTo());
		
	}
	
	public void forceValue(Object value) {
		this.value = value;

		//GLLogger.traceTech("received (forced) value "+value, getClass());
		
		// warn children
		to.notifyInputAvailable(c.getTo());
	}
	
	public void reset() {
		this.value = null;
	}

}
