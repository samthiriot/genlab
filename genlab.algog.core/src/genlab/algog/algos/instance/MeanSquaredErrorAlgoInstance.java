package genlab.algog.algos.instance;

import genlab.algog.algos.meta.GenomeAlgo;
import genlab.algog.algos.meta.MeanSquaredErrorAlgo;
import genlab.algog.algos.meta.ReceiveFitnessAlgo;
import genlab.core.commons.ProgramException;
import genlab.core.commons.WrongParametersException;
import genlab.core.model.instance.AlgoContainerInstance;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.instance.IWorkflowContentListener;
import genlab.core.model.instance.WorkflowCheckResult;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IFlowType;
import genlab.core.model.meta.basics.flowtypes.DoubleFlowType;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;
import genlab.core.parameters.DoubleParameter;
import genlab.core.parameters.IntParameter;
import genlab.core.parameters.Parameter;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class MeanSquaredErrorAlgoInstance 
							extends AlgoInstance 
							// listens for the updates of the workflow, in order to update the parameters accordingly
							implements IWorkflowContentListener {

	private transient Map<IConnection,String> connection2parametersId = new HashMap<IConnection, String>();
	private transient Map<String,Parameter<?>> parameterId2parameter = new HashMap<String, Parameter<?>>();

	public MeanSquaredErrorAlgoInstance(IAlgo algo,
			IGenlabWorkflowInstance workflow, String id) {
		super(algo, workflow, id);
		
	}

	public MeanSquaredErrorAlgoInstance(
			MeanSquaredErrorAlgo algo,
			IGenlabWorkflowInstance workflow) {
		super(algo, workflow);
		
		
		if (workflow != null)
			this.workflow.addListener(this);
	}


	@Override
	public void _setWorkflowInstance(IGenlabWorkflowInstance workflow) {
		IGenlabWorkflowInstance previous = this.workflow;
		
		super._setWorkflowInstance(workflow);
		
		if (previous != null)
			previous.removeListener(this);
		
		this.workflow.addListener(this);
		
		try {
			_updateParametersForConnections();
		} catch (NullPointerException e) {
			// happens when called from init. don't care.
		}
	}
	
	public Map<IConnection,Object> connection2parameterValue() {
		HashMap<IConnection,Object> res = new HashMap<IConnection, Object>(connection2parametersId.size());
		for (IConnection c: connection2parametersId.keySet()) {
			String paramId = connection2parametersId.get(c);
			Parameter<?> param = parameterId2parameter.get(paramId);
			Object value = getValueForParameter(param);
			res.put(c, value);
		}
		return res;
	}

	public void _updateParametersForConnections() {
		
		IInputOutputInstance inInstance = getInputInstanceForInput(MeanSquaredErrorAlgo.INTPUT_VALUES);
		
		Set<IConnection> connectionsToCreate = new HashSet<IConnection>(inInstance.getConnections());
		connectionsToCreate.removeAll(connection2parametersId.keySet());
		
		Set<IConnection> connectionsToRemove = new HashSet<IConnection>(connection2parametersId.keySet());
		connectionsToRemove.removeAll(inInstance.getConnections());
		
		// create connections
		for (IConnection c : connectionsToCreate) {
			
			Parameter<?> param = null;
			
			IFlowType<?> typeFrom = c.getFrom().getMeta().getType();
			
			if (typeFrom.getId().equals(IntegerFlowType.SINGLETON.getId())) {
				// we receive an Integer value
				param = new IntParameter(
						getId(), 
						c.getFrom().getMeta().getName(), 
						"value received by algo "+c.getFrom().getName(), 
						0
						);
			} else if (typeFrom.getId().equals(DoubleFlowType.SINGLETON.getId())) {
				// we receive an Integer value
				param = new DoubleParameter(
						getId(), 
						c.getFrom().getMeta().getName(), 
						"value received by algo "+c.getFrom().getName(), 
						0.0
						);
				
			} else 
				throw new ProgramException("cannot manage a connection from something which is not a numerical value (integer or double)");
			
			
			connection2parametersId.put(c, param.getId());
			parameterId2parameter.put(param.getId(), param);
			
		}
		
		// remove connections
		for (IConnection c : connectionsToRemove) {
			
			String paramToRemoveId = connection2parametersId.get(c);
			
			clearValueForParameter(paramToRemoveId);
			connection2parametersId.remove(paramToRemoveId);
			parameterId2parameter.remove(paramToRemoveId);
		}

		// TODO notify someone
	}
	
	@Override
	public void notifyConnectionAdded(IConnection c) {
		if (c.getTo().getAlgoInstance() == this) {
			_updateParametersForConnections();
		}
	}

	@Override
	public void notifyConnectionRemoved(IConnection c) {
		if (c.getTo().getAlgoInstance() == this) {
			_updateParametersForConnections();
		}
	}

	@Override
	public void notifyAlgoAdded(IAlgoInstance ai) {
		
	}

	@Override
	public void notifyAlgoRemoved(IAlgoInstance ai) {
		if (ai == this) {
			this.workflow.removeListener(this);
		}
	}

	@Override
	public void notifyAlgoChanged(IAlgoInstance ai) {
		
	}
	

	@Override
	public Collection<Parameter<?>> getParameters() {
		// by default, returns the algo's parameters
		// but here, the instance creates its own parameters
		return parameterId2parameter.values();
	}
	
	
	@Override
	public boolean hasParameter(String id) {
		return parameterId2parameter.containsKey(id);
	}
	
	@Override
	public Parameter<?> getParameter(String id) {
		return parameterId2parameter.get(id);
	}


	@Override
	public void setValueForParameter(String name, Object value) {
		
		//if (!hasParameter(name))
		//	throw new WrongParametersException("wrong parameter "+name);
		final Object previousValue = parametersKey2value.get(name); 
		
		if ((previousValue == null) || (!previousValue.equals(value))) {
			parametersKey2value.put(name, value);
			if (workflow != null)
				workflow._notifyAlgoChanged(this);
		}
		
	}
	
}
