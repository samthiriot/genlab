package genlab.algog.algos.exec;

import genlab.algog.algos.instance.GeneticExplorationAlgoContainerInstance;
import genlab.algog.internal.AGene;
import genlab.algog.internal.AGenome;
import genlab.algog.internal.ANumericGene;
import genlab.algog.internal.AnIndividual;
import genlab.core.exec.IExecution;

import java.util.Arrays;
import java.util.Collections;
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
		// TODO Auto-generated constructor stub
	}

	protected final Object[][] crossoverOnePoint(final AGenome genome, Object[] indiv1, Object[] indiv2) {
		
		Object[][] res = new Object[2][];
		 
		final int minSlice = 1;
		final int maxSlice = genome.getGenes().length-1;
		
		int genIdx = uniform.nextIntFromTo(minSlice, maxSlice);

		// indiv 1
		Object[] novelIndiv = new Object[indiv1.length];
		for (int i=0; i<genIdx; i++) {
			novelIndiv[i]=indiv1[i];
		}
		for (int i=genIdx; i<indiv1.length; i++) {
			novelIndiv[i]=indiv2[i];
		}
		res[0] = novelIndiv;
		
		// indiv 2
		novelIndiv = new Object[indiv1.length];
		for (int i=0; i<genIdx; i++) {
			novelIndiv[i]=indiv2[i];
		}
		for (int i=genIdx; i<indiv1.length; i++) {
			novelIndiv[i]=indiv1[i];
		}
		res[1] = novelIndiv;

		 
		return res;
	}
	
	
	protected final Object[][] crossoverArithmetic(final AGenome genome, Object[] indiv1, Object[] indiv2) {
		
		
		Object[][] res = new Object[2][];

		
		
		Object[] novelIndiv1 = new Object[indiv1.length];
		Object[] novelIndiv2 = new Object[indiv1.length];
		
		for (int i=0; i<indiv1.length; i++) {
		
			final double randomWeightFactor = uniform.nextDoubleFromTo(0, 1.0);
			ANumericGene<?> currentGene = (ANumericGene<?>)genome.getGenes()[i];
			
			novelIndiv1[i] = currentGene.crossoverArithmetic(indiv1[i], indiv2[i], randomWeightFactor);
			novelIndiv2[i] = currentGene.crossoverArithmetic(indiv2[i], indiv1[i], randomWeightFactor);

		}
		
		res[0] = novelIndiv1;
		res[1] = novelIndiv2;
						
		return res;
		
	}

	/**
	 * Step "construct population" of the genetic algo. Called before each 
	 * @return
	 */
	protected Map<AGenome,Object[][]> generateInitialPopulation() {
	

		// associates each geneome to its corresponding population (for one generation)
		Map<AGenome,Object[][]> genome2population = new HashMap<AGenome, Object[][]>();

		// generate the novel population for each specy
		// for each specy
		messages.debugUser("generation of the population for generation "+iterationsMade, getClass());
		for (AGenome genome : genome2algoInstance.keySet()) {
	
			messages.debugUser("generation of the sub population based on genome: "+genome, getClass());

			
			Object[][] population = generateInitialPopulation(genome, paramPopulationSize/genome2algoInstance.size());
			
			genome2population.put(genome, population);

			
		}
				
		return genome2population;
		
	}
	
	/**
	 * Mutates a population described by the genome passed as parameter, update the population in place, 
	 * and update the map of gene mutation counts.
	 * @param genome
	 * @param novelPopulation
	 * @param statsGene2countMutations
	 */
	protected void mutatePopulation(AGenome genome, Object[][] novelPopulation, Map<AGene<?>,Integer> statsGene2countMutations) {
		
		int countMutations = 0;
		StringBuffer sb = new StringBuffer();
		
		for (int i=0; i<novelPopulation.length; i++) {
			
			
			AGene<?>[] genes = genome.getGenes();
			for (int j=0; j<genes.length; j++) {
				 
				if (uniform.nextDoubleFromTo(0.0, 1.0) <= genes[j].getMutationProbability()) {
					
					Object[] individual = novelPopulation[i];
					String debugIndivBefore = Arrays.toString(individual);
					individual[j] = genes[j].mutate(uniform, individual[j]);
					sb.append("mutate individual ").append(i)
						.append(": ").append(debugIndivBefore)
						.append(" => ").append(Arrays.toString(individual))
						.append("\n")
						;
					// stats on mutation
					Integer count = statsGene2countMutations.get(genes[j]);
					if (count == null) {
						count = 0;
					} 
					statsGene2countMutations.put(genes[j], count+1);
					countMutations++;
				}
				
			}
			
			
		}
		
		messages.infoTech(countMutations+" mutations: "+sb.toString(), getClass());
	}
	

	/**
	 * Based on the list of selected individuals passed as parameter, generate the next population.
	 * This default crossover does not takes into account the fitness. It just deals with the 
	 * selected individuals, which were selected based on the fitness. 
	 * using crossover.   
	 * @param indivs
	 * @return
	 */
	protected Object[][] generateNextGenerationWithCrossover(AGenome genome, Set<AnIndividual> indivs, int popSize) {
		
		Object[][] novelPopulation = new Object[popSize][];
		int novelPopulationSize = 0;
		
		List<AnIndividual> selectedPopIndex = new LinkedList<AnIndividual>(indivs);

		StringBuffer sb = new StringBuffer();
		
		while (novelPopulationSize < popSize) {
		
			// randomly select individual 1
			int index1 = uniform.nextIntFromTo(0, selectedPopIndex.size()-1);
			// randomly select individual 2, different from 1
			int index2;
			do {
				index2 = uniform.nextIntFromTo(0, selectedPopIndex.size()-1);
			} while (index2 == index1);
			// randomly select the place where doing the cut
		
			Object[] indiv1 = selectedPopIndex.get(index1).genes;
			Object[] indiv2 = selectedPopIndex.get(index2).genes;

			//Object[][] novelIndividuals = crossoverOnePoint(genome, indiv1, indiv2);
			if (genome.crossoverProbability == 1.0 || uniform.nextDoubleFromTo(0.0, 1.0) <= genome.crossoverProbability) {
				// TODO use a parameter for crossover method
				Object[][] novelIndividuals = crossoverArithmetic(genome, indiv1, indiv2);
				//Object[][] novelIndividuals = crossoverOnePoint(genome, indiv1, indiv2);

				sb.append("crossover: ")
					.append(Arrays.toString(indiv1))
					.append(" and ")
					.append(Arrays.toString(indiv2))
					.append(" => ");
				
				indiv1 = novelIndividuals[0];
				indiv2 = novelIndividuals[1];
				
				sb.append(Arrays.toString(indiv1)).append(" and ").append(Arrays.toString(indiv2));
				sb.append("\n");
			}
			
			// add these individuals to the population (if the population is not already filled)
			novelPopulation[novelPopulationSize++] = indiv1;
			
			if (novelPopulationSize >= novelPopulation.length) 
				break;
			
			novelPopulation[novelPopulationSize++] = indiv2;
			
			
		}
		
		messages.infoUser("evolution: "+sb.toString(), getClass());

		return novelPopulation;
	}
	

	
	/**
	 * Selects a part of the existing population based on fitness.
	 * @return
	 */
	protected abstract INextGeneration selectIndividuals(Map<AnIndividual,Double[]> indiv2fitness);
	

	
	protected Map<AGenome,Object[][]> prepareNextGeneration() {
		
		int previousGenerationId = iterationsMade;
		
		// reuses the previous population
		final Map<AnIndividual,Double[]> indiv2fitness =  getIndivAndFitnessForLastGeneration();
		messages.infoUser("retrieved "+indiv2fitness.size()+" individuals from the previous generation "+previousGenerationId, getClass());
		
		// SELECT 
		
		// TODO elitism !
		
		final INextGeneration selectedIndividuals  = selectIndividuals(indiv2fitness);
		final Map<AGenome,Set<AnIndividual>> selectedGenome2Population = selectedIndividuals.getAllIndividuals();
		int totalIndividualsSelected = selectedIndividuals.getTotalOfIndividualsAllGenomes();
		messages.infoUser("selected for "+selectedGenome2Population.size()+" genome(s) a total of "+totalIndividualsSelected+" individuals", getClass());
		
		// TODO if the population is not big enough, recreate some novel individuals
		if (totalIndividualsSelected < paramPopulationSize) {
			messages.warnUser("not enough individuals selected ! Probably many individuals were not evaluated with success. We will create novel individuals to repopulate the population", getClass());

			for (AGenome genome : selectedGenome2Population.keySet()) {
				
				Set<AnIndividual> individuals = selectedGenome2Population.get(genome);
				
				int targetSizeForGenome = paramPopulationSize/genome2algoInstance.size(); // TODO should be what ?
				int diff = targetSizeForGenome - individuals.size(); 
				if (diff > 0) {
					
					messages.infoUser("adding "+diff +" new individuals in the population for genome "+genome.name, getClass());
					Object[][] population = generateInitialPopulation(genome, diff);
					for (Object[] indiv : population) {
						individuals.add(new AnIndividual(genome, indiv));
					}
				}
			}
			
		}
		
		// CROSS
		// TODO manage multi specy !
		
		Map<AGenome,Object[][]> novelGenome2Population = new HashMap<AGenome, Object[][]>();

		// stats on the count of mutation (to be returned to the user)
		Map<AGene<?>,Integer> statsGene2countMutations = new HashMap<AGene<?>, Integer>();
		
		for (AGenome genome: selectedGenome2Population.keySet()) {
			
			Set<AnIndividual> indivs = selectedGenome2Population.get(genome);
			
			// generate the next generation
			Object[][] novelPopulation = generateNextGenerationWithCrossover(
					genome, 
					indivs, 
					paramPopulationSize
					);
			
			// mutate in this novel generation
			mutatePopulation(genome, novelPopulation, statsGene2countMutations);
			
			// store this novel generation
			novelGenome2Population.put(genome, novelPopulation);
			
		}
		
		{
			StringBuffer sb = new StringBuffer();
			sb.append("during the generation of the population ").append(iterationsMade);
			sb.append(" there were these mutations per gene: ");
			for (Map.Entry<AGene<?>,Integer> gene2count : statsGene2countMutations.entrySet()) {
				sb.append(gene2count.getKey().name);
				sb.append(":");
				sb.append(gene2count.getValue());
				sb.append("; ");
			}
			messages.infoUser(sb.toString(), getClass());
		}
		
		exportContinuousOutput();
		
		return novelGenome2Population;
		
	}
	

	@Override
	protected boolean hasConverged() {
		// in the 
		return false;
	}

}
