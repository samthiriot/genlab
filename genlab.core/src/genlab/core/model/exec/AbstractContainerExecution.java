package genlab.core.model.exec;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IContainerTask;
import genlab.core.exec.IExecution;
import genlab.core.exec.ITask;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * A task container. Provides basic features 
 * 
 * 
 * TODO prepare to clean tasks (we should clean iterations as there are done in order to save resources)
 * 
 * @author Samuel Thiriot
 *
 */
public abstract class AbstractContainerExecution 
								extends AbstractAlgoExecutionOneshot 
								implements IContainerTask, IComputationProgressSimpleListener, IComputationProgressDetailedListener {

	/**
	 * All the subtasks for this iteration
	 */
	// TODO change ITask to IAbstractExecution
	protected final LinkedList<ITask> tasks = new LinkedList<ITask>();
	
	protected final IAlgoContainerInstance algoInst;

	protected boolean canceled = false;
	
	/**
	 * The mapping instance 2 execution provided at an init step.
	 * Required for the delayed init of subtasks.
	 * (lifecycle not controlled here)
	 */
	protected Map<IAlgoInstance,IAlgoExecution> instance2execOriginal = null;
	
	/**
	 * Lifecycle controlled here
	 */
	protected Map<IAlgoInstance,IAlgoExecution> instance2execForSubtasks = null;
	
	
	/**
	 * When tasks were removed (in order to save space !),
	 * they should still be counted in our progress. 
	 */
	protected int subTasksRemovedCount = 0;
	protected long subTasksRemovedDone = 0;
	protected boolean somethingFailed = false;
	protected boolean somethingCanceled = false;
	
	
	public AbstractContainerExecution(IExecution exec, IAlgoContainerInstance algoInst,
			IComputationProgress progress) {
		super(exec, algoInst, progress);

		this.algoInst = algoInst;

	}
	
	/**
	 * can be override by children in order to process things when the computation finished
	 */
	protected void hookContainerExecutionFinished(ComputationState state) {
		
	}

	@Override
	public final void computationStateChanged(IComputationProgress progress) {
		
		final ComputationState receivedState = progress.getComputationState();
		
		// ignore intermediate messages
		if (!receivedState.isFinished())
			return;
		
		// ignore progress which is not coming from my children
		if (!tasks.contains(progress.getAlgoExecution()))
			return; 
		
		// ensure all tasks are done !
		//synchronized (tasks) {
			
		updateProgressFromChildren();
			
		//}
			
	}
	
	private void updateProgressFromChildren() {
		
		ComputationState ourState = null;
		
		long totalToDo = subTasksRemovedDone;
		long totalDone = subTasksRemovedDone;
		boolean somethingNotFinished = false;

		for (ITask sub: tasks) {
			
			final ComputationState subState = sub.getProgress().getComputationState(); 
			
				
			// well, looks like it knows a count
			totalToDo += sub.getProgress().getProgressTotalToDo();
			totalToDo ++;
						
			if (!subState.isFinished()) {
				somethingNotFinished = true;
				totalDone += sub.getProgress().getProgressDone();
				continue;
			}  else {
				totalDone ++;
			}
			
			switch (subState) {
			case FINISHED_FAILURE:
				somethingFailed = true;
				break;
			case FINISHED_CANCEL:
				somethingCanceled = true;
				break;
			case FINISHED_OK:
				// don't care 
				break;
			default:
				throw new ProgramException("unknown computation status with property 'finished': "+subState);
			}
			
		}
	
		
		if (somethingNotFinished) {
			this.progress.setProgressTotal(totalToDo);
			this.progress.setProgressMade(totalDone);
		} else {
			// if we reached this step, then all our children have finished.
			// as a container, we end ourself
			if (somethingFailed)
				ourState = ComputationState.FINISHED_FAILURE;
			else if (somethingCanceled)
				ourState = ComputationState.FINISHED_CANCEL;
			else 
				ourState = ComputationState.FINISHED_OK;
			
			messages.traceTech("all subs terminated; should transmit results", getClass());
			hookContainerExecutionFinished(ourState);
			this.progress.setComputationState(ourState);
			
		}
	}
	

	@Override
	public void computationProgressChanged(IComputationProgress progress) {
		updateProgressFromChildren();
	}

	@Override
	public void taskCleaning(ITask task) {
		
		// I would like to remember the work of this subtask
		switch (task.getProgress().getComputationState()) {
		case FINISHED_OK:
			break;
		case FINISHED_CANCEL:
			somethingCanceled = true;
			break;
		case FINISHED_FAILURE:
			somethingFailed = true;
			break;
		default:
			throw new ProgramException("only finished tasks should be cleaned, or unknown finished state: "+task.getProgress().getComputationState());
		}
		
		subTasksRemovedCount++;
		subTasksRemovedDone += task.getProgress().getProgressDone();
		
		tasks.remove(task);
		
	}

	
	@Override
	public final void addTask(ITask t) {
		//synchronized (tasks) {
			if (!tasks.contains(t)) {
				tasks.add(t);
				t.getProgress().addListener(this);
				t.getProgress().addDetailedListener(this);
				exec.getRunner().addTask((IAlgoExecution) t);
			}	
		//}
		
	}


	@Override
	public final void kill() {
		this.canceled = true; // TODO attempt to kill the running subtasks !

		synchronized (tasks) {
			// pass the messages to children
			for (ITask subTask : tasks) {
				try {
					subTask.kill();
				} catch (RuntimeException e) {
					messages.warnTech("catched an exception while attempting to cancel subtask "+subTask+": "+e.getMessage(), getClass(), e);
				}
			}
		}
	}

	@Override
	public final void cancel() {
		this.canceled = true; // TODO attempt to kill the running subtasks !

		synchronized (tasks) {
			// pass the messages to children
			for (ITask subTask : tasks) {
				try {
					subTask.cancel();
				} catch (RuntimeException e) {
					messages.warnTech("catched an exception while attempting to cancel subtask "+subTask+": "+e.getMessage(), getClass(), e);
				}
			}
		}
	}

	

	@Override
	public final Collection<ITask> getTasks() {
		return tasks;
	}
	
	@Override
	public final long getTimeout() {
		// children may have timeouts, not me
		return -1;
	}


	@Override
	public final int getThreadsUsed() {
		// important ! else running only containers would be enough to declare all the threads used, and nothing would ever happen ^^
		return 0;
	}


	@Override
	public final void collectEntities(
			Set<IAlgoExecution> execs,
			Set<IConnectionExecution> connections
			) {
		
		super.collectEntities(execs, connections);
		
		// also collect my subentities
		for (ITask t: tasks) {
			((IAlgoExecution)t).collectEntities(execs, connections);
		}
		
		
	}
	
	@Override
	public void clean() {
	
		// clean subtasks !
		for (ITask sub: (LinkedList<ITask>)tasks.clone()) {
			sub.clean();
		}
				
		// clean local data
		if (tasks != null)
			tasks.clear();
		
		if (instance2execForSubtasks != null) {
			instance2execForSubtasks.clear();
			instance2execForSubtasks = null;
		}
		instance2execOriginal = null;
		
		// super clean
		super.clean();
	}
	
}
