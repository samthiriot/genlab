package genlab.algog.algos.exec;

import genlab.algog.algos.meta.GoalAlgo;
import genlab.core.commons.ProgramException;
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
	private Number target = null;
	private Number value = null;
	
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
		
		target = (Number)getInputValueForInput(GoalAlgo.INPUT_TARGET);
		value = (Number)getInputValueForInput(GoalAlgo.INPUT_VALUE);
		
		if( Double.isNaN(value.doubleValue()) ) {
			resultDiffAbs = NSGA2Exec.INF;
		}else {		
			int rounding = (Integer)algoInst.getValueForParameter(GoalAlgo.PARM_ROUNDING);
			int factor = (int)Math.pow(10, rounding);
			
			resultDiffAbs = 
					(double)Math.round(
							Math.abs(target.doubleValue()-value.doubleValue())*factor
					)/factor;
		}		
		
		// end of computation
		setResult(new ComputationResult(algoInst, progress, messages));
		progress.setComputationState(ComputationState.FINISHED_OK);
	}

	@Override
	public void cancel() {
		progress.setComputationState(ComputationState.FINISHED_CANCEL);
	}

	@Override
	public void kill() {
		progress.setComputationState(ComputationState.FINISHED_CANCEL);
	}

	@Override
	public Double getFitness() {

		if (!progress.getComputationState().isFinished())
			throw new ProgramException("trying to read the fitness from a goal before its completion");
		
		return resultDiffAbs;
	}

	@Override
	public Object getTarget() {
		return target;
	}

	@Override
	public Object getActualValue() {
		return value;
	}


}
