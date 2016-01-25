package genlab.core.model.meta.basics.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.AlgoContainer;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.basics.flowtypes.BooleanInOut;

public class IfBlockAlgo extends AlgoContainer {

	public static final BooleanInOut INPUT_CONDITION = new BooleanInOut(
			"in_condition", 
			"condition", 
			"if this input receives true, the input elements will be executed; else they will not"
			);
	
	public IfBlockAlgo() {
		super(
				"if", 
				"executes algorithms inside the block only if a condition is verified", 
				ExistingAlgoCategories.LOOPS, 
				null, 
				null
				);
		
		inputs.add(INPUT_CONDITION);
	}
	/*
	@Override
	public IAlgoInstance createInstance(String id,
			IGenlabWorkflowInstance workflow) {
		return new IfBlockInstance(this, workflow, id);
	}


	@Override
	public IAlgoInstance createInstance(IGenlabWorkflowInstance workflow) {
		return new IfBlockInstance(this, workflow);
	}

*/
	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new IfBlockExec(execution, (IAlgoContainerInstance) algoInstance);
	}

}
