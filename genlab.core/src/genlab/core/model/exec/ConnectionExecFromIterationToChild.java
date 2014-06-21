package genlab.core.model.exec;

import java.util.HashSet;
import java.util.Set;

import genlab.core.commons.ProgramException;
import genlab.core.exec.ITask;
import genlab.core.model.instance.IConnection;

/**
 * This weird connection links an iteration to its one-shot children.
 * 
 * As soon as the parent iteration is started, it will transmit information
 * to its destination. Also, it will ignore the "finished" status from its parent iteration.
 * In other words, the transmission of information is triggered by the "started" status. 
 * 
 * @author Samuel Thiriot
 *
 */
public class ConnectionExecFromIterationToChild extends AbstractConnectionExec<IAlgoExecution,IAlgoExecutionOneshot> {

	
	public ConnectionExecFromIterationToChild(IConnection c, IAlgoExecution from, IAlgoExecutionOneshot to, boolean check) {
		
		super(c, from, to);
		
		// check parameters
		if (check && (c.getFrom().getAlgoInstance() != from.getAlgoInstance() || c.getTo().getAlgoInstance() != to.getAlgoInstance()))
			throw new ProgramException("inconsistant executable connection");
		
	}
	
	public ConnectionExecFromIterationToChild(IConnection c, IAlgoExecution from, IAlgoExecutionOneshot to) {
		this(c, from, to, true);
	}
	

	/* (non-Javadoc)
	 * @see genlab.core.model.exec.IConnectionExecution#computationStateChanged(genlab.core.model.exec.IComputationProgress)
	 */
	@Override
	public void computationStateChanged(IComputationProgress progress) {
		
		// we just ignore the parent status !
		
	}

	@Override
	public void forceValue(Object value) {
		this.value = value;
		to.notifyInputAvailable(c.getTo());
	}
	
	@Override
	public void propagateRank(Integer rank, Set<ITask> visited) {
		// don't propagate
	}

}
