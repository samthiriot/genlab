package genlab.core.model.instance;

import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IInputOutput;
import genlab.core.parameters.Parameter;
import genlab.core.usermachineinteraction.MessageLevel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Most of the time, an algo instance can be infered automatically 
 * from the algo: it just stores values for the inputs and outputs
 * of its algo, and just deleguates the execution to the algo type.
 *  
 *  TODO add a "disable" flag
 * 
 * @author Samuel Thiriot
 *
 */
public class AlgoInstance implements IAlgoInstance {

	protected String id;
	protected String name;

	protected transient IAlgo algo;
	protected transient IGenlabWorkflowInstance workflow;
	
	private Map<IInputOutput<?>,IInputOutputInstance> inputs2inputInstances = new LinkedHashMap<IInputOutput<?>, IInputOutputInstance>();
	private Map<IInputOutput<?>,IInputOutputInstance> outputs2outputInstances = new LinkedHashMap<IInputOutput<?>, IInputOutputInstance>();
	
	// very basic management of algo parameters;
	// TODO improve algo parameters
	protected Map<String,Object> parametersKey2value = new HashMap<String, Object>();
	
	private IAlgoContainerInstance container = null;
	
	public AlgoInstance(IAlgo algo, IGenlabWorkflowInstance workflow, String id) {

		this.id = id;
		this.algo = algo;
		this.name = algo.getName() + " " + (workflow==null?"":workflow.getCountOfAlgo(algo)+1); 
		
		// init in and outs
		for (IInputOutput<?> input : algo.getInputs()) {
			InputInstance i = new InputInstance(input, this);
			
			i.setAcceptMultipleInputs(input.acceptsMultipleInputs());
			
			inputs2inputInstances.put(
					input, 
					i
					);
			
		}
		for (IInputOutput<?> output : algo.getOuputs()) {
			outputs2outputInstances.put(
					output, 
					new OutputInstance(output, this)
					);
		}
		

		if (workflow != null) {
			_setWorkflowInstance(workflow);
			//workflow.addAlgoInstance(this);
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
		return name;
	}

	@Override
	public void delete() {
		if (workflow != null)
			workflow.removeAlgoInstance(this);
		if (container != null)
			container.removeChildren(this);
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
		
		Object res = parametersKey2value.get(name);
		
		if (res == null) {
			// find default value
			res = getParameter(name).getDefaultValue();
		}
		
		return res;
	}
	
	public Object getValueForParameter(Parameter<?> param) {
		return getValueForParameter(param.getId());
	}
	
	public Map<String,Object> getParametersAndValues() {
		return Collections.unmodifiableMap(parametersKey2value);
	}
	
	public Map<String,Object> _getParametersAndValues() {
		return parametersKey2value;
	}
	

	public boolean hasParameters() {
		return !parametersKey2value.isEmpty();
	}

	public void setValueForParameter(String name, Object value) {
		
		if (!hasParameter(name))
			throw new WrongParametersException("wrong parameter "+name);
		final Object previousValue = parametersKey2value.get(name); 
		
		if ((previousValue == null) || (!previousValue.equals(value))) {
			parametersKey2value.put(name, value);
			if (workflow != null)
				workflow._notifyAlgoChanged(this);
		}
		
	}

	public void clearValueForParameter(String name) {
		
		if (!hasParameter(name))
			throw new WrongParametersException("wrong parameter "+name);
		
		parametersKey2value.remove(name);
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
			else if (!in.getMeta().acceptsMultipleInputs() && inputConnections.size() > 1) 
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
	}

	@Override
	public void setName(String novelName) {
		
		if (name.equals(novelName))
			return;
		
		this.name = novelName;
		
		if (workflow != null)
			workflow._notifyAlgoChanged(this);
		
	}

	@Override
	public IAlgoContainerInstance getContainer() {
		return container;
	}

	@Override
	public void setContainer(IAlgoContainerInstance container) {
		this.container = container;
	}

	@Override
	public Collection<IConnection> getAllIncomingConnections() {

		HashSet<IConnection> result = new HashSet<IConnection>();
		
		for (IInputOutputInstance inputInstance: inputs2inputInstances.values()) {
			result.addAll(inputInstance.getConnections());
		}
		
		return result;
	}

	@Override
	public IAlgoInstance cloneInContext(IAlgoContainerInstance containerInput) {
		
		// retrieve target objects
		IGenlabWorkflowInstance workflow = null;
		IAlgoContainerInstance container = null;
		if (containerInput instanceof IGenlabWorkflowInstance) {
			workflow = (IGenlabWorkflowInstance)containerInput;
		} else {
			workflow = containerInput.getWorkflow();
			container = workflow;
		}
		
		
		// create instance
		IAlgoInstance copy = algo.createInstance(workflow);
		copy.setContainer(container);
		
		// duplicate parameters
		for (Map.Entry<String,Object> key2value : parametersKey2value.entrySet()) {
			copy.setValueForParameter(key2value.getKey(), key2value.getValue());
		}

		
		return copy;
	}

	@Override
	public boolean canBeContainedInto(IAlgoContainerInstance container) {
		// true by default
		return true;
	}

	@Override
	public Collection<Parameter<?>> getParameters() {
		// by default, returns the algo's parameters
		return algo.getParameters();
	}

	@Override
	public boolean hasParameter(String id) {
		return algo.hasParameter(id);
	}
	
	@Override
	public Parameter<?> getParameter(String id) {
		return algo.getParameter(id);
	}

	@Override
	public boolean isContainedInto(IAlgoContainerInstance otherInstance) {
		
		// well, "other" is a container; it may be our parent
		// let's dig into our genealogy
		IAlgoContainerInstance currentParentInstance = container;
		while (currentParentInstance != null) {
			if (otherInstance.equals(currentParentInstance))
				return true;
			currentParentInstance = currentParentInstance.getContainer();
		}
		
		return false;
	}

}
