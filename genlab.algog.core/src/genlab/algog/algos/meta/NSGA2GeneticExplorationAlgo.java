package genlab.algog.algos.meta;

import genlab.algog.algos.exec.NSGA2Exec;
import genlab.algog.algos.flowtypes.GeneticTableFlowType;
import genlab.algog.algos.instance.GeneticExplorationAlgoContainerInstance;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IGenlabTable;
import genlab.core.parameters.ListParameter;

public class NSGA2GeneticExplorationAlgo extends AbstractGeneticExplorationAlgo {

	public static final InputOutput<IGenlabTable> OUTPUT_TABLE_PARETO = new InputOutput<IGenlabTable>(
			GeneticTableFlowType.SINGLETON, 
			"out_table_paretofronts", 
			"pareto fronts", 
			"a table containing all the pareto fronts for each iteration"
			);

	public static final ListParameter PARAM_CROSSOVER = new ListParameter(
			"param_crossover", 
			"crossover", 
			"method for crossover",
			0,
			ECrossoverMethod.getLabelsAsList()
			);
	
	static {
		OUTPUT_TABLE_PARETO.setIsContinuousOutput(true);
	}
	
	
	
	public NSGA2GeneticExplorationAlgo() {
		super(
				"NSGA2", 
				"A Fast and Elitist Multiobjective Genetic Algorithm"
				);
		
		outputs.add(OUTPUT_TABLE_PARETO);
		
		registerParameter(PARAM_CROSSOVER);
		
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new NSGA2Exec(
				execution, 
				(GeneticExplorationAlgoContainerInstance)algoInstance
				);
	}

	
}
