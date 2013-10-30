package genlab.core.model.exec;

import genlab.core.exec.IContainerTask;
import genlab.core.exec.IExecution;
import genlab.core.exec.ITask;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * A basis for container algo executions. 
 * The supervisor starts subexecution iterations. 
 * It contains subtasks, each subtask corresponding to one iteration.
 * 
 * 
 * @author Samuel Thiriot
 *
 */
public abstract class AbstractContainerExecutionSupervisor extends AbstractAlgoExecution implements IContainerTask, IComputationProgressSimpleListener {

	protected IAlgoContainerInstance algoInst;
	
	protected boolean canceled = false;
	

	/**
	 * The connections from me to an algo outside (I'm waiting for its results)
	 */
	//private final Map<IConnection,ConnectionExec> connection2OutsideToMe = new HashMap<IConnection, ConnectionExec>();
	/**
	 * The connections from from inside to me (a child is waiting for directives)
	 */
	//protected final Map<IConnection,ConnectionExec> connection2MeToInside = new HashMap<IConnection, ConnectionExec>();
	/**
	 * The connections from me to inside (I'm waiting the result of a child)
	 */
	//private final Map<IConnection,ConnectionExec> connection2InsideToMe = new HashMap<IConnection, ConnectionExec>();
	/**
	 * The connections from outside to me (a non child is waiting for my result)
	 */
	//private final Map<IConnection,ConnectionExec> connection2MeToOutside = new HashMap<IConnection, ConnectionExec>();
	
	// TODO change ITask to IAbstractExecution
	private Collection<ITask> subtasks = new LinkedList<ITask>();
	
	/**
	 * The mapping instance 2 execution provided at an init step.
	 * Required for the delayed init of subtasks.
	 */
	private Map<IAlgoInstance,IAlgoExecution> instance2exec;
	
	public AbstractContainerExecutionSupervisor(
			IExecution exec,
			IAlgoContainerInstance algoInst) {
		super(
				exec, 
				algoInst, 
				new ComputationProgressWithSteps()
				);

		this.algoInst = algoInst;
		
		
	}

	@Override
	public long getTimeout() {
		// no timeout here; the timeouts are managed at a more atomic level
		return -1;
	}

	protected abstract void initFirstRun();
	
	protected abstract void startOfIteration();
	
	protected abstract boolean shouldContinueRun();
	
	protected abstract void endOfRun();
	
	@Override
	public void run() {

		messages.debugTech("starting", getClass());
		
		initFirstRun();
		
		progress.setComputationState(ComputationState.STARTED);
		
		while (shouldContinueRun()) {
		
			if (canceled) {
				messages.infoUser("cancelled; interrupting iterations.", getClass());
				progress.setComputationState(ComputationState.FINISHED_CANCEL);
				return;
			}
				
			startOfIteration();
			
			{
				IAlgoExecution subExec =  createNextExecutionForOneIteration();
				
				// define several properties of this "sub"
				subExec.setParent(this);
				addTask(subExec);
				subExec.getProgress().addListener(this);
				
				// and, provide them with inputs !
				
				
			}
			
			// TODO ?

			
			// suggest garbage collecting right now
		}
		
		// deliver results
		messages.debugTech("queued all iterations.", getClass());
		/*
		for (IConnection c: connection2InsideToMe.keySet()) {
			
			Object value = connection2InsideToMe.get(c).getValue();
			messages.debugTech("transmitting value "+value+" to "+c.getTo().getAlgoInstance(), getClass());
			connection2MeToOutside.get(c).forceValue(value);
		}
		*/
		// TODO tell each mapreduce that computation is finished
		
		// here the result is empty (nothing exported by the loop itself)
		ComputationResult res = new ComputationResult(algoInst, progress, messages);
		setResult(res);
		
		
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
		this.instance2exec = instance2exec;
		
	}
	
	@Override
	public void kill() {
		// TODO Auto-generated method stub
		this.canceled = true; // TODO attempt to kill the running subtasks !

	}

	@Override
	public void cancel() {
		this.canceled = true; // TODO attempt to kill the running subtasks !
	}


	@Override
	public int getThreadsUsed() {
		// important ! else running only containers would be enough to declare all the threads used, and nothing would ever happen ^^
		return 0;
	}
	

	@Override
	public void addTask(ITask t) {
		subtasks.add(t);
		exec.getRunner().addTask((IAlgoExecution) t);
	}

	@Override
	public Collection<ITask> getTasks() {
		return subtasks;
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
		AbstractContainerExecutionIteration resExecIteration = new AbstractContainerExecutionIteration(
				exec, 
				algoInst, 
				progress, 
				inputConnection2value, 
				getSuffixForCurrentIteration()
				);
		
		// now create the links to call this iteration
		{
			Map<IAlgoInstance, IAlgoExecution> instance2exec = new HashMap<IAlgoInstance, IAlgoExecution>();

			for (IConnection c: algoInst.getConnectionsComingFromOutside()) {
				
				instance2exec.put(
						c.getFrom().getAlgoInstance(), 
						this
						);
			}
			
			resExecIteration.initInputs(instance2exec);
		}
		
		// set values
		{
			
			
			for (IConnection c: algoInst.getConnectionsComingFromOutside()) {
				
				for (ConnectionExec cEx : resExecIteration.getOrCreateConnectionsForInput(c.getTo())) {
					Object value = inputConnection2value.get(c);
					messages.debugTech("defining value "+value+" for "+c, getClass());
					cEx.forceValue(value);
				}
			}
			
			
				
		}
		
		return resExecIteration;
	}
	
	@Override
	public void computationStateChanged(IComputationProgress progress) {
		// ensure all done !
		ComputationState ourState = null;
		for (ITask sub: subtasks) {
			switch (sub.getProgress().getComputationState()) {
			case FINISHED_OK:
				if (ourState == null)
					ourState = sub.getProgress().getComputationState();
				break;
			case FINISHED_FAILURE:
			case FINISHED_CANCEL:
				ourState = sub.getProgress().getComputationState();
				break;
			default:
				// not finished; let's stop analyzing this stuff !
				return;
			}
		}

		messages.traceTech("all subs terminated; should transmit results", getClass());
		progress.setComputationState(ourState);
		
		// suggest a garbage collecting now
		Runtime.getRuntime().gc();
		
	}
	

	@Override
	public void collectEntities(
			Set<IAlgoExecution> execs,
			Set<ConnectionExec> connections
			) {
		
		super.collectEntities(execs, connections);
		
		// also collect my subentities
		for (ITask t: subtasks) {
			((IAlgoExecution)t).collectEntities(execs, connections);
		}
		
		
	}
}
