package genlab.core.model.instance;

import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IInputOutput;
import genlab.core.usermachineinteraction.MessageLevel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AlgoInstance implements IAlgoInstance {

	private String id;
	
	private transient IAlgo algo;
	private transient IGenlabWorkflowInstance workflow;
	
	private Map<IInputOutput<?>,IInputOutputInstance> inputs2inputInstances = new HashMap<IInputOutput<?>, IInputOutputInstance>();
	private Map<IInputOutput<?>,IInputOutputInstance> outputs2outputInstances = new HashMap<IInputOutput<?>, IInputOutputInstance>();
	
	// very basic management of algo parameters;
	// TODO improve algo parameters
	private Map<String,Object> parameters = new HashMap<String, Object>();
	
	public AlgoInstance(IAlgo algo, IGenlabWorkflowInstance workflow, String id) {

		this.id = id;
		this.algo = algo;
		
		if (workflow != null) {
			_setWorkflowInstance(workflow);
			workflow.addAlgoInstance(this);
		} 
		// init in and outs
		for (IInputOutput<?> input : algo.getInputs()) {
			inputs2inputInstances.put(
					input, 
					new InputInstance(input, this)
					);
		}
		for (IInputOutput<?> output : algo.getOuputs()) {
			outputs2outputInstances.put(
					output, 
					new OutputInstance(output, this)
					);
		}
	}

	public AlgoInstance(IAlgo algo, IGenlabWorkflowInstance workflow) {
		this(
				algo, 
				workflow, 
				(workflow==null?
						algo.getId()+"."+System.currentTimeMillis()
						:
						workflow.getId()+".algos."+algo.getId()+"."+workflow.getAlgoInstances().size()
						)

				);
	}

	
	@Override
	public final String getId() {
		return id;
	}

	@Override
	public final IAlgo getAlgo() {
		return algo;
	}

	@Override
	public IAlgoExecution execute(IExecution exec) {	
		
		// default behavior is to try to execute the algo in a generic way
		return algo.createExec(exec, this);		
		
	}

	@Override
	public IGenlabWorkflowInstance getWorkflow() {
		return workflow;
	}

	@Override
	public String getName() {
		return algo.getName();
	}

	@Override
	public void delete() {
		if (workflow != null)
			workflow.removeAlgoInstance(this);
	}
	
	public void _setWorkflow(IGenlabWorkflowInstance workflow) {
		this.workflow = workflow;
		if (workflow != null)
			workflow.addAlgoInstance(this);
	}
	
	public void _setAlgo(IAlgo algo) {
		this.algo = algo;
	}


	@Override
	public Collection<IInputOutputInstance> getInputInstances() {
		return inputs2inputInstances.values();
	}


	@Override
	public Collection<IInputOutputInstance> getOutputInstances() {
		return outputs2outputInstances.values();
	}


	@Override
	public IInputOutputInstance getInputInstanceForInput(IInputOutput<?> meta) {
		return inputs2inputInstances.get(meta);
	}


	@Override
	public IInputOutputInstance getOutputInstanceForOutput(IInputOutput<?> meta) {
		return outputs2outputInstances.get(meta);
	}
	
	public Object getValueForParameter(String name) {
		return parameters.get(name);
	}
	
	public Map<String,Object> getParametersAndValues() {
		return Collections.unmodifiableMap(parameters);
	}
	

	public boolean hasParameters() {
		return !parameters.isEmpty();
	}

	public void setValueForParameter(String name, Object value) {
		if (!algo.hasParameter(name))
			throw new WrongParametersException("wrong parameter "+name);
		parameters.put(name, value);
	}


	@Override
	public void checkForRun(WorkflowCheckResult res) {
		
		// ensure that inputs are connected
		for (IInputOutputInstance in : inputs2inputInstances.values()) {
			Collection<IConnection> inputConnections = in.getConnections();
			
			if (inputConnections.isEmpty())
				res.messages.add(new TextMessageFromAlgoInstance(
						this, 
						MessageLevel.ERROR, 
						"input "+in.getMeta().getName()+" is not connected"
						)
				);
			else if (inputConnections.size() > 1) 
				res.messages.add(new TextMessageFromAlgoInstance(this, MessageLevel.ERROR, "input "+in.getMeta().getName()+" is connected to several inputs"));
			
		}
		
		// ensure that inputs are connected
		for (IInputOutputInstance out : outputs2outputInstances.values()) {
			Collection<IConnection> inputConnections = out.getConnections();
			
			if (inputConnections.isEmpty())
				res.messages.add(new TextMessageFromAlgoInstance(
						this, 
						MessageLevel.TIP,
						"output "+out.getMeta().getName()+" is not connected, its result will be lost"
						)
				);
			
			
		}
				
	}

	public String toString() {
		return id;
	}


	@Override
	public void _setWorkflowInstance(IGenlabWorkflowInstance workflow) {
		this.workflow = workflow;
		workflow.addAlgoInstance(this);
	}


}
