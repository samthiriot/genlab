package genlab.algog.algos.meta;

import genlab.algog.types.GeneFlowType;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.AnythingFlowType;

public class IndividualsAlgo extends AbstractGeneticAlgo {

	public static final InputOutput<Object> INPUT_POPULATION = new InputOutput<Object>(
			AnythingFlowType.SINGLETON, 
			"in_population", 
			"population",
			"individuals to add to the population to be evaluated"
			);
	
	public static final InputOutput<Object> OUTPUT_ANYTHING = new InputOutput<Object>(
			GeneFlowType.SINGLETON, 
			"out_individuals", 
			"individuals",
			"individuals to evaluate"
			);
	
	public IndividualsAlgo(String name, String description) {
		super(
				"population", 
				"aggregates the population to be evaluated"
				);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		// TODO Auto-generated method stub
		return null;
	}

}
