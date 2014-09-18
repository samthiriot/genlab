package genlab.populations.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;
import genlab.core.model.meta.basics.flowtypes.StringFlowType;
import genlab.populations.bo.IPopulation;
import genlab.populations.execs.AddEmptyAgentsExec;
import genlab.populations.execs.CreateEmptyPopulationExec;
import genlab.populations.flowtypes.PopulationFlowType;

/**
 * This algo adds N agents of a given type, with no defined attribute value
 * 
 * @author Samuel Thiriot
 *
 */
public class AddEmptyAgentsAlgo extends BasicAlgo {


	public static final InputOutput<IPopulation> INPUT_POPULATION = new InputOutput<IPopulation>(
			PopulationFlowType.SINGLETON, 
			"in_pop", 
			"population", 
			"the population to fill"
			);
	
	public static final InputOutput<Integer> INPUT_COUNT = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON, 
			"in_count", 
			"count", 
			"count of individuals to generate from this Bayesian network",
			200
			);
	
	public static final InputOutput<String> INPUT_TYPENAME = new InputOutput<String>(
			StringFlowType.SINGLETON, 
			"in_agenttype", 
			"agent type", 
			"type of agents to create"
			);
	
	public static final InputOutput<IPopulation> OUTPUT_POPULATION = new InputOutput<IPopulation>(
			PopulationFlowType.SINGLETON, 
			"out_pop", 
			"population", 
			"the population filled"
			);

	
	public AddEmptyAgentsAlgo() {
		super(
				"Create empty agents", 
				"adds empty agents of a type in the population", 
				ExistingAlgoCategories.GENERATORS_POPULATIONS, 
				null, 
				null
				);
		
		inputs.add(INPUT_POPULATION);
		inputs.add(INPUT_COUNT);
		inputs.add(INPUT_TYPENAME);
		outputs.add(OUTPUT_POPULATION);
	}
	

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new AddEmptyAgentsExec(execution, algoInstance);
	}

}
