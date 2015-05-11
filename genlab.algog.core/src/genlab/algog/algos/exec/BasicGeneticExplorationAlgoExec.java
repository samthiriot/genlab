package genlab.algog.algos.exec;

import genlab.algog.algos.instance.GeneticExplorationAlgoContainerInstance;
import genlab.algog.internal.AGene;
import genlab.algog.internal.AGenome;
import genlab.algog.internal.ANumericGene;
import genlab.algog.internal.AnIndividual;
import genlab.core.exec.IExecution;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BasicGeneticExplorationAlgoExec extends
		AbstractGeneticExplorationAlgoExec {

	public BasicGeneticExplorationAlgoExec(IExecution exec,
			GeneticExplorationAlgoContainerInstance algoInst) {
		super(exec, algoInst);
	}
	



	/**
	 * Step "construct population" of the genetic algo. Called only for the very first one.
	 * @return
	 */
	protected Map<AGenome,Set<AnIndividual>> generateInitialPopulation() {
	

		// associates each geneome to its corresponding population (for one generation)
		Map<AGenome,Set<AnIndividual>> genome2population = new HashMap<AGenome, Set<AnIndividual>>();

		// generate the novel population for each specy
		// for each specy
		messages.debugUser("generation of the population for generation "+iterationsMade, getClass());
		for (AGenome genome : genome2algoInstance.keySet()) {
	
			messages.debugUser("generation of the sub population based on genome: "+genome, getClass());

			
			Set<AnIndividual> population = generateInitialPopulation(genome, paramPopulationSize/genome2algoInstance.size());
			
			parentGeneration.put(iterationsMade, population);
			
			genome2population.put(genome, population);

			
		}
				
		return genome2population;
		
	}
	
	
	

	@Override
	protected boolean hasConverged() {
		// in the 
		return false;
	}

}
