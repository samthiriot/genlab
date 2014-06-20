package genlab.algog.algos.meta;

import genlab.algog.algos.exec.GeneticExplorationMonoObjectiveAlgoExec;
import genlab.algog.algos.instance.GeneticExplorationAlgoContainerInstance;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;

public class GeneticExplorationAlgo extends AbstractGeneticExplorationAlgo {
	
	/**
	 * This metadata of the table will contain a map associating each goal to its target, value, fitness
	 */
	public static final String TABLE_METADATA_KEY_GENES2VALUES = "algog.genes2cols";

	public static final String TABLE_METADATA_KEY_GOALS2COLS = "algog.goals2cols";
	
	public static final String TABLE_METADATA_KEY_COLTITLE_ITERATION = "algog.columnContainingIterations";

	public static final String TABLE_METADATA_KEY_MAX_ITERATIONS = "algog.maxIterations";

	public static final String TABLE_COLUMN_METADATA_KEY_ROLE = "algog.role";
	public static final String TABLE_COLUMN_METADATA_VALUE_TARGET = "algog.target";
	public static final String TABLE_COLUMN_METADATA_VALUE_VALUE = "algog.value";
	public static final String TABLE_COLUMN_METADATA_VALUE_FITNESS = "algog.fitness";
	
	public GeneticExplorationAlgo() {
		super(
				"genetic exploration", 
				"evolutionary algorithm based of genetic exploration"
				);
		
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new GeneticExplorationMonoObjectiveAlgoExec(
				execution, 
				(GeneticExplorationAlgoContainerInstance)algoInstance
				);
	}

	
}
