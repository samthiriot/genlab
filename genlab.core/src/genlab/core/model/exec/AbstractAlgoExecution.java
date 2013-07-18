package genlab.core.model.exec;

import genlab.core.commons.ProgramException;
import genlab.core.exec.ExecutionTask;
import genlab.core.exec.IExecution;
import genlab.core.model.instance.Connection;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.meta.IInputOutput;

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
	private Map<IInputOutputInstance,Collection<ConnectionExec>> input2connection = new HashMap<IInputOutputInstance, Collection<ConnectionExec>>();

	private Set<IInputOutputInstance> inputsNotAvailable = null;

	
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
		
		
	}	
	
	public void initInputs(Map<IAlgoInstance,IAlgoExecution> instance2exec) {
		
		for (IInputOutputInstance input : algoInst.getInputInstances()) {
			createInputExecutableConnection(input, instance2exec);
		}
	}
	
	protected void createInputExecutableConnection(IInputOutputInstance input, Map<IAlgoInstance,IAlgoExecution> instance2exec) {
		
		for (IConnection c : input.getConnections()) {
			IAlgoExecution fromExec = instance2exec.get(c.getFrom().getAlgoInstance());
			
			if (fromExec == null)
				throw new ProgramException("unable to find an executable for algo instance "+c.getFrom());
			
			ConnectionExec cEx = new ConnectionExec(
					c, 
					fromExec, 
					this
					);
			
			Collection<ConnectionExec> conns = input2connection.get(input);
			if (conns == null) {
				conns = new LinkedList<ConnectionExec>();
				input2connection.put(input, conns);
			}
			conns.add(cEx);
			
			addPrerequire(fromExec);
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
		
		if (to.getMeta().acceptsMultipleInputs()) {
			// this input accepts / expects several connection; so we have to check for all these connections !
			
			boolean allConnectionsProvidedValue = true;
			
			for (ConnectionExec c: input2connection.get(to)) {
				if (c.getValue() == null) {
					allConnectionsProvidedValue = false;
				}
			}
			
			if (allConnectionsProvidedValue)
				inputsNotAvailable.remove(to);
			
		} else
			// this input only accepts one connection; so we can assume it is satisfied :-)
			inputsNotAvailable.remove(to);
		
		// maybe now we have all the required inputs ?
		if (inputsNotAvailable.isEmpty()) {
			exec.getListOfMessages().debugTech("all inputs are available, now ready to run !", getClass());
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
		return "execution of "+algoInst.getName();
	}
	
	
}
