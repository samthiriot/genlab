package genlab.core.model.exec;

import genlab.core.commons.ProgramException;
import genlab.core.exec.ExecutionTask;
import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.meta.IInputOutput;

import java.util.HashMap;
import java.util.HashSet;
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
	
	private Map<IInputOutputInstance,ConnectionExec> input2connection = new HashMap<IInputOutputInstance, ConnectionExec>();

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
			
			input2connection.put(
					input, 
					cEx
					);
			
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
		
		ConnectionExec c = input2connection.get(input);
		if (c == null)
			throw new ProgramException("unable to find the executable connection for this input: "+input);
		
		return input.getMeta().decodeFromParameters(c.getValue());
	}
	
	protected Object getInputValueForInput(IInputOutput<?> input) {
		
		return getInputValueForInput(
				getAlgoInstance().getInputInstanceForInput(input)
				);
		
	}
	
	protected ConnectionExec getConnectionExecForInputInstance(IInputOutputInstance input) {
		return input2connection.get(input);
	}
	
	public IExecution getExecution() {
		return exec;
	}

	@Override
	public void notifyInputAvailable(IInputOutputInstance to) {
		inputsNotAvailable.remove(to);
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
	
}
