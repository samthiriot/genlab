package genlab.core.model.meta.basics.algos;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractContainerExecutionSupervisor;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.model.instance.IInputOutputInstance;

public class IfBlockExec 
					extends AbstractContainerExecutionSupervisor {

	
	final Object lockIterations = new Object();

	/**
	 * Should this block run or not ? Null as long as unknown, true if it should run (if true), false else (sic)
	 */
	protected Boolean shouldRun = null;
	
	/**
	 * Did this block ran already ?
	 */
	protected boolean hasRan = false;
	
	
	public IfBlockExec(IExecution exec, IAlgoContainerInstance algoInst) {
		super(exec, algoInst);
	
		if (!(algoInst.getAlgo() instanceof IfBlockAlgo))
			throw new ProgramException("this execution should only be used for instances of if block");
		
		autoFinishWhenChildrenFinished = true;
		autoUpdateProgressFromChildren = true;
	}

	@Override
	public void run() {

		// let's check whether we should start tasks or not
		shouldRun = (Boolean) getInputValueForInput(IfBlockAlgo.INPUT_CONDITION);

		messages.infoTech("Starting If", getClass());
		
		super.run();
		
		if (!shouldRun) {
			// easy: don't have to run !
			progress.setComputationState(ComputationState.FINISHED_OK);
			return;
		} 
		
		messages.infoTech("Started If", getClass());
		
	}

	
	
	@Override
	public void notifyInputAvailable(IInputOutputInstance to) {
		// TODO Auto-generated method stub
		super.notifyInputAvailable(to);
	}
	
	

	@Override
	protected void initFirstRun() {
		
	}

	@Override
	protected void startOfIteration() {
		synchronized (lockIterations) {
			hasRan = true;
		}
		
	}

	@Override
	protected boolean shouldContinueRun() {
		synchronized (lockIterations) {
			return shouldRun && !hasRan;
		}
	}


	@Override
	protected int evaluateRemainingSteps() {
		synchronized (lockIterations) {
			if (shouldRun && !hasRan)
				return 1;
			else 
				return 0;
		}
	}
	
	@Override
	protected void endOfRun() {
		
	}

	@Override
	protected String getSuffixForCurrentIteration() {
		return " 1/1";
	}

}
