package genlab.algog.algos.instance;

import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.instance.IWorkflowContentListener;
import genlab.core.model.instance.WorkflowCheckResult;
import genlab.core.model.meta.IAlgo;
import genlab.core.parameters.Parameter;

/**
 * 
 * Once created, listens for workflow events, so it detects when it is connected.
 * 
 * 
 * @author Samuel Thiriot
 *
 */
public class GeneInstance extends AlgoInstance implements IWorkflowContentListener {

	public GeneInstance(IAlgo algo, IGenlabWorkflowInstance workflow, String id) {
		super(algo, workflow, id);
		
		if (workflow != null)
			workflow.addListener(this);
	}

	public GeneInstance(IAlgo algo, IGenlabWorkflowInstance workflow) {
		super(algo, workflow);

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
	public void checkForRun(WorkflowCheckResult res) {
		super.checkForRun(res);
		
		boolean paramAtDefault = false;
		for (Parameter<?> param : getAlgo().getParameters()) {
			
			if (isParameterAtDefaultValue(param.getId())) {
				res.messages.warnUser("for gene "+name+", the parameter "+param.getName()+" was not tuned.", getClass());
				paramAtDefault = true;
			}
		}
		if (paramAtDefault)
			res.messages.warnUser("the parameters of genes have a strong impact on the efficiency of genetic algorithms. You should use these parameters to restrict the search space as much as possible.", getClass());
		
	}

	/**
	 * Adapt the parameters of this gene based on the input it was connected to.
	 * May be override for smart things, like min, max.
	 * @param input
	 */
	protected void adaptMyselfToTarget(IInputOutputInstance input) {
		setName("gene "+input.getMeta().getName());
		
	}
	
	@Override
	public void notifyConnectionAdded(IConnection c) {
		
		// if the connection comes from me, then adapt my parameters
		// to the element I'm connected to.
		
		if (c.getFrom().getAlgoInstance() != this)
			return;
		
		// adapt my parameters to the target ones
		adaptMyselfToTarget(c.getTo());
		
		
	}

	@Override
	public void notifyConnectionRemoved(IConnection c) {
	}

	@Override
	public void notifyAlgoAdded(IAlgoInstance ai) {
	}

	@Override
	public void notifyAlgoRemoved(IAlgoInstance ai) {
	}

	@Override
	public void notifyAlgoChanged(IAlgoInstance ai) {
	}


}
