package genlab.algog.algos.exec;

import genlab.core.model.exec.IAlgoExecution;

public interface IGoalExec extends IAlgoExecution {

	/**
	 * Returns how far we are from the target, the lower the better.
	 * @return
	 */
	public Double getFitness();
	
	
}
