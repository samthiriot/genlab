package genlab.core.model.exec;

import genlab.core.commons.ProgramException;
import genlab.core.model.instance.IConnection;

/**
 * This weird connection links an iteration to its children.
 * As soon as the parent iteration is started, it will transmit information
 * to its destination. Also, it will ignore the "finished" status from its parent iteration.
 * In other words, the transmission of information is triggered by the "started" status. 
 * 
 * @author Samuel Thiriot
 *
 */
public class ConnectionExecFromIterationToChild extends AbstractConnectionExec {

	
	public ConnectionExecFromIterationToChild(IConnection c, IAlgoExecution from, IAlgoExecution to, boolean check) {
		
		super(c, from, to);
		
		// check parameters
		if (check && (c.getFrom().getAlgoInstance() != from.getAlgoInstance() || c.getTo().getAlgoInstance() != to.getAlgoInstance()))
			throw new ProgramException("inconsistant executable connection");
		
	}
	
	public ConnectionExecFromIterationToChild(IConnection c, IAlgoExecution from, IAlgoExecution to) {
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
		
		if (state != ComputationState.STARTED) 
			return;
		
		
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
	

}
