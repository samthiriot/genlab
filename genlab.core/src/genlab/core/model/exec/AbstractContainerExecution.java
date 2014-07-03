package genlab.core.model.exec;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IContainerTask;
import genlab.core.exec.IExecution;
import genlab.core.exec.ITask;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * A task container. Provides basic features like: <ul>
 * <li>a list of tasks to store subtasks, and the corresponding getter</li>
 * <li>methods to kill, cancel, clean</li>
 * <li>the monitoring of children's progress to update our own progress</li>
 * </ul>
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
	
	protected boolean autoFinishWhenChildrenFinished = true;
	
	/**
	 * if true, when adapting our progress based on the progress of children, 
	 * we will ignore them being failed.
	 */
	protected boolean ignoreFailuresFromChildren = false;
	protected boolean ignoreCancelFromChildren = false;


	protected boolean autoUpdateProgressFromChildren = true;
	
	
	public AbstractContainerExecution(
			IExecution exec, 
			IAlgoContainerInstance algoInst,
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
	public void computationStateChanged(IComputationProgress progress) {
		
		try {
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
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
			
	}
	
	protected void updateProgressFromChildren() {
		
		if (!autoUpdateProgressFromChildren && !autoFinishWhenChildrenFinished)
			return;
		
		ComputationState ourState = null;

		// review sub tasks
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
	
		// update our state
		if (autoUpdateProgressFromChildren 
				&& somethingNotFinished 
				&& !somethingFailed 
				&& !somethingCanceled) {
			
			this.progress.setProgressTotal(totalToDo);
			this.progress.setProgressMade(totalDone);
			
		} else if (autoFinishWhenChildrenFinished) {
			
			// if we reached this step, then all our children have finished.

			// as a container, we end ourself
			if (somethingFailed && !ignoreFailuresFromChildren) {
				//cancel();
				ourState = ComputationState.FINISHED_FAILURE;
				
			} else if (somethingCanceled && !ignoreCancelFromChildren) {
				//cancel();
				ourState = ComputationState.FINISHED_CANCEL;
			} else 
				ourState = ComputationState.FINISHED_OK;
			
			messages.traceTech("all subs terminated; should transmit results (and set my state to "+ourState+")", getClass());
			hookContainerExecutionFinished(ourState);
			
			this.progress.setComputationState(ourState);
			
		}
	}
	
	

	@Override
	public void computationProgressChanged(IComputationProgress progress) {
		
		messages.traceTech("received a state change: "+progress.getAlgoExecution()+" changed to "+progress.getComputationState(), getClass());
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
				if (autoUpdateProgressFromChildren)
					t.getProgress().addDetailedListener(this);
				
				// add this subtask to the runner, but only if we are already running.
				// if we are not running yet, then the runner will discover the task itself when adding us as a task.
				// we don't want the runner to run our tasks while we are not in his pool yet.
				if (exec.getRunner().containsTask(this)) {
					exec.getRunner().addTask((IAlgoExecution) t);
				}
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
				if (!subTask.getProgress().getComputationState().isFinished())
					try {
						subTask.cancel();
					} catch (RuntimeException e) {
						messages.warnTech("catched an exception while attempting to cancel subtask "+subTask+": "+e.getMessage(), getClass(), e);
					}
			}
		}
	}

	

	@SuppressWarnings("unchecked")
	@Override
	public final Collection<ITask> getTasks() {
		return (LinkedList<ITask>)tasks.clone();
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
