package genlab.core.model.exec;

import genlab.core.exec.IContainerTask;
import genlab.core.exec.IExecution;
import genlab.core.exec.ITask;
import genlab.core.model.instance.AlgoContainerInstance;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * An iteration execution (meaning: one run of all the algos into the container) is driven like:
 * <ul>
 * <li>at creation: create all the subexecution tasks for children algo instances</li>
 * <li>at run: provides data to the root children, so they will soon start</li>
 * <li>when each subtask ended: take their results, and pass them to the various child instance (reduce)</li>
 * <li></li>
 * </ul>
 * 
 * 
 * @author Samuel Thiriot
 *
 */
public class AbstractContainerExecutionIteration 
						extends AbstractAlgoExecution 
						implements IContainerTask, IComputationProgressSimpleListener {

	protected final IAlgoContainerInstance algoInst;
	
	/**
	 * All the subtasks for this iteration
	 */
	protected final Collection<ITask> tasks = new LinkedList<ITask>();
	
	protected final Map<IConnection,Object> inputConnection2value;
	
	protected final String nameSuffix;
	
	public AbstractContainerExecutionIteration(IExecution exec,
			IAlgoContainerInstance algoInst, IComputationProgress progress, Map<IConnection,Object> inputConnection2value, String suffix) {
		super(exec, algoInst, progress);
	
		this.algoInst = algoInst;
		this.inputConnection2value = inputConnection2value;
		this.nameSuffix = suffix;
		initSubtasks();
		progress.setComputationState(ComputationState.WAITING_DEPENDENCY);


	}
	

	@Override
	public void initInputs(Map<IAlgoInstance,IAlgoExecution> instance2exec) {
		
		
		for (IConnection cIn : ((AlgoContainerInstance)algoInst).getConnectionsComingFromOutside()) {
			
			createInputExecutableConnection(cIn.getTo(), instance2exec);

		}
		
	}

	private void initSubtasks() {
		
		Map<IAlgoInstance, IAlgoExecution> instance2exec = new HashMap<IAlgoInstance, IAlgoExecution>();
		
		// first create each child execution...
		for (IAlgoInstance child : algoInst.getChildren()) {
			IAlgoExecution childExec = child.execute(exec);
			instance2exec.put(child, childExec);
			childExec.setParent(this);
			addTask(childExec);
		}
		
		// ... complete the list with us as a parent 
		for (IConnection inputConnection : algoInst.getConnectionsComingFromOutside()) {
			instance2exec.put(
					inputConnection.getFrom().getAlgoInstance(), 
					this
					);
		}
		
		// and let them init their links
		for (IAlgoExecution exec: instance2exec.values()) {
			exec.initInputs(instance2exec);
		}
		
		// also listen for their state so we know when they finished !
		for (IConnection outputConnection : algoInst.getConnectionsGoingToOutside()) {
			
			instance2exec.get(outputConnection.getFrom().getAlgoInstance()).getProgress().addListener(this);
		}
		
	}

	@Override
	public long getTimeout() {
		return -1;
	}

	@Override
	public void run() {
		
		messages.debugTech("defining the inputs of the iteration executions", getClass());
		
		for (IConnection c: inputConnection2value.keySet()) {
			
			for (ConnectionExec cEx : getConnectionsForInput(c.getTo())) {
				Object value = inputConnection2value.get(c);
				messages.traceTech("defining for "+c+": "+value, getClass());
				cEx.forceValue(value);

			}
			///ConnectionExec cEx = getExecutableConnectionForConnection(c);
			
		}
		
		// done.
		
		// empty result
		ComputationResult res = new ComputationResult(algoInst, progress, messages);
		setResult(res);
		
		
		
	}

	@Override
	public void kill() {
		// pass the messages to children
		for (ITask subTask : tasks) {
			try {
				subTask.kill();
			} catch (RuntimeException e) {
				messages.warnTech("catched an exception while attempting to cancel subtask "+subTask+": "+e.getMessage(), getClass(), e);
			}
		}
	}

	@Override
	public void cancel() {
		// pass the messages to children
		for (ITask subTask : tasks) {
			try {
				subTask.cancel();
			} catch (RuntimeException e) {
				messages.warnTech("catched an exception while attempting to cancel subtask "+subTask+": "+e.getMessage(), getClass(), e);
			}
		}
	}

	@Override
	public final int getThreadsUsed() {
		return 0;
	}

	@Override
	public void addTask(ITask t) {
		if (!tasks.contains(t))
			tasks.add(t);
	}

	@Override
	public Collection<ITask> getTasks() {
		return tasks;
	}

	@Override
	public void computationStateChanged(IComputationProgress progress) {
		
		// ignore intermediate messages
		if (!progress.getComputationState().isFinished())
			return;
		
		
		
		// ensure all done !
		ComputationState ourState = null;
		for (ITask sub: tasks) {
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
				
		// TODO transmit all the values to the reduce steps
	}

	@Override
	public String getName() {
		return algoInst.getName()+nameSuffix;
	}
	
	@Override
	public void collectEntities(
			Set<IAlgoExecution> execs,
			Set<ConnectionExec> connections
			) {
		
		super.collectEntities(execs, connections);
		
		// also collect my subentities
		for (ITask t: tasks) {
			((IAlgoExecution)t).collectEntities(execs, connections);
		}		
		
	}
	
}
