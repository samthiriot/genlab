package genlab.core.model.exec;

import genlab.core.commons.ProgramException;
import genlab.core.exec.ExecutionTask;
import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.usermachineinteraction.ListOfMessages;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Minimal algo execution: able to retrieve the algo, computation progress, publish a result.
 * 
 * 
 * 
 * @author Samuel THiriot
 *
 * @param 
 */
public abstract class AbstractAlgoExecution extends ExecutionTask implements IAlgoExecution {

	protected final IAlgoInstance algoInst;
	protected final IComputationProgress progress;
	private IComputationResult result = null;
	
	protected final IExecution exec;
	
	/**
	 * For each input, associates it with the incoming connections for this input.
	 */
	protected Map<IInputOutputInstance,Collection<ConnectionExec>> input2connection = new HashMap<IInputOutputInstance, Collection<ConnectionExec>>();

	protected Set<IInputOutputInstance> inputsNotAvailable = null;

	protected ListOfMessages messages;
	
	/**
	 * During init, creates the input executable connections
	 * @param algoInst
	 * @param progress
	 */
	public AbstractAlgoExecution(IExecution exec, IAlgoInstance algoInst, IComputationProgress progress) {
		this.exec = exec;
		this.algoInst = algoInst;
		this.progress = progress;
		progress._setAlgoExecution(this);
		
		// at the very beginning, all the inputs are waiting for data
		inputsNotAvailable = new HashSet<IInputOutputInstance>(algoInst.getInputInstances());
		progress.setComputationState(ComputationState.WAITING_DEPENDENCY);
		
		messages = exec.getListOfMessages();
	}	
	
	public void initInputs(Map<IAlgoInstance,IAlgoExecution> instance2exec) {
		
		for (IInputOutputInstance input : algoInst.getInputInstances()) {
			createInputExecutableConnection(input, instance2exec);
		}
	}
	
	protected void createInputExecutableConnection(IInputOutputInstance input, Map<IAlgoInstance,IAlgoExecution> instance2exec) {
		
		for (IConnection c : input.getConnections()) {
			createInputExecutableConnection(input, c, instance2exec);
		}
		
	}
	
	protected Collection<ConnectionExec> getConnectionsForInput(IInputOutputInstance input) {
		Collection<ConnectionExec> conns = input2connection.get(input);
		if (conns == null) {
			conns = new LinkedList<ConnectionExec>();
			input2connection.put(input, conns);
		}
		return conns;
	}

	
	protected void createInputExecutableConnection(IInputOutputInstance input, IConnection c, Map<IAlgoInstance,IAlgoExecution> instance2exec) {
	
		// the executable we listen for
		IAlgoExecution fromExec = null;
		boolean connectToParent = (
				// the "from" algo is not in the same container
				(c.getFrom().getAlgoInstance().getContainer() != algoInst.getContainer())
				);
				// ... then I should connect to parent 
		

		if (!connectToParent) {
			// we are in the same container; let's link as usual
			fromExec = instance2exec.get(c.getFrom().getAlgoInstance());
			//GLLogger.traceTech("creating an exec link from "+fromExec, getClass());
		} else {
			// we are not in the same container; 
			// so I will listen for this container, which will act as a proxy for this case
			// maybe i'm in the container and not my parent; so my container will warn me when ready
			// or i'm out of the container; in this case the targer container will also warn me when necesssary
			fromExec = instance2exec.get((algoInst.getContainer()==null?c.getFrom().getAlgoInstance().getContainer():algoInst.getContainer()));
			//GLLogger.traceTech("creating an exec link from the container "+fromExec, getClass());
		}
		
		if (fromExec == null)
			throw new ProgramException("unable to find an executable for algo instance "+c.getFrom());
		
		ConnectionExec cEx = new ConnectionExec(
				c, 
				fromExec, 
				this,
				!connectToParent	// don't check when we do fancy things
				);
		
		getConnectionsForInput(input).add(cEx);
		
		addPrerequire(fromExec);
		
		if (connectToParent) {
			// warn the parent who eager we are to receive orders from him
			((AbstractContainerExecution)fromExec).declareInputExpected(cEx, input, c, instance2exec);
		}
		
		
	}

	@Override
	public final IComputationProgress getProgress() {
		return progress;
	}

	@Override
	public final IComputationResult getResult() {
		return result;
	}
	
	protected void setResult(IComputationResult res) {
		this.result = res;
	}
	
	@Override
	public IAlgoInstance getAlgoInstance() {
		return algoInst;
	}

	protected Object getInputValueForInput(IInputOutputInstance input) {
		
		Collection<ConnectionExec> cs = input2connection.get(input);
		if (cs == null)
			throw new ProgramException("unable to find the executable connection for this input: "+input);
		
		if (cs.isEmpty())
			throw new ProgramException("unable to find the executable connection for this input: "+input);
		
		if (cs.size() > 1)
			throw new ProgramException("there are several conncetions for this input: "+input);
		
		final ConnectionExec c = cs.iterator().next();
		
		return input.getMeta().decodeFromParameters(c.getValue());
	}
	
	public Object getInputValueForInput(IInputOutput<?> input) {
		
		return getInputValueForInput(
				getAlgoInstance().getInputInstanceForInput(input)
				);
		
	}

	protected Map<IConnection,Object> getInputValuesForInput(IInputOutputInstance input) {
		
		Collection<ConnectionExec> cs = input2connection.get(input);
		if (cs == null)
			throw new ProgramException("unable to find the executable connection for this input: "+input);
		
		if (cs.isEmpty())
			throw new ProgramException("unable to find the executable connection for this input: "+input);
		
		Map<IConnection,Object> map = new HashMap<IConnection, Object>();
		for (ConnectionExec ce : cs) {
			map.put(
					ce.c, 
					input.getMeta().decodeFromParameters(ce.getValue())
					);
		}
		
		
		return map;
	}
	
	protected Map<IConnection,Object> getInputValuesForInput(IInputOutput<?> input) {

		return getInputValuesForInput(
				getAlgoInstance().getInputInstanceForInput(input)
				);
	}
	
	public IExecution getExecution() {
		return exec;
	}

	@Override
	public void notifyInputAvailable(IInputOutputInstance to) {
		
		// ignore all 
		if (progress.getComputationState() != ComputationState.WAITING_DEPENDENCY)
			return;
		
		if (to.getMeta().acceptsMultipleInputs()) {
			// this input accepts / expects several connection; so we have to check for all these connections !
			
			boolean allConnectionsProvidedValue = true;
			
			for (ConnectionExec c: input2connection.get(to)) {
				if (c.getValue() == null) {
					allConnectionsProvidedValue = false;
					break;
				}
			}
			
			if (allConnectionsProvidedValue)
				inputsNotAvailable.remove(to);
			
		} else
			// this input only accepts one connection; so we can assume it is satisfied :-)
			inputsNotAvailable.remove(to);
		
		// maybe now we have all the required inputs ?
		if (inputsNotAvailable.isEmpty()) {
			exec.getListOfMessages().traceTech("all inputs are available, now ready to run !", getClass());
			progress.setComputationState(ComputationState.READY);
		}
			
	}
	
	/**
	 * Returns true if the output is used. Children should take care of not computing things that are not useful (for instance, 
	 * results which are not used !).
	 * @param output
	 * @return
	 */
	public boolean isUsed(IInputOutputInstance output) {
		return !output.getConnections().isEmpty();
	}

	public boolean isUsed(IInputOutput<?> output) {
		return !algoInst.getOutputInstanceForOutput(output).getConnections().isEmpty();
	}


	/**
	 * Returns true if no output is used, that is 
	 * no result is observed. Standard behavior in this case is to 
	 * not compute, because nobody cares about the results; still, 
	 * in this case, the user has to be warned.
	 * @return
	 */
	protected boolean noOutputIsUsed() {
		
		for (IInputOutputInstance output : algoInst.getOutputInstances()) {
			if (isUsed(output))
				return false;
		}
		return true;
	}
	
	@Override
	public String getName() {
		return algoInst.getName();
	}
	

	@Override
	public void reset() {
		progress.setComputationState(ComputationState.WAITING_DEPENDENCY);
		inputsNotAvailable.clear();
		inputsNotAvailable.addAll(algoInst.getInputInstances());

	}

	@Override
	public int getThreadsUsed() {
		return 1;
	}

}
