package genlab.algog.algos.exec;

import genlab.algog.algos.meta.GoalAlgo;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecutionOneshot;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IInputOutputInstance;

public class GoalExec extends AbstractAlgoExecutionOneshot implements IGoalExec {

	private Double resultDiffAbs = null;
	
	public GoalExec(IExecution exec, IAlgoInstance algoInst, IComputationProgress progress) {
		super(exec, algoInst, progress);

	}

	@Override
	public long getTimeout() {
		return 1000;
	}

	@Override
	public void run() {

		progress.setComputationState(ComputationState.STARTED);
		
		Number target = (Number)getInputValueForInput(GoalAlgo.INPUT_TARGET);
		Number value = (Number)getInputValueForInput(GoalAlgo.INPUT_VALUE);
		
		resultDiffAbs = Math.abs(target.doubleValue()-value.doubleValue());
		
		// end of computation
		setResult(new ComputationResult(algoInst, progress, messages));
		progress.setComputationState(ComputationState.FINISHED_OK);
		
		
	}

	@Override
	public void cancel() {

	}

	@Override
	public void kill() {

	}

	@Override
	public Double getFitness() {
		return resultDiffAbs;
	}


}
