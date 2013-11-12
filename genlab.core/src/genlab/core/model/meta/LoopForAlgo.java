package genlab.core.model.meta;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.LoopAlgoExecutionSupervisor;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.parameters.IntParameter;

public class LoopForAlgo extends AlgoContainer {

	public static final IntParameter PARAM_ITERATIONS = new IntParameter("iterations", "iterations", "number of iterations", new Integer("5"));
	
	public LoopForAlgo() {
		super(
				"for loop", 
				"iterates N times", 
				null, 
				ExistingAlgoCategories.LOOPS.getTotalId(), 
				null
				);
		
		registerParameter(PARAM_ITERATIONS);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new LoopAlgoExecutionSupervisor(
				execution, 
				(IAlgoContainerInstance) algoInstance
				);
		
	}



}
