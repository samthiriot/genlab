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
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.parameters.IntParameter;
import genlab.core.parameters.StringParameter;
import genlab.population.yang.execs.CreatePopulationFromBNExec;
import genlab.populations.bo.IPopulation;
import genlab.populations.flowtypes.PopulationFlowType;

import java.io.File;

/**
 * This algo sets randomly the value of an attribute
 */
public class SetRandomAttributeUniformAlgo extends BasicAlgo {
	
	public static final InputOutput<IPopulation> INPUT_POPULATION = new InputOutput<IPopulation>(
			PopulationFlowType.SINGLETON, 
			"in_pop", 
			"population", 
			"the population to fill"
			);
	
	public static final IntParameter PARAM_MIN = new IntParameter(
			"param_min", 
			"min", 
			"minimal value",
			0
			);
	
	public static final IntParameter PARAM_MAX = new IntParameter(
			"param_max", 
			"max", 
			"maximal value",
			100
			);
	
	// TODO parameter to choose one parameter
	public static final StringParameter PARAM_ATTRIBUTE_NAME = new StringParameter(
			"param_attribute_name", 
			"attribute", 
			"attribute to update", 
			"an_attribute"
			);
	
	public static final StringParameter PARAM_ENTITY_TYPE = new StringParameter(
			"param_entity_type", 
			"entity", 
			"entity to update", 
			"an entity"
			);
	
	public static final InputOutput<IPopulation> OUTPUT_POPULATION = new InputOutput<IPopulation>(
			PopulationFlowType.SINGLETON, 
			"out_pop", 
			"population", 
			"the population filled"
			);
	
	public SetRandomAttributeUniformAlgo() {
		super(
				"set attribute random integer", 
				"update attribute with random integer", 
				ExistingAlgoCategories.GENERATORS_POPULATIONS, 
				null, 
				null
				);

		
		inputs.add(INPUT_POPULATION);
		
		registerParameter(PARAM_ENTITY_TYPE);
		registerParameter(PARAM_ATTRIBUTE_NAME);
		registerParameter(PARAM_MIN);
		registerParameter(PARAM_MAX);
		
		outputs.add(OUTPUT_POPULATION);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new CreatePopulationFromBNExec(execution, algoInstance);
	}

}
