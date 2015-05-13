package genlab.core.model.exec;

import java.util.Set;

import genlab.core.commons.NotImplementedException;
import genlab.core.commons.ProgramException;
import genlab.core.exec.ITask;
import genlab.core.model.instance.IConnection;

/**
 * This special connection links a supervisor to its reduce child.
 * 
 * When the supervisor is done, it transmits this nice information to the destination reduce algo.
 * No data is conveyed by this connection (in fact, information is rather transmitted by the iterations, 
 * no the supervisor).
 * 
 * @author Samuel Thiriot
 *
 */
public class ConnectionExecFromSupervisorToChild extends AbstractConnectionExec<AbstractContainerExecutionSupervisor,IReduceAlgoExecution> {

	/**
	 * Serialization only
	 */
	public ConnectionExecFromSupervisorToChild(){}
	
	public ConnectionExecFromSupervisorToChild(IConnection c, AbstractContainerExecutionSupervisor from, IReduceAlgoExecution to) {
		
		super(c, from, to);
	
		// TODO check parameters ???
		
	}
	

	/* (non-Javadoc)
	 * @see genlab.core.model.exec.IConnectionExecution#computationStateChanged(genlab.core.model.exec.IComputationProgress)
	 */
	@Override
	public void computationStateChanged(IComputationProgress progress) {
		
		ComputationState status = progress.getComputationState();
		
		switch (status) {

		case FINISHED_CANCEL:
		case FINISHED_FAILURE:
		case FINISHED_OK:
			// relay information to the child
			to.signalEndOfTasksForSupervisor(from, progress.getComputationState());
			break;
			
		case STARTED:
			to.signalIncomingSupervisor(from);
			break;

		case CREATED:
		case READY:
		case WAITING_DEPENDENCY:
			// ignore
			break;

		default:
			throw new ProgramException("unknown computation state: "+status);
		}
		
		if (status == ComputationState.FINISHED_CANCEL || status == ComputationState.FINISHED_FAILURE) {
			to.cancel();
		}
		
	}

	@Override
	public void forceValue(Object value) {
		throw new NotImplementedException();		
	}
	
	@Override
	public void propagateRank(Integer rank, Set<ITask> visited) {
		// don't propagate
	}


}
