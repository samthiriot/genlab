package genlab.algog.algos.meta;

import genlab.algog.algos.exec.GoalExec;
import genlab.algog.algos.exec.IGoalExec;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.NumberFlowType;

/**
 * TODO check the algorithm with the principle: the goal should not come from a genetic algo, from a gene, in 
 * brief: it should be a constant !
 * 
 * 
 * @author Samuel Thiriot
 *
 */
public class GoalAlgo extends AbstractGeneticAlgo  {


	public static final InputOutput<Number> INPUT_TARGET = new InputOutput<Number>(
			NumberFlowType.SINGLETON, 
			"in_target_value", 
			"target",
			"the target to be reached"
			);
	

	public static final InputOutput<Number> INPUT_VALUE = new InputOutput<Number>(
			NumberFlowType.SINGLETON, 
			"in_value", 
			"value",
			"the value actually observed"
			);
	
	public static final String NAME = "goal";

	public GoalAlgo() {
		super(NAME, "a goal to be targeted by the genetic algorithm");

		inputs.add(INPUT_TARGET);
		inputs.add(INPUT_VALUE);
		
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new GoalExec(
				execution, 
				algoInstance, 
				new ComputationProgressWithSteps()
				);
		
	}

}
