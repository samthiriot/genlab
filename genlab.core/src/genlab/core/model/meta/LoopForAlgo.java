package genlab.core.model.meta;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractContainerExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoContainerInstance;

public class LoopForAlgo extends AlgoContainer {

	public LoopForAlgo() {
		super(
				"for loop", 
				"iterates N times", 
				null, 
				ExistingAlgoCategories.LOOPS.getTotalId(), 
				null
				);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new AbstractContainerExecution(execution, (IAlgoContainerInstance)algoInstance);
	}

	

}
