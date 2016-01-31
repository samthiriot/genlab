package genlab.core.model.instance;

import genlab.core.model.meta.IAlgo;

/**
 * Abstract algo instance which adapts some parameters to the inputs of the algo.
 * 
 * 
 * 
 * @author Samuel Thiriot
 *
 */
@SuppressWarnings("serial")
public abstract class AlgoInstanceWithParametersDependantToConnections 
										extends AlgoInstance 
										implements IWorkflowContentListener, IWorkflowListener {

	public AlgoInstanceWithParametersDependantToConnections(IAlgo algo,
			IGenlabWorkflowInstance workflow, String id) {
		super(algo, workflow, id);
		
		declareLocalParameters();
		
		if (workflow != null)
			workflow.addListener(this);
	}

	public AlgoInstanceWithParametersDependantToConnections(IAlgo algo,
			IGenlabWorkflowInstance workflow) {
		super(algo, workflow);
		
		declareLocalParameters();
		
		if (workflow != null)
			workflow.addListener(this);
	}
	
	
	@Override
	public void _setWorkflowInstance(IGenlabWorkflowInstance workflow) {
		super._setWorkflowInstance(workflow);
		
		if (workflow != null)
			workflow.addListener(this);

	}
	


	@Override
	public void _initializeParamChangeName() {
		super._initializeParamChangeName();
		
		declareLocalParameters();
		
		WorkflowHooks.getWorkflowHooks().declareListener(this);
	}

	/**
	 * Declare (register) here your local parameter. 
	 * Will be called at construction, reading from persisted, etc.
	 */
	protected abstract void declareLocalParameters();
	
	/**
	 * Implement this method to adapt the parameters of your algo to the inputs 
	 * of the algo
	 */
	protected abstract void adaptParametersToInputs();


	@Override
	public void notifyConnectionAdded(IConnection c) {
		adaptParametersToInputs();
	}

	@Override
	public void notifyConnectionRemoved(IConnection c) {
		adaptParametersToInputs();
	}

	@Override
	public void notifyAlgoAdded(IAlgoInstance ai) {
		
	}

	@Override
	public void notifyAlgoRemoved(IAlgoInstance ai) {
		if (ai == this)
			workflow.removeListener(this);
	}

	@Override
	public void notifyAlgoChanged(IAlgoInstance ai) {
		
	}

	@Override
	public void workflowCreation(IGenlabWorkflowInstance workflow) {
		
	}

	@Override
	public void workflowChanged(IGenlabWorkflowInstance workflow) {
		
	}

	@Override
	public void workflowOpened(IGenlabWorkflowInstance workflow) {
		
	}

	@Override
	public void workflowSaving(IGenlabWorkflowInstance workflow) {
		
	}

	@Override
	public void workflowSaved(IGenlabWorkflowInstance workflow) {
		
	}


	@Override
	public void workflowAutomaticallyCreatedAndFinished(IGenlabWorkflowInstance instance) {
		if (instance != this.getWorkflow())
			return;
		adaptParametersToInputs();
		WorkflowHooks.getWorkflowHooks().removeListener(this);
	}

	@Override
	public void workflowLoaded(IGenlabWorkflowInstance instance) {
		if (instance != this.getWorkflow())
			return;
		adaptParametersToInputs();
		WorkflowHooks.getWorkflowHooks().removeListener(this);
	}

}
