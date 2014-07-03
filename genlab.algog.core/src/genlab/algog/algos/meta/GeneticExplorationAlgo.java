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
	/*
	 * Metadata for genes
	 * tablemetadata = {
	 * 					TABLE_METADATA_KEY_GENES2METADATA => {
	 * 														"ahaha " => {
	 * 																	TABLE_COLUMN_GENE_METADATA_KEY_VALUE => "column 3 of the table"
	 * 																	TABLE_COLUMN_GENE_METADATA_KEY_MIN => 12.3,
	 * 																	TABLE_COLUMN_GENE_METADATA_KEY_MAX => 12.7 
	 * 																	},
	 * 														"bb" => { 
	 * 																	TABLE_COLUMN_GENE_METADATA_KEY_VALUE => "column 4 of the table"
	 * 																	}
	 * 														} 
	 * 					}
	 */
	public static final String TABLE_METADATA_KEY_GENES2METADATA = "algog.genes2metadata";
	
	public static final String TABLE_METADATA_KEY_GOALS2COLS = "algog.goals2cols";
	
	public static final String TABLE_METADATA_KEY_COLTITLE_ITERATION = "algog.columnContainingIterations";

	public static final String TABLE_METADATA_KEY_MAX_ITERATIONS = "algog.maxIterations";

	public static final String TABLE_COLUMN_GOAL_METADATA_KEY_ROLE = "algog.goal.role";
	public static final String TABLE_COLUMN_GOAL_METADATA_VALUE_TARGET = "algog.goal.target";
	public static final String TABLE_COLUMN_GOAL_METADATA_VALUE_VALUE = "algog.goal.value";
	public static final String TABLE_COLUMN_GOAL_METADATA_VALUE_FITNESS = "algog.goal.fitness";
	
	public static final String TABLE_COLUMN_GENE_METADATA_KEY_VALUE = "algog.gene.coltoread";
	public static final String TABLE_COLUMN_GENE_METADATA_KEY_MIN = "algog.gene.minvalue";
	public static final String TABLE_COLUMN_GENE_METADATA_KEY_MAX = "algog.gene.maxvalue";
	
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
