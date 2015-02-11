package genlab.core.model.instance;

import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IInputOutput;
import genlab.core.parameters.InstanceNameParameter;
import genlab.core.parameters.Parameter;
import genlab.core.usermachineinteraction.MessageLevel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
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
	
	protected transient Collection<IParametersListener> parametersListener = null;

	
	private Map<IInputOutput<?>,IInputOutputInstance> inputs2inputInstances = new LinkedHashMap<IInputOutput<?>, IInputOutputInstance>();
	private Map<IInputOutput<?>,IInputOutputInstance> outputs2outputInstances = new LinkedHashMap<IInputOutput<?>, IInputOutputInstance>();
	
	// very basic management of algo parameters;
	// TODO improve algo parameters
	protected Map<String,Object> parametersKey2value = new HashMap<String, Object>();
	
	private IAlgoContainerInstance container = null;
	
	protected transient InstanceNameParameter paramChangeName;
	
	/**
	 * Local parameters
	 */
	protected transient List<Parameter<?>> localParameters;
	protected transient Map<String, Parameter<?>> localParameterId2param;
	
	
	public AlgoInstance(IAlgo algo, IGenlabWorkflowInstance workflow, String id) {

		this.id = id;
		this.algo = algo;
		this.name = algo.getName() + " " + (workflow==null?"":workflow.getCountOfAlgo(algo)+1); 
		
		this._initializeParamChangeName();
		
		
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
						workflow.getNextId(workflow.getId()+".algos."+algo.getId())
						)

				);
	}


	@Override
	public void _initializeParamChangeName() {
		
		this.localParameters = new LinkedList<Parameter<?>>();
		this.localParameterId2param = new HashMap<String, Parameter<?>>();

		this.paramChangeName = new InstanceNameParameter(
				this, 
				this.getId()+"._name", 
				"name", 
				"the name of this algorithm", 
				this.name
				);
		
		declareParameter(paramChangeName, this.name);
		
	}
	
	protected void declareParameter(Parameter<?> p) {
		if (!localParameters.contains(p))
			localParameters.add(p);
	}


	protected void declareParameter(Parameter<?> p, Object value) {
		if (!localParameters.contains(p))
			localParameters.add(p);
		parametersKey2value.put(p.getId(), value);
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
	public IInputOutputInstance getInputInstanceForInput(String inputId) {
		return inputs2inputInstances.get(algo.getInputInstanceForId(inputId));
	}

	@Override
	public IInputOutputInstance getOutputInstanceForOutput(String outputId) {
		return outputs2outputInstances.get(algo.getOutputInstanceForId(outputId));
	}
	@Override
	public IInputOutputInstance getOutputInstanceForOutput(IInputOutput<?> meta) {
		return outputs2outputInstances.get(meta);
	}
	
	public Object getValueForParameter(String name) {
		
		if (paramChangeName.getId().equals(name))
			return this.name;
		
		Object res = parametersKey2value.get(name);
		
		if (res == null) {
			// find default value
			res = getParameter(name).getDefaultValue();
		}
		
		return res;
	}
	
	public boolean isParameterAtDefaultValue(String name) {
		return !parametersKey2value.containsKey(name);
	}
	
	public Object getValueForParameter(Parameter<?> param) {
		if (param == paramChangeName)
			return this.name;
		else
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
		
		if (!hasParameter(name) && paramChangeName.getName().equals(name))
			throw new WrongParametersException("wrong parameter "+name+": no parameter found with this name");
	
		if (name.equals(paramChangeName.getId())) {
			// specific case: rename !
			setName((String)value);
			return;
		}
		
		final Object previousValue = parametersKey2value.get(name); 
		
		if ((previousValue == null) || (!previousValue.equals(value))) {
			parametersKey2value.put(name, value);
			if (workflow != null)
				workflow._notifyAlgoChanged(this);
			
			if (parametersListener != null)
				for (IParametersListener l : parametersListener) {
					try {
						l.parameterValueChanged(this, name, value);
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}
		}
		
	}
	

	@Override
	public void setValueForParameter(Parameter<?> parameter, Object value) {
		setValueForParameter(parameter.getId(), value);
	}

	public void clearValueForParameter(String name) {
		
		if (!hasParameter(name))
			throw new WrongParametersException("wrong parameter "+name);
		
		parametersKey2value.remove(name);
	}

	@Override
	public void checkForRun(WorkflowCheckResult res) {
		
		// ensure our algo is available
		if (!algo.isAvailable()) {
			res.messages.add(new TextMessageFromAlgoInstance(
					this, 
					MessageLevel.ERROR, 
					"the algorithm "+algo.getName()+" is not available in this environment; check the startup messages for more details"
					)
			);
		}
		
		// ensure that inputs are connected
		for (IInputOutput<?> input: inputs2inputInstances.keySet()) {
			
			IInputOutputInstance in = inputs2inputInstances.get(input);
			
			Collection<IConnection> inputConnections = in.getConnections();
			
			// ensure the input is connected (if necessary)
			if (inputConnections.isEmpty() && !in.getMeta().isFacultative())
				res.messages.add(new TextMessageFromAlgoInstance(
						this, 
						MessageLevel.ERROR, 
						"input "+in.getMeta().getName()+" is not connected"
						)
				);
			else if (!in.getMeta().acceptsMultipleInputs() && inputConnections.size() > 1) 
				res.messages.add(new TextMessageFromAlgoInstance(this, MessageLevel.ERROR, "input "+in.getMeta().getName()+" is connected to several inputs"));
			
		}
		
		// ensure that outputs are connected
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
		setValueForParameter(paramChangeName, name);
		
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
		// TODO optimiser
		LinkedList<Parameter<?>> res = new LinkedList<Parameter<?>>();
		for (Parameter<?> p: localParameters) {
			res.add(p);
		}
		res.addAll(algo.getParameters());
		return res;
	}

	@Override
	public boolean hasParameter(String id) {
		return localParameterId2param.containsKey(id) || algo.hasParameter(id);
	}
	
	@Override
	public Parameter<?> getParameter(String id) {
		Parameter<?> res = localParameterId2param.get(id);
		if (res != null)
			return res;
		else
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

	
	@Override
	public void addParametersListener(IParametersListener list) {
		
		if (parametersListener == null)
			parametersListener = new LinkedList<IParametersListener>();
		
		if (!parametersListener.contains(list))
			parametersListener.add(list);
		
		
	}

	@Override
	public void removeParametersListener(IParametersListener list) {
		
		if (parametersListener == null)
			return;
		
		parametersListener.remove(list);
		
	}



}
