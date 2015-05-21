package genlab.algog.algos.meta;

import genlab.algog.algos.exec.GoalExec;
import genlab.algog.algos.instance.GoalInstance;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.IAlgoContainer;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.NumberFlowType;
import genlab.core.parameters.IntParameter;

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
			"the target to be reached",
			10
			);
	

	public static final InputOutput<Number> INPUT_VALUE = new InputOutput<Number>(
			NumberFlowType.SINGLETON, 
			"in_value", 
			"value",
			"the value actually observed"
			);
	
	public static final IntParameter PARM_ROUNDING = new IntParameter(
			"param_rounding", 
			"rounding decimals for the fitness", 
			"number of decimals to round (if negative, rounding of the int value)", 
			10
			);
	
	public static final String NAME = "goal";

	public GoalAlgo() {
		super(NAME, "a goal to be targeted by the genetic algorithm");

		inputs.add(INPUT_TARGET);
		inputs.add(INPUT_VALUE);
	
		// TODO can we have negative rounding ? 
		// TODO is it a good idea ?
		PARM_ROUNDING.setMinValue(0);
		PARM_ROUNDING.setMaxValue(10);
		
		registerParameter(PARM_ROUNDING);
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
	
	@Override
	public boolean canBeContainedInto(IAlgoContainer algoContainer) {
		// genes can only be contained into genetic exploration algos
		return (algoContainer instanceof AbstractGeneticExplorationAlgo);
	}

	@Override
	public IAlgoInstance createInstance(IGenlabWorkflowInstance workflow) {
		return new GoalInstance(this, workflow);
	}

	@Override
	public IAlgoInstance createInstance(String id,
			IGenlabWorkflowInstance workflow) {
		return new GoalInstance(this, workflow, id);
	}


}
