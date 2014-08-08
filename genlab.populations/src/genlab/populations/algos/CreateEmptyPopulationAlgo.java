package genlab.populations.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.populations.bo.IPopulation;
import genlab.populations.bo.PopulationDescription;
import genlab.populations.execs.CreateEmptyPopulationExec;
import genlab.populations.flowtypes.PopulationDescriptionFlowType;
import genlab.populations.flowtypes.PopulationFlowType;

public class CreateEmptyPopulationAlgo extends BasicAlgo {

	public static final InputOutput<PopulationDescription> INPUT_POPULATION_DESCRIPTION = new InputOutput<PopulationDescription>(
			PopulationDescriptionFlowType.SINGLETON, 
			"in_pop_desc", 
			"pop desc", 
			"population description"
			);
	
	public static final InputOutput<IPopulation> OUTPUT_POPULATION = new InputOutput<IPopulation>(
			PopulationFlowType.SINGLETON, 
			"out_pop", 
			"population", 
			"the generated population"
			);
	
	
	public CreateEmptyPopulationAlgo() {
		super(
				"empty population", 
				"inits an empty population", 
				ExistingAlgoCategories.GENERATORS_POPULATIONS, 
				null, 
				null
				);

		inputs.add(INPUT_POPULATION_DESCRIPTION);
		
		outputs.add(OUTPUT_POPULATION);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new CreateEmptyPopulationExec(execution, algoInstance);
	}

}
