package genlab.core.model.exec;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IContainerTask;
import genlab.core.exec.IExecution;
import genlab.core.exec.ITask;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IInputOutputInstance;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * A task container
 * 
 * TODO prepare to clean tasks (we should clean iterations as there are done in order to save resources)
 * 
 * @author Samuel Thiriot
 *
 */
public abstract class AbstractContainerExecution 
								extends AbstractAlgoExecution 
								implements IContainerTask, IComputationProgressSimpleListener {

	/**
	 * All the subtasks for this iteration
	 */
	// TODO change ITask to IAbstractExecution
	protected final Collection<ITask> tasks = new LinkedList<ITask>();
	
	protected final IAlgoContainerInstance algoInst;

	protected boolean canceled = false;

	
	/**
	 * The mapping instance 2 execution provided at an init step.
	 * Required for the delayed init of subtasks.
	 */
	protected Map<IAlgoInstance,IAlgoExecution> instance2execOriginal = null;
	
	protected Map<IAlgoInstance,IAlgoExecution> instance2execForSubtasks = null;
	
	
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
		synchronized (tasks) {
			
			ComputationState ourState = null;
			boolean somethingNotFinished = false;
			boolean somethingFailed = false;
			boolean somethingCanceled = false;
			long totalToDo = 0;
			long totalDone = 0;
			
			loop: for (ITask sub: tasks) {
				
				final ComputationState subState = sub.getProgress().getComputationState(); 
				
				totalToDo ++;
				totalToDo += sub.getProgress().getProgressTotalToDo();
								
				if (!subState.isFinished()) {
					somethingNotFinished = true;
					totalDone += sub.getProgress().getProgressDone();
					continue;
				}  else {
					totalDone ++;
				}
				
				switch (receivedState) {
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
					throw new ProgramException("unknown computation status with property 'finished': "+receivedState);
				}
				
			}
			
			// if we reached this step, then all our children have finished.
			
			if (somethingNotFinished) {
				this.progress.setProgressTotal(totalToDo);
				this.progress.setProgressMade(totalDone);
			} else { 
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
			
		
		// suggest a garbage collecting now
		Runtime.getRuntime().gc();
				
		// TODO transmit all the values to the reduce steps
	}
	
	@Override
	public final void addTask(ITask t) {
		synchronized (tasks) {
			if (!tasks.contains(t)) {
				tasks.add(t);
				t.getProgress().addListener(this);
				exec.getRunner().addTask((IAlgoExecution) t);
			}	
		}
		
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
}
