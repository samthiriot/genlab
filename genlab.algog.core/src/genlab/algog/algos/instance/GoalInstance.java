package genlab.algog.algos.instance;

import genlab.algog.algos.meta.GoalAlgo;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IInputOutputInstance;
import genlab.core.model.instance.IWorkflowContentListener;
import genlab.core.model.meta.IAlgo;

/**
 * 
 * Once created, listens for workflow events, so it detects when it is connected.
 * 
 * 
 * @author Samuel Thiriot
 *
 */
public class GoalInstance extends AlgoInstance implements IWorkflowContentListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8903247183168984087L;

	public GoalInstance(IAlgo algo, IGenlabWorkflowInstance workflow, String id) {
		super(algo, workflow, id);
		
		if (workflow != null)
			workflow.addListener(this);
	}

	public GoalInstance(IAlgo algo, IGenlabWorkflowInstance workflow) {
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

	/**
	 * Adapt the parameters of this gene based on the input it was connected to.
	 * May be override for smart things, like min, max.
	 * @param input
	 */
	protected void adaptMyselfToTarget(IInputOutputInstance output) {
		
		// adapt the name to the target
		setName("goal "+output.getMeta().getName());
		
	}
	
	@Override
	public void notifyConnectionAdded(IConnection c) {
		
		// if the connection comes from me, then adapt my parameters
		// to the element I'm connected to.
		
		if (c.getTo().getAlgoInstance() != this)
			return;
		if (c.getTo().getMeta() != GoalAlgo.INPUT_VALUE)
			return;
		
		// adapt my parameters to the target ones
		adaptMyselfToTarget(c.getFrom());
		
		
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
