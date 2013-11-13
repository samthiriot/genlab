package genlab.algog.algos.instance;

import genlab.algog.algos.meta.GenesAlgo;
import genlab.algog.algos.meta.MeanSquaredErrorAlgo;
import genlab.algog.algos.meta.ReceiveFitnessAlgo;
import genlab.core.commons.ProgramException;
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

	private Map<IConnection,Parameter<?>> connection2parameters = new HashMap<IConnection, Parameter<?>>();

	public MeanSquaredErrorAlgoInstance(IAlgo algo,
			IGenlabWorkflowInstance workflow, String id) {
		super(algo, workflow, id);
		// TODO Auto-generated constructor stub
	}

	public MeanSquaredErrorAlgoInstance(
			MeanSquaredErrorAlgo algo,
			IGenlabWorkflowInstance workflow) {
		super(algo, workflow);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void _setWorkflowInstance(IGenlabWorkflowInstance workflow) {
		IGenlabWorkflowInstance previous = this.workflow;
		
		super._setWorkflowInstance(workflow);
		
		if (previous != null)
			previous.removeListener(this);
		
		this.workflow.addListener(this);
		
	}

	protected void updateParametersForConnections() {
		
		IInputOutputInstance inInstance = getInputInstanceForInput(MeanSquaredErrorAlgo.INTPUT_VALUES);
		

		
		Set<IConnection> connectionsToCreate = new HashSet<IConnection>(inInstance.getConnections());
		connectionsToCreate.removeAll(connection2parameters.keySet());
		
		Set<IConnection> connectionsToRemove = new HashSet<IConnection>(connection2parameters.keySet());
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
			
			connection2parameters.put(c, param);
			
			
		}
		
		// remove connections
		for (IConnection c : connectionsToRemove) {
			
			Parameter<?> paramToRemove = connection2parameters.get(c);
			
			clearValueForParameter(paramToRemove.getId());
			connection2parameters.remove(paramToRemove);
			
		}

		// TODO notify someone
	}
	
	@Override
	public void notifyConnectionAdded(IConnection c) {
		if (c.getTo().getAlgoInstance() == this) {
			updateParametersForConnections();
		}
	}

	@Override
	public void notifyConnectionRemoved(IConnection c) {
		if (c.getTo().getAlgoInstance() == this) {
			updateParametersForConnections();
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
		return connection2parameters.values();
	}
	
	
	@Override
	public boolean hasParameter(String id) {
		for (Parameter<?> p: connection2parameters.values()) {
			if (p.getId().equals(id))
				return true;
		}
		return false;
	}
	
	@Override
	public Parameter<?> getParameter(String id) {
		for (Parameter<?> p: connection2parameters.values()) {
			if (p.getId().equals(id))
				return p;
		}
		return null;
	}


	
	
}
