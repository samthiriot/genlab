package genlab.algog.algos.meta;

import genlab.algog.algos.flowtypes.GeneticTableFlowType;
import genlab.algog.algos.instance.GeneticExplorationAlgoContainerInstance;
import genlab.algog.core.Activator;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.AlgoContainer;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.ExistingAlgos;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IGenlabTable;
import genlab.core.parameters.IntParameter;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;

public abstract class AbstractGeneticExplorationAlgo extends AlgoContainer {


	public static final InputOutput<IGenlabTable> OUTPUT_TABLE = new InputOutput<IGenlabTable>(
			GeneticTableFlowType.SINGLETON, 
			"out_table", 
			"complete results", 
			"a table containing the detailed results of computations"
			);
	
	public static final IntParameter PARAM_STOP_MAXITERATIONS = new IntParameter(
			"param_stop_maxiterations", 
			"stop after iterations", 
			"if this number of iterations is reached, the exploration will be stopped;", 
			new Integer(50)
			);
	
	
	public static final IntParameter PARAM_SIZE_POPULATION = new IntParameter(
			"param_population_size", 
			"total population size", 
			"the size of the total population (all species cumulated)", 
			new Integer(100)
			);
	
	static {

		OUTPUT_TABLE.setIsContinuousOutput(true);

		PARAM_STOP_MAXITERATIONS.setMinValue(5);
		PARAM_SIZE_POPULATION.setMinValue(10);

	}
	
	
	public AbstractGeneticExplorationAlgo(String name, String desc) {
		super(
				name, 
				desc,
				ExistingAlgoCategories.EXPLORATION_GENETIC_ALGOS, 
				"/icons/dna"+IMAGE_PATH_PLACEHOLDER_SIZE+".png",
				"/icons/dnaBig.png"
				);
		
		outputs.add(OUTPUT_TABLE);
		
		registerParameter(PARAM_STOP_MAXITERATIONS);
		registerParameter(PARAM_SIZE_POPULATION);
	}


	@Override
	public boolean canBeContainedInto(IAlgoInstance algoInstance) {
		// only permit genetic algos to be stored directly into workflows
		return (algoInstance instanceof IGenlabWorkflowInstance);
	}

	@Override
	public boolean canContain(IAlgo algo) {
		// TODO limit ? avoid loops and reduce algos ?
		return true; 
	}
	
	@Override
	public Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}

	@Override
	public IAlgoInstance createInstance(String id, IGenlabWorkflowInstance workflow) {
		return new GeneticExplorationAlgoContainerInstance(this, workflow, id); 
	}


	@Override
	public final IAlgoInstance createInstance(IGenlabWorkflowInstance workflow) {
		return new GeneticExplorationAlgoContainerInstance(this, workflow);
	}

	@Override
	public Map<IAlgo, Integer> recommandAlgosContained() {
		
		Map<IAlgo, Integer> res = new HashMap<IAlgo, Integer>(20);
		
		
		// -- genes
		
		// boolean gene
		res.put(
				ExistingAlgos.getExistingAlgos().getAlgoForClass(BooleanGeneAlgo.class.getCanonicalName()),
				100
				);
		// integer gene (prefer integer to double, because double is always possible while integer is not)
		res.put(
				ExistingAlgos.getExistingAlgos().getAlgoForClass(IntegerGeneAlgo.class.getCanonicalName()),
				102
				);
		res.put(
				ExistingAlgos.getExistingAlgos().getAlgoForClass(DoubleGeneAlgo.class.getCanonicalName()),
				101
				);
		
		// -- genome
		res.put(
				ExistingAlgos.getExistingAlgos().getAlgoForClass(GenomeAlgo.class.getCanonicalName()),
				100
				);
		
		
		return res;
	}

}
