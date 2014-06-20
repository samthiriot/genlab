package genlab.algog.algos.meta;

import genlab.algog.algos.exec.NSGA2Exec;
import genlab.algog.algos.flowtypes.GeneticTableFlowType;
import genlab.algog.algos.instance.GeneticExplorationAlgoContainerInstance;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IGenlabTable;

public class NSGA2GeneticExplorationAlgo extends AbstractGeneticExplorationAlgo {

	public static final InputOutput<IGenlabTable> OUTPUT_TABLE_PARETO = new InputOutput<IGenlabTable>(
			GeneticTableFlowType.SINGLETON, 
			"out_table_paretofronts", 
			"pareto fronts", 
			"a table containing all the pareto fronts for each iteration"
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
