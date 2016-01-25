package genlab.core.model.meta.basics.algos;

import genlab.core.model.instance.AlgoContainerInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.WorkflowCheckResult;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IAlgoContainer;

public class IfBlockInstance extends AlgoContainerInstance {

	/**
	 * 
	 */
	private static final long serialVersionUID = -19117252674089086L;

	public IfBlockInstance(IAlgoContainer algo, IGenlabWorkflowInstance workflow) {
		super(algo, workflow);
	}

	public IfBlockInstance(IAlgo algo, IGenlabWorkflowInstance workflow,
			String id) {
		super(algo, workflow, id);
	}
	

	@Override
	public void checkForRun(WorkflowCheckResult res) {
		
		super.checkForRun(res);
		
		/* TODO restore
		// ensure that nothing gets out of this if block
		if (getConnectionsGoingToOutside().size() > 0) {
			res.messages.errorUser("no connection can get out of if blocks; please put the corresponding algorithms inside the if block instead", getClass());
		}
		*/
				
	}

}
