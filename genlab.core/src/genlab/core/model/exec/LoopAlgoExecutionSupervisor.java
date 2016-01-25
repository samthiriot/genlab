package genlab.core.model.exec;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.meta.LoopForAlgo;

public class LoopAlgoExecutionSupervisor 
					extends AbstractContainerExecutionSupervisor {

	final Object lockIterations = new Object();
	final int iterationsTotal;
	int iterationsDone = 0;
			
	public LoopAlgoExecutionSupervisor(IExecution exec,
			IAlgoContainerInstance algoInst) {
		super(exec, algoInst);
		
		
		if (!(algoInst.getAlgo() instanceof LoopForAlgo))
			throw new ProgramException("this execution should only be used for instances of loop algo");
		
		iterationsTotal = (Integer)algoInst.getValueForParameter(LoopForAlgo.PARAM_ITERATIONS.getId());
		autoFinishWhenChildrenFinished = true;
		autoUpdateProgressFromChildren = true;
		
	}

	@Override
	protected void initFirstRun() {

	}

	@Override
	protected void startOfIteration() {
		synchronized (lockIterations) {
			iterationsDone++;	
		}
	}

	@Override
	protected boolean shouldContinueRun() {
		synchronized (lockIterations) {
			return (iterationsDone < iterationsTotal);
		}
	}
	
	@Override
	protected int evaluateRemainingSteps() {
		synchronized (lockIterations) {
			return iterationsTotal-iterationsDone;
		}
	}

	@Override
	protected void endOfRun() {
		
	}

	@Override
	protected String getSuffixForCurrentIteration() {
		return " "+iterationsDone+"/"+iterationsTotal;
	}


}
