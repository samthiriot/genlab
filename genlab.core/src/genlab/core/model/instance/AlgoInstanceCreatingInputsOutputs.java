package genlab.core.model.instance;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IInputOutput;
import genlab.core.usermachineinteraction.GLLogger;

@SuppressWarnings("serial")
public abstract class AlgoInstanceCreatingInputsOutputs extends AlgoInstance implements IParametersListener {

	protected transient Set<IInputOutput<?>> localInputs = new LinkedHashSet<IInputOutput<?>>();
	protected transient Set<IInputOutput<?>> localOutputs = new LinkedHashSet<IInputOutput<?>>();
	
	protected boolean pendingEventAlgoChange = false;
	
	public AlgoInstanceCreatingInputsOutputs(IAlgo algo,
			IGenlabWorkflowInstance workflow, String id) {
		super(algo, workflow, id);
	}

	public AlgoInstanceCreatingInputsOutputs(IAlgo algo,
			IGenlabWorkflowInstance workflow) {
		super(algo, workflow);
	}

	
	
	@Override
	public void _setAlgo(IAlgo algo) {
		super._setAlgo(algo);
	}

	@Override
	public void _setWorkflowInstance(IGenlabWorkflowInstance workflow) {
		super._setWorkflowInstance(workflow);
		if (pendingEventAlgoChange)
			workflow.dispatchAlgoChange(this);

	}

	protected void removeLocalInput(IInputOutput<?> i) {
		
		// remove the connections that might exist
		IInputOutputInstance iInst = inputs2inputInstances.get(i);
		if (iInst != null && !iInst.getConnections().isEmpty()) {
			for (IConnection c: iInst.getConnections()) {
				workflow.removeConnection(c);
			}
		}
		
		localInputs.remove(i);
		inputs2inputInstances.remove(i);
		
		if (workflow != null)
			workflow.dispatchAlgoChange(this);
		else 
			pendingEventAlgoChange = true;
	}
	

	protected void removeLocalOutput(IInputOutput<?> o) {
		
		// remove the connections that might exist
		IInputOutputInstance oInst = outputs2outputInstances.get(o);
		if (oInst != null && !oInst.getConnections().isEmpty()) {
			for (IConnection c: oInst.getConnections()) {
				workflow.removeConnection(c);
			}
		}
		
		localOutputs.remove(o);
		outputs2outputInstances.remove(o);
		
		if (workflow != null)
			workflow.dispatchAlgoChange(this);
		else 
			pendingEventAlgoChange = true;
	}
	
	protected void declareLocalInput(IInputOutput<?> input) {
		localInputs.add(input);
		InputInstance i = new InputInstance(input, this);
		i.setAcceptMultipleInputs(input.acceptsMultipleInputs());		
		inputs2inputInstances.put(
				input, 
				i
				);
		if (workflow != null)
			workflow.dispatchAlgoChange(this);
		else 
			pendingEventAlgoChange = true;
	}
	

	protected void declareLocalOutput(IInputOutput<?> output) {
		localOutputs.add(output);
		OutputInstance o = new OutputInstance(output, this);
		outputs2outputInstances.put(
				output, 
				o
				);
		if (workflow != null)
			workflow.dispatchAlgoChange(this);
		else 
			pendingEventAlgoChange = true;
	}
	
	protected abstract void refreshFromParameters(String parameterId, Object novelValue);

	@Override
	public final void parameterValueChanged(IAlgoInstance ai, String parameterId,
			Object novelValue) {
		if (ai == this) {
			try {
				refreshFromParameters(parameterId, novelValue);
			} catch (Exception e) {
				e.printStackTrace();
				GLLogger.warnTech("internal error while detecting inputs and outputs from model", getClass());
			}
		}
	}
	

	@Override
	public Set<IInputOutput> getInputs() {
		
		// quick solution
		if (localInputs.isEmpty())
			return super.getInputs();
		
		HashSet<IInputOutput> inputsAll = new LinkedHashSet<>(algo.getInputs());
		inputsAll.addAll(localInputs);
		return inputsAll;
	}

	@Override
	public boolean containsInput(IInputOutput input) {
		if (localInputs.contains(input))
			return true;
		return super.containsInput(input);
	}

	@Override
	public boolean containsOutput(IInputOutput output) {
		if (localOutputs.contains(output))
			return true;
		return super.containsOutput(output);
	}
	
	@Override
	public Set<IInputOutput> getOuputs() {
		// quick solution
		if (localOutputs.isEmpty())
			return super.getInputs();
		
		HashSet<IInputOutput> outputsAll = new LinkedHashSet<>(algo.getInputs());
		outputsAll.addAll(localInputs);
		return outputsAll;
	}
	
}
