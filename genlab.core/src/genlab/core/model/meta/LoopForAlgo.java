package genlab.core.model.meta;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractContainerExecution;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoContainerInstance;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.parameters.IntParameter;
import genlab.core.usermachineinteraction.GLLogger;

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
		
		final int totalIterations = (Integer)algoInstance.getValueForParameter(PARAM_ITERATIONS);
		
		return new AbstractContainerExecution(execution, (IAlgoContainerInstance)algoInstance) {

			protected int total = totalIterations;
			protected int i = 0;
			
			@Override
			protected void initFirstRun() {
				i = 0;
				progress.setProgressTotal(total);
			}

			@Override
			protected boolean shouldContinueRun() {
				return i < total;
			}

			@Override
			protected void endOfRun() {
				i++;
				progress.incProgressMade();
			}

			@Override
			protected void startOfIteration() {
				messages.debugTech("start iteration "+i, getClass());
			}
		

		};
	}

	

}
