package genlab.core.model.exec;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.exec.ITask;
import genlab.core.exec.ITasksDynamicProducer;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * A basis for container algo executions. 
 * The supervisor starts subexecution iterations. 
 * It contains subtasks, each subtask corresponding to one iteration.
 * 
 * The supervisor has the responsability to transmit to reduce algos the information of processing end.
 * 
 * @author Samuel Thiriot
 *
 */
public abstract class AbstractContainerExecutionSupervisor 
							extends AbstractContainerExecution 
							implements ITasksDynamicProducer  {


	
	public AbstractContainerExecutionSupervisor(
			IExecution exec,
			IAlgoContainerInstance algoInst) {
		super(
				exec, 
				algoInst, 
				new ComputationProgressWithSteps()
				);
		
		
	}

	@Override
	protected void initComputationState() {
		
		// note that the set of inputs not available is going to grow later, during initLinks. 
		// so always assume we wait for something
		progress.setComputationState(ComputationState.WAITING_DEPENDENCY);
		
	}

	protected abstract void initFirstRun();
	
	protected abstract void startOfIteration();
	
	protected abstract boolean shouldContinueRun();
	
	protected abstract void endOfRun();
	
	protected int evaluateRemainingSteps() {
		return 0;
	}
	
	public void createNewTasks() {
		
	}
	
	@Override
	public boolean willMoreTasks() {
		return shouldContinueRun();
	}

	@Override
	public boolean cannotSendTasksNow() {
		
		// by default, we announce we are always able to send novel tasks
		// (at least, if we are started !)
		// override to change this behaviour
		return (progress.getComputationState() != ComputationState.STARTED);
		
	}

	
	@Override
	public IAlgoExecution provideMoreTasks() {
		
		if (canceled) {
			messages.infoUser("cancelled; interrupting iterations.", getClass());
			progress.setComputationState(ComputationState.FINISHED_CANCEL);
			return null;
		}
		
		if (shouldContinueRun()) {
			
			startOfIteration();
		
			IAlgoExecution subExec =  createNextExecutionForOneIteration();
			
			// define several properties of this "sub"
			subExec.setParent(this);
			addTask(subExec);
			subExec.getProgress().addListener(this);
					
			// TODO catch exceptions and remove it ?
			return subExec;
			
		} else {
			
			// deliver results
			messages.debugTech("queued all iterations.", getClass());
			
			return null;
		}
	}

	@Override
	public void run() {

		messages.debugTech("starting", getClass());
		
		ComputationResult res = new ComputationResult(algoInst, progress, messages);
		setResult(res);
			
		initFirstRun();
		
		progress.setComputationState(ComputationState.STARTED);
		
		exec.getRunner().registerTasksDynamicProducer(this);
		
		
		// TODO tell each mapreduce that computation is finished
		
		// here the result is empty (nothing exported by the loop itself)
		
		
	}
	
	protected void hookContainerExecutionFinished(ComputationState state) {
	
		// called at the end of an execution
		if (!state.isFinished())
			return;
		
		
	}

	

	@Override
	public void initInputs(Map<IAlgoInstance,IAlgoExecution> instance2exec) {

		// nearly as a standard task, but we do iterate across all incoming links
		// instead of a predefined set of inputs.
		
		// create execution links for each input expected;
		// its comes from the output to this container
		for (IConnection c : algoInst.getConnectionsComingFromOutside()) {

			createInputExecutableConnection(
					c.getTo(), 
					c, 
					instance2exec
					);
			
			inputsNotAvailable.add(c.getTo());
			
		}
		
		// also, we store the table for later usage (when we will create the subtasks !)
		this.instance2execOriginal = instance2exec;
		
		// and we create a version to be transmitted to our subtasks
		instance2execForSubtasks = new HashMap<IAlgoInstance, IAlgoExecution>(instance2execOriginal.size());
		for (IConnection c : algoInst.getConnectionsComingFromOutside()) {

			// for each algo exec out of this container, the actual 
			// contact during exec will be the supervisor.
			instance2execForSubtasks.put(c.getFrom().getAlgoInstance(), this);	
		}
		
		// maybe we can start, if there is not input expected ?
		// at the very beginning, all the inputs are waiting for data
		if (inputsNotAvailable.isEmpty())
			progress.setComputationState(ComputationState.READY);
		
		
	}
	


	protected abstract String getSuffixForCurrentIteration();
	
	/**
	 * Returns an execution for one iteration (for instance, for a loop, the execution set of internal executions)
	 * Its inputs and outputs will be set by this one.
	 * @return
	 */
	protected IAlgoExecution createNextExecutionForOneIteration() {
		
		
		// prepare the data to send
		Map<IConnection,Object> inputConnection2value = new HashMap<IConnection, Object>();
		for (IConnection c : algoInst.getConnectionsComingFromOutside()) {

			Object value = getInputValueForInput(c.getTo());
			
			inputConnection2value.put(c, value);
		}
		
		
		// create the container for the iteration
		messages.traceTech("creating the executable for this iteration...", getClass());
		AbstractContainerExecutionIteration resExecIteration = new AbstractContainerExecutionIteration(
				exec, 
				algoInst, 
				new ComputationProgressWithSteps(), // TODO another progress ? 
				inputConnection2value, 
				instance2execOriginal, // instance2execForSubtasks,
				getSuffixForCurrentIteration()
				);
		resExecIteration.autoFinishWhenChildrenFinished = true;
		resExecIteration.autoUpdateProgressFromChildren = true;
		resExecIteration.ignoreCancelFromChildren = false;
		resExecIteration.ignoreFailuresFromChildren = false;
		
		
		// now create the links to call this iteration
		messages.traceTech("init links for this iteration...", getClass());
		// note that iterations not only create input, but also output, connections (for connection between children and outside)
		resExecIteration.initInputs(instance2execForSubtasks);
				
		// set values
		messages.traceTech("defining the values to let the executable start...", getClass());
		{
			for (IConnection c: algoInst.getConnectionsComingFromOutside()) {
				
				for (IConnectionExecution cEx : resExecIteration.getOrCreateConnectionsForInput(c.getTo())) {
					Object value = inputConnection2value.get(c);
					messages.traceTech("defining value "+value+" for "+c, getClass());
					cEx.forceValue(value);
				}
			}
				
		}
		
	
		
		return resExecIteration;
	}
	
	

	@Override
	public void clean() {
	
		// no local data to clean (o_O)
			
		// super clean
		super.clean();
	}	

		
	@Override
	protected void updateProgressFromChildren() {
		
		// this version is very similar to the one from parents. 
		// the main difference is that we assume that a task with "unknown" progress will last as long as the longest 
		// of the supervisor. So the progress bar is more relevant.
		
		ComputationState ourState = null;
		
		long totalToDo = subTasksRemovedDone;
		long totalDone = subTasksRemovedDone;
		boolean somethingNotFinished = shouldContinueRun();
		
		long maxDuration = 0;
		int countUnknown = evaluateRemainingSteps();
		
		for (ITask sub: (LinkedList<ITask>)tasks.clone()) {
			
			final ComputationState subState = sub.getProgress().getComputationState(); 
			
			long subToDo = sub.getProgress().getProgressTotalToDo();
			
			if (subToDo == 0l) {
				countUnknown++;
			} else {
				// well, looks like it knows a count
				totalToDo += sub.getProgress().getProgressTotalToDo();
				totalToDo ++;
				
				if (subToDo > maxDuration)
					maxDuration = subToDo;
			}
			
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
		
		totalToDo += countUnknown*(maxDuration+1);
		
		if (somethingNotFinished) {
			this.progress.setProgressTotal(totalToDo);
			this.progress.setProgressMade(totalDone);
		} else {
			// if we reached this step, then all our children have finished.
			// as a container, we end ourself
			if (somethingFailed && !ignoreFailuresFromChildren)
				ourState = ComputationState.FINISHED_FAILURE;
			else if (somethingCanceled && !ignoreCancelFromChildren)
				ourState = ComputationState.FINISHED_CANCEL;
			else 
				ourState = ComputationState.FINISHED_OK;
			
			endOfRun();
			messages.traceTech("all subs terminated; should transmit results", getClass());
			hookContainerExecutionFinished(ourState);
			this.progress.setComputationState(ourState);
			
		}
	}
	
}
