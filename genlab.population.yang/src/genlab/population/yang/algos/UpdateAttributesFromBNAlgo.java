package genlab.population.yang.algos;

import genlab.bayesianinference.IBayesianNetwork;
import genlab.bayesianinference.meta.BayesianNetworkFlowType;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.FileFlowType;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;
import genlab.population.yang.execs.CreatePopulationFromBNExec;
import genlab.populations.bo.IPopulation;
import genlab.populations.flowtypes.PopulationFlowType;

import java.io.File;

// TODO !!!
public class UpdateAttributesFromBNAlgo extends BasicAlgo {
	
	public static final InputOutput<IPopulation> INPUT_POPULATION = new InputOutput<IPopulation>(
			PopulationFlowType.SINGLETON, 
			"in_pop", 
			"population", 
			"the population to fill"
			);
	
	public static final InputOutput<IBayesianNetwork> INPUT_BAYESIAN_NETWORK = new InputOutput<IBayesianNetwork>(
			BayesianNetworkFlowType.SINGLETON, 
			"in_bn", 
			"Bayesian network", 
			"the Bayesian network describing the probabilities for each attribute value"
			);
	
	public static final InputOutput<IPopulation> OUTPUT_POPULATION = new InputOutput<IPopulation>(
			PopulationFlowType.SINGLETON, 
			"out_pop", 
			"population", 
			"the population filled"
			);
	
	public UpdateAttributesFromBNAlgo() {
		super(
				"create individuals from BN", 
				"creates individuals from a Bayesian network", 
				ExistingAlgoCategories.GENERATORS_POPULATIONS, 
				null, 
				null
				);

		
		inputs.add(INPUT_POPULATION);
		inputs.add(INPUT_BAYESIAN_NETWORK);
		
		outputs.add(OUTPUT_POPULATION);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new CreatePopulationFromBNExec(execution, algoInstance);
	}

}
