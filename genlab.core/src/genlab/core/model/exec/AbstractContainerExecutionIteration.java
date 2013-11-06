package genlab.core.model.exec;

import genlab.core.commons.ProgramException;
import genlab.core.exec.ICleanableTask;
import genlab.core.exec.IExecution;
import genlab.core.exec.ITask;
import genlab.core.model.instance.AlgoContainerInstance;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IInputOutputInstance;

import java.util.HashMap;
import java.util.Map;

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
					extends AbstractContainerExecution 
					implements ICleanableTask {

	protected final Map<IConnection,Object> inputConnection2value;
	
	protected final String nameSuffix;
	
	public AbstractContainerExecutionIteration(IExecution exec,
			IAlgoContainerInstance algoInst, 
			IComputationProgress progress, 
			Map<IConnection,Object> inputConnection2value, 
			Map<IAlgoInstance,IAlgoExecution> instance2exec,
			String suffix
			) {
		
		super(exec, algoInst, progress);
	
		this.inputConnection2value = inputConnection2value;
		this.nameSuffix = suffix;
		
		// prepare links for internal use
		instance2execOriginal = instance2exec;
		instance2execForSubtasks = new HashMap<IAlgoInstance, IAlgoExecution>(instance2exec);
		for (IConnection cIn : algoInst.getConnectionsComingFromOutside()) {
			// let's assume that each connection coming from outside actually comes from ME
			instance2execForSubtasks.put(cIn.getFrom().getAlgoInstance(), this);
		}
		
		initSubtasks();
		progress.setComputationState(ComputationState.WAITING_DEPENDENCY);


	}
	
	protected void createOutputExecutableConnection(
			IConnection cOut, 
			Map<IAlgoInstance,IAlgoExecution> instance2exec
			) {
		
		final IAlgoExecution ourChildFrom = instance2execForSubtasks.get(cOut.getFrom().getAlgoInstance());
		if (ourChildFrom == null)
			throw new ProgramException("unable to find our own child "+cOut.getFrom().getAlgoInstance());
		
		final IAlgoExecution toExec = instance2execOriginal.get(cOut.getTo().getAlgoInstance());
		if (toExec == null)
			throw new ProgramException("unable to find the destination executable: "+cOut.getTo().getAlgoInstance());
		IReduceAlgoExecution toExecReduce = null;
		try {
			toExecReduce = (IReduceAlgoExecution)toExec;
		} catch (ClassCastException e) {
			throw new ProgramException("children should be reduce algos");
		}
	
		ConnectionExecFromIterationToReduce cEx = new ConnectionExecFromIterationToReduce(
				cOut, 
				ourChildFrom, 
				toExecReduce
				);
		
	}
	

	@Override
	public void initInputs(Map<IAlgoInstance,IAlgoExecution> instance2exec) {
		
		AlgoContainerInstance algoContainerInst = (AlgoContainerInstance)algoInst;
		
		// creates links from this iteration to its supervisor
		for (IConnection cIn : algoContainerInst.getConnectionsComingFromOutside()) {
			
			createInputExecutableConnection(
					cIn.getTo(), 
					instance2exec
					);

		}
		
		// and create links from this iteration to children
		for (IConnection cOut: algoContainerInst.getConnectionsGoingToOutside()) {
			
			createOutputExecutableConnection(
					cOut,
					instance2exec
					);
		}
		
	}

	private void initSubtasks() {
		
		messages.debugTech("init subtasks for this iteration", getClass());
		
		// first create each child execution...
		messages.traceTech("create executables for child tasks", getClass());
		for (IAlgoInstance child : algoInst.getChildren()) {
			IAlgoExecution childExec = child.execute(exec);
			instance2execForSubtasks.put(child, childExec);
			childExec.setParent(this);
			addTask(childExec);
		}
		
		// and let each child initiate its links
		messages.traceTech("init links for child tasks", getClass());
		for (IAlgoInstance child : algoInst.getChildren()) {
			IAlgoExecution childExec = instance2execForSubtasks.get(child);
			childExec.initInputs(instance2execForSubtasks);
		}
		
		
		// also listen for their state so we know when they finished !
		messages.traceTech("setting up the monitoring of child statuses", getClass());
		for (IConnection outputConnection : algoInst.getConnectionsGoingToOutside()) {
			
			instance2execForSubtasks.get(outputConnection.getFrom().getAlgoInstance()).getProgress().addListener(this);
		}
		
	}

	@Override
	protected IConnectionExecution createInputExecutableConnection(IInputOutputInstance input, IConnection c, Map<IAlgoInstance,IAlgoExecution> instance2exec) {
		
		// for the "from" side, we deal with connection from out of this iteration to something;
		// so let's refer to the mapping provided as a parameter
		final IAlgoExecution fromExec = instance2exec.get(c.getFrom().getAlgoInstance());
		
		// for the "to" side, we deal with internal executions known by us only;
		// so let's use our internal mapping
		IAlgoExecution toExec = null;
		if (c.getTo().getAlgoInstance().getContainer() == this.getAlgoInstance()) {
			toExec = this;
		} else {
			toExec = instance2execForSubtasks.get(c.getTo().getAlgoInstance());
		}
				
		IConnectionExecution cEx = new ConnectionExecFromIterationToChild(
				c, 
				fromExec, 
				(IAlgoExecutionOneshot) toExec,
				false	// don't check when we do fancy things
				);
		
		getOrCreateConnectionsForInput(input).add(cEx);
		
		
		addPrerequire(fromExec);

		return cEx;
		
	}
	
	@Override
	public void run() {
		
		ComputationResult res = new ComputationResult(algoInst, progress, messages);
		setResult(res);
		
		messages.debugTech("starting this iteration", getClass());
		
		progress.setComputationState(ComputationState.STARTED);
		
		messages.debugTech("defining the inputs of the iteration executions", getClass());
		
		for (IConnection c: inputConnection2value.keySet()) {
			
			final IAlgoExecution toExec = instance2execForSubtasks.get(c.getTo().getAlgoInstance());
			
			for (IConnectionExecution cEx : toExec.getConnectionsForInput(c.getTo())) {
				Object value = inputConnection2value.get(c);
				messages.traceTech("defining for "+c+": "+value, getClass());
				cEx.forceValue(value);

			}
			///ConnectionExec cEx = getExecutableConnectionForConnection(c);
			
		}
		
		// done.
		messages.debugTech("initialized all the children inputs; now waiting for them to finish", getClass());

	
		// we DO NOT set the progress to finished, because this container task will only be assumed to be finished once its childrne will be finiched as well
		
		
	}


	@Override
	public String getName() {
		return algoInst.getName()+nameSuffix;
	}


	@Override
	public void clean() {
	
		// clean local data
		input2connection.clear();
		
		// super clean
		super.clean();
	}

	
}
