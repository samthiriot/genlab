package genlab.core.model.exec;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.exec.ITasksDynamicProducer;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IReduceAlgoInstance;
import genlab.core.model.meta.IReduceAlgo;

import java.util.HashMap;
import java.util.Map;

/**
 * A basis for container algo executions. 
 * The supervisor starts subexecution iterations. 
 * It contains subtasks, each subtask corresponding to one iteration.
 * 
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

	protected abstract void initFirstRun();
	
	protected abstract void startOfIteration();
	
	protected abstract boolean shouldContinueRun();
	
	protected abstract void endOfRun();
	
	public void createNewTasks() {
		
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
		if (state != ComputationState.FINISHED_OK)
			return;
		
		messages.debugTech("notifying dependant children that we finished with success", getClass());
		
		for (IAlgoInstance algoInst : this.algoInst.getAlgoInstancesDependingToOurChildren()) {
			try {
				IReduceAlgoInstance algoInstReduce = (IReduceAlgoInstance)algoInst;
				algoInstReduce.notifyActualEnd(state);
			} catch (ClassCastException e) {
				throw new ProgramException("Reduce algorithms should always create IReduceAlgoInstance instances.");
			}
		}
		
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
				instance2execForSubtasks,
				getSuffixForCurrentIteration()
				);
		
		// now create the links to call this iteration
		messages.traceTech("init links for this iteration...", getClass());
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
	
	
	

}
