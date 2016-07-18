package genlab.core.model.meta;

import java.util.Map;
import java.util.Set;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.ExplorationExecutionSupervisor;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.instance.IAlgoContainerInstance;
import genlab.core.parameters.IntParameter;
import genlab.core.parameters.StringParameter;

public class ExplorationAlgo extends AlgoContainer {

	public static final StringParameter PARAM_EXPLORATION = new StringParameter(
			"p_explo", 
			"exploration", 
			"schema to explore", 
			""
			);
	
	public static final IntParameter PARAM_REPETITION = new IntParameter("p_repeat", "repetitions", "count of executions", 1, 1);

	public ExplorationAlgo() {
		super(
				"exploration", 
				"explore a space of parameters", 
				ExistingAlgoCategories.EXPLORATION,
				null,
				null
				);

		registerParameter(PARAM_EXPLORATION);
		registerParameter(PARAM_REPETITION);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution, AlgoInstance algoInstance) {
		return new ExplorationExecutionSupervisor(execution, (IAlgoContainerInstance)algoInstance);
	}

	@Override
	public boolean canContain(IAlgo algo) {
		return true;
	}

	@Override
	public Map<IAlgo, Integer> recommandAlgosContained() {
		// TODO Auto-generated method stub
		return super.recommandAlgosContained();
	}

	
	
}
