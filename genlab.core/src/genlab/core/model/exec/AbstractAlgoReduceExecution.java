package genlab.core.model.exec;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IInputOutputInstance;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * Intercepts the events of input reception, and 
 * 
 * @author Samuel Thiriot
 *
 */
public abstract class AbstractAlgoReduceExecution 
								extends AbstractAlgoExecution 
								implements IReduceAlgoExecution, IComputationProgressSimpleListener {

	/**
	 * For each input connection, stores the computation state received when finished 
	 */
	protected Map<IAlgoExecution, ComputationState> exec2finishStatus;
		
	protected Set<AbstractContainerExecutionSupervisor> incomingSupervisorsNotFinished;
	
	protected boolean somethingCanceled = false;
	protected boolean somethingFailed = false;
	
	
	public AbstractAlgoReduceExecution(IExecution exec, IAlgoInstance algoInst,
			IComputationProgress progress) {
		super(exec, algoInst, progress);
		
		
		// retrieve the set one connections
		Collection<IConnection> allConnections = algoInst.getAllIncomingConnections();

		this.exec2finishStatus = new HashMap<IAlgoExecution, ComputationState>();

		this.incomingSupervisorsNotFinished = new HashSet<AbstractContainerExecutionSupervisor>();
		
		progress.setComputationState(ComputationState.WAITING_DEPENDENCY);
		
	}
	
	/**
	 * Called when an incoming execution finished.
	 * Only called in a thread safe way.
	 * Does not guarantees that all inputs are present ! (yes, they should).
	 * @param exec
	 */
	protected abstract void processEndOfExecution(IAlgoExecution exec);
	
	/**
	 * Called when a novel, unknown incoming execution is called. 
	 * Only called in a thread safe way.
	 * @param executionRun
	 */
	protected abstract void prepareToProcessNovelInputsFor(IAlgoExecution executionRun);

	/**
	 * 
	 * @param executionRun
	 * @param inputInstance
	 * @param value
	 */
	protected abstract void processNovelInputs(IAlgoExecution executionRun,
			IConnectionExecution connectionExec, Object value);	


	@Override
	public final void receiveInput(IAlgoExecution executionRun,
			IConnectionExecution connectionExec, Object value) {
		
		// manage: store finish status
		synchronized (exec2finishStatus) {
			
			boolean created = false;
			
			if (!exec2finishStatus.containsKey(executionRun)) {
				
				exec2finishStatus.put(executionRun, null);

				prepareToProcessNovelInputsFor(executionRun);

				created = true;
				
			}
			
			processNovelInputs(executionRun, connectionExec, value);
			
			if (created) {
				// first time we detect this run which will provide data to us
				// let's listen for its progress, so we will be aware of its end !
				if (executionRun.getProgress().getComputationState().isFinished()) {
					// special (common) case where the execution already finished before
					// sending this data.
					processEndOfExecution(executionRun);
				} else {
					executionRun.getProgress().addListener(this);
				}
				// TODO is there a risk of missing something with this "else" ?
			}
				
		}
		
	}
	
	@Override
	public void computationStateChanged(IComputationProgress progress) {
		
		if (!progress.getComputationState().isFinished())
			return;
		
		if (!exec2finishStatus.containsKey(progress.getAlgoExecution()))
			throw new ProgramException("unknown incoming execution: " + progress.getAlgoExecution());

		messages.traceTech("one of the executions finished: "+progress.getAlgoExecution(), getClass());

		exec2finishStatus.put(progress.getAlgoExecution(), progress.getComputationState());
		
		switch (progress.getComputationState()) {
		case FINISHED_CANCEL:
			somethingCanceled = true;
			break;
		case FINISHED_FAILURE:
			somethingFailed = true;
			break;
		case FINISHED_OK:
			break;
		default:
			throw new ProgramException("this status 'finished' is unknown: "+progress.getComputationState());
		}
		
		processEndOfExecution(progress.getAlgoExecution());
		
	}
	


	@Override
	public void signalIncomingSupervisor(
			AbstractContainerExecutionSupervisor supervisor) {
		
		synchronized (incomingSupervisorsNotFinished) {
			incomingSupervisorsNotFinished.add(supervisor);
		}
	}



	@Override
	public void signalEndOfTasksForSupervisor(
			AbstractContainerExecutionSupervisor supervisor, ComputationState state) {
		
		synchronized (incomingSupervisorsNotFinished) {
			incomingSupervisorsNotFinished.remove(supervisor);
			if (incomingSupervisorsNotFinished.isEmpty()) {
				messages.debugTech("all the incoming supervisors ended. I'm now ready for postprocessing.", getClass());
				progress.setComputationState(ComputationState.READY);
			}
		}
	}


}
