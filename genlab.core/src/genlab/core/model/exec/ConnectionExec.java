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
	
	public ConnectionExec(IConnection c, IAlgoExecution from, IAlgoExecution to) {
		
		// check parameters
		if (c.getFrom().getAlgoInstance() != from.getAlgoInstance() || c.getTo().getAlgoInstance() != to.getAlgoInstance())
			throw new ProgramException("inconsistant executable connection");
		
		// store them
		this.c = c;
		this.from = from;
		this.to = to;
		
		exec = from.getExecution();
		
		// listen for the "from" exec
		from.getProgress().addListener(this);
		
	}
	
	public Object getValue() {
		return value;
	}

	@Override
	public void computationStateChanged(IComputationProgress progress) {
		
		if (progress != from.getProgress())
			return;	// no reason for this case...
		
		if (progress.getComputationState() != ComputationState.FINISHED_OK)
			return;	
		
		// a computation ended; let's reuse its result !
		
		if (value != null)
			exec.getListOfMessages().errorUser("Unable to load the data", getClass());
		
		// retrieve the value
		value = from.getResult().getResults().get(c.getFrom());
		
		GLLogger.traceTech("received value "+value, getClass());
		
		// warn children
		to.notifyInputAvailable(c.getTo());
		
	}
	
	

}
