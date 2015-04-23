package genlab.algog.algos.exec;

import genlab.algog.algos.instance.GeneticExplorationAlgoContainerInstance;
import genlab.algog.algos.meta.GeneticExplorationAlgo;
import genlab.algog.algos.meta.NSGA2GeneticExplorationAlgo;
import genlab.algog.internal.AGene;
import genlab.algog.internal.AGenome;
import genlab.algog.internal.ANumericGene;
import genlab.algog.internal.AnIndividual;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * TODO elitism ? 
 * TODO changer taille offspring en paramètre
 * 
 * @author Samuel Thiriot
 */
public class NSGA2Exec extends BasicGeneticExplorationAlgoExec {
	
	// variables used only during the selection of the next generation

	/**
	 * For the last generation associates each rank with its individuals
	 */
	protected SortedMap<Integer,Collection<AnIndividual>> fronts = null;
	
	/**
	 * For the last generation evaluated, associates each individual with its rank
	 */
	protected Map<AnIndividual,Integer> individual2rank = null;
	
	/**
	 * For the last generation, associates each individual with its fitness
	 */
	protected Map<AnIndividual, Double[]> indiv2fitnessForLastGenerations = null;
	

	/**
	 * for each generation, and each individual, stores the corresponding fitness
	 */
	protected final LinkedHashMap<Integer,Collection<AnIndividual>> generation2firstParetoFront;

	
	public NSGA2Exec(IExecution exec,
			GeneticExplorationAlgoContainerInstance algoInst) {
		super(exec, algoInst);

		// initializes the map of, for each iteration, the first pareto front
		generation2firstParetoFront = new LinkedHashMap<Integer,Collection<AnIndividual>>(paramStopMaxIterations);
		
	}

	/**
	 * a dominates b if for all fitness objective fa we got fa <= fb and it exists one fa < fb
	 * @param aFitness
	 * @param bFitness
	 * @return
	 */
	protected boolean dominates(Double[] aFitness, Double[] bFitness) {
		
		boolean d = false;
		
		for( int m=0 ; m<aFitness.length ; m++ ) {
			if( aFitness[m]>bFitness[m] ) {
				return false;
			}else if( bFitness[m]>aFitness[m] ) {
				d = true;
			}
		}
		
		if( d )
			return true;
		
		return false;
	}

	/**
	 * for display purpose, transforms a set of individuals 
	 * (possibly representing a Pareto front) to a String
	 * @param front
	 * @return
	 */
	protected String frontToString(Collection<AnIndividual> front) {
		StringBuffer sb = new StringBuffer("\t");
		for (AnIndividual i: front) {
			sb.append(i.toString()).append(" => ").append(Arrays.toString(indiv2fitnessForLastGenerations.get(i))).append("\n\t");
		}
		return sb.toString();
	}
	
	/**
	 * As described in NSGA-II
	 * @param individualsWFitness
	 */
	protected void fastNonDominatedSort(Map<AnIndividual, Double[]> individuals2fitness) {
		
		SortedMap<Integer,Collection<AnIndividual>> frontIdx2individuals = new TreeMap<Integer, Collection<AnIndividual>>();
		Map<AnIndividual, Integer> individual2rank = new HashMap<AnIndividual, Integer>(individuals2fitness.size());
		
		// first pass: discover for each individual how many individuals dominate it, and which children it dominates
		// also automatically builds the first front
		Map<AnIndividual,Integer> individual2dominationCount = new HashMap<AnIndividual, Integer>(individuals2fitness.size());
		Map<AnIndividual,Set<AnIndividual>> individual2dominated = new HashMap<AnIndividual, Set<AnIndividual>>(individuals2fitness.size());
		Collection<AnIndividual> currentFront = new LinkedList<AnIndividual>();
		
		for (AnIndividual p : individuals2fitness.keySet()) {
		
			final Double[] pFitness = individuals2fitness.get(p);
			
			if (pFitness == null)
				// don't even include individuals who have no fitness
				continue; 
			
			int dominationCount = 0;
			Set<AnIndividual> dominatedIndividuals = new HashSet<AnIndividual>(individuals2fitness.size());
			
			for (AnIndividual q : individuals2fitness.keySet()) {
				
				final Double[] qFitness = individuals2fitness.get(q);
						
				if (dominates(pFitness, qFitness)) {
					dominatedIndividuals.add(q);
				} else if (dominates(qFitness, pFitness)) {
					dominationCount++;
				}
				
			}
			
			individual2dominated.put(p, dominatedIndividuals);
			individual2dominationCount.put(p, dominationCount);

			if (dominationCount == 0) {
				// this individual belongs the first front
				currentFront.add(p);
				individual2rank.put(p, 1);
			}
			
		}
		
		messages.infoUser("for generation "+iterationsMade+", the Pareto front contains "+currentFront.size()+": "+currentFront.toString(), getClass());
		
		StringBuffer sbDomFronts = new StringBuffer();
		
		sbDomFronts.append("1st domination front (").append(currentFront.size()).append("): ").append(frontToString(currentFront)).append("\n");

		// save the first domination front
		generation2firstParetoFront.put(iterationsMade, currentFront);
		frontIdx2individuals.put(1, currentFront);
		
		
		// build the second, third, ..., X domination fronts
		int frontIdx = 1;
		Collection<AnIndividual> nextFront = null;
		while (!currentFront.isEmpty()) {
			
			nextFront = new LinkedList<AnIndividual>();
			for (AnIndividual p: currentFront) {
			
				for (AnIndividual q: individual2dominated.get(p)) {
					
					Integer nq = individual2dominationCount.get(q) - 1;
					individual2dominationCount.put(q, nq);
					
					if (nq == 0) {
						// q belongs the next front
						nextFront.add(q);
						individual2rank.put(q, frontIdx+1);
					}
					
				}
				
				
			}
			frontIdx++;
			if (!nextFront.isEmpty())
				frontIdx2individuals.put(frontIdx, nextFront);
			currentFront = nextFront;
			sbDomFronts.append(frontIdx).append("th domination front (").append(nextFront.size()).append("): ").append(frontToString(nextFront)).append("\n");
			
		}
		
		// we computed the fronts. God bless us.
		this.fronts = frontIdx2individuals;
		this.individual2rank = individual2rank;
		
		messages.infoUser("computed "+frontIdx2individuals.size()+" Pareto domination fronts: "+sbDomFronts.toString(), getClass());
	}
	
	/**
	 * Compares two individuals based on the fitness computed
	 * 
	 * @author Samuel Thiriot
	 *
	 */
	protected class ComparatorFitness implements Comparator<AnIndividual> {

		private final int m;
		private final Map<AnIndividual, Double[]> indiv2fitness;
		
		
		public ComparatorFitness(int m, Map<AnIndividual, Double[]> indiv2fitness) {
			this.m = m;
			this.indiv2fitness = indiv2fitness;
		}


		@Override
		public int compare(AnIndividual o1, AnIndividual o2) {
			
			final Double fitness1 = indiv2fitness.get(o1)[m];
			final Double fitness2 = indiv2fitness.get(o2)[m];
			return Double.compare(fitness1, fitness2);
		}
		
	}

	
	/**
	 * Compares two individuals based on the crowded stats
	 * @author Samuel Thiriot
	 *
	 */
	protected class ComparatorCrowded implements Comparator<AnIndividual> {
		
		final Map<AnIndividual,Double> individual2distance;
		
		public ComparatorCrowded(Map<AnIndividual,Double> individual2distance) {
			this.individual2distance = individual2distance;
		}
	
	
		@Override
		public int compare(AnIndividual a, AnIndividual b) {
	
			final Integer aRank = individual2rank.get(a);
			final Integer bRank = individual2rank.get(b);
			// first, we compare ranks
			if( aRank<bRank ) return -1;
			
			final Double aDistance = individual2distance.get(a);
			final Double bDistance = individual2distance.get(b);
			// else, we compare crowding distance
			if( (aRank==bRank) && (aDistance>bDistance) ) return -1;
			
			return 1;
		}
		
	}
	
	
	protected Map<AnIndividual,Double> calculateCrowdingDistance(Collection<AnIndividual> pop, Map<AnIndividual, Double[]> indiv2fitness) {
		
		int l = pop.size();
		
		Map<AnIndividual,Double> individual2distance = new HashMap<AnIndividual, Double>(pop.size());
		
		int objectivesCount = indiv2fitness.values().iterator().next().length;
		
		// init distance
		for (AnIndividual i: pop) {
			individual2distance.put(i,0d);
		}
		
		List<AnIndividual> sortedPop = new ArrayList<AnIndividual>(pop);
		for (int m=0; m<objectivesCount; m++) {
			
			Collections.sort(sortedPop, new ComparatorFitness(m, indiv2fitness));
			
			final double minFitness = indiv2fitness.get(sortedPop.get(0))[m];
			final double maxFitness = indiv2fitness.get(sortedPop.get(l-1))[m];
			final double diffFitness = maxFitness - minFitness;
			
			if (Double.isNaN(diffFitness)) {
				// ignore the individuals which were not evaluated (no data for comparison !)
				continue;
			}
					
			individual2distance.put(sortedPop.get(0), Double.POSITIVE_INFINITY);
			individual2distance.put(sortedPop.get(l-1), Double.POSITIVE_INFINITY);
			
			for (int i=1; i<l-2; i++) {
				Double d = individual2distance.get(sortedPop.get(i));
				d += (indiv2fitness.get(sortedPop.get(i+1))[m] - indiv2fitness.get(sortedPop.get(i-1))[m] ) / diffFitness;
				individual2distance.put(sortedPop.get(i), d);
			}
		}
		
		return individual2distance;
	}

	protected Set<AnIndividual> selectParents(Map<AnIndividual, Double[]> indiv2fitness, int parentsCountToSelect) {
		
		// TODO manage the numerous genomes ! we have there no guarantee to keep all the genomes !
		
		Set<AnIndividual> offsprings = new HashSet<AnIndividual>(indiv2fitness.size());
		int lastFrontIdx = 1;
		
		// first add as many entire fronts as possible
		for (Integer frontIdx : fronts.keySet()) {
		
			Collection<AnIndividual> front = fronts.get(frontIdx);
						
			if (offsprings.size() + front.size() > parentsCountToSelect) {
				// we selected enough fronts.
				break;
			}
			
			messages.infoUser("keeping as offsprings the "+front.size()+" individual of front "+frontIdx, getClass());

			// add all the fronts
			offsprings.addAll(front);
			
			lastFrontIdx++;
		}
		
		int remaining = parentsCountToSelect - offsprings.size();
		if (remaining > 0) {
			
			if (fronts.get(lastFrontIdx) == null || fronts.get(lastFrontIdx).isEmpty()) {
				messages.infoUser("no individuals to select from front "+lastFrontIdx+" which is empty", getClass());
			} else {
				messages.infoUser("still have to select "+remaining+" offsprings; will select them from the front "+lastFrontIdx, getClass());
				
				// now complete with only a part of the last front
				
				List<AnIndividual> sortedFront = new ArrayList<AnIndividual>(fronts.get(lastFrontIdx));
				Map<AnIndividual,Double> individual2distance = calculateCrowdingDistance(sortedFront, indiv2fitness);
				
				Collections.sort(sortedFront, new ComparatorCrowded(individual2distance));
				
				// add the best ones based on the crowded operator
				// (as long as we do have some offsprings !)
				for (int i=0; i<remaining && i < sortedFront.size(); i++) {
					offsprings.add(sortedFront.get(i));
				}
			}
		}
		
		if (offsprings.size() < parentsCountToSelect)
			messages.infoUser("we were not able to select enough individuals from Q(t) and P(t): selected "+offsprings.size()+" for "+parentsCountToSelect+" expected", getClass());
		
		return offsprings;
	}
	
	public Map<AnIndividual,Double[]> getIndivAndFitnessFor2LastGenerations(int iterationsMade) {
		
		Map<AnIndividual,Double[]> previous = generation2fitness.get(iterationsMade-1);

		if (previous == null)
			// generation evaluated: Q(t)
			return generation2fitness.get(iterationsMade);
		else {
			Map<AnIndividual,Double[]> res = new HashMap<AnIndividual, Double[]>(paramPopulationSize*2);
			
			// add the last generation (there is always one)
			// generation evaluated: Q(t)
			res.putAll(generation2fitness.get(iterationsMade));
			
			// add the previous generation (only if available)
			// generation evaluated: P(t)
			res.putAll(previous);
			
			return res;
		}
		
	}
	

	public Map<AnIndividual,Object[]> getIndivAndTargetFor2LastGenerations(int iterationsMade) {
		
		Map<AnIndividual,Object[]> previous = generation2targets.get(iterationsMade-1);

		if (previous == null)
			// generation evaluated: Q(t)
			return generation2targets.get(iterationsMade);
		else {
			Map<AnIndividual,Object[]> res = new HashMap<AnIndividual, Object[]>(paramPopulationSize*2);
			
			// add the last generation (there is always one)
			// generation evaluated: Q(t)
			res.putAll(generation2targets.get(iterationsMade));
			
			// add the previous generation (only if available)
			// generation evaluated: P(t)
			res.putAll(previous);
			
			return res;
		}
		
	}
	
	public Map<AnIndividual,Object[]> getIndivAndValuesFor2LastGenerations(int iterationsMade) {
		
		Map<AnIndividual,Object[]> previous = generation2values.get(iterationsMade-1);

		if (previous == null)
			// generation evaluated: Q(t)
			return generation2values.get(iterationsMade);
		else {
			Map<AnIndividual,Object[]> res = new HashMap<AnIndividual, Object[]>(paramPopulationSize*2);
			
			// add the last generation (there is always one)
			// generation evaluated: Q(t)
			res.putAll(generation2values.get(iterationsMade));
			
			// add the previous generation (only if available)
			// generation evaluated: P(t)
			res.putAll(previous);
			
			return res;
		}
		
	}
	
	protected void analyzeLastPopulation(Map<AnIndividual, Double[]> indiv2fitness) {
		
		// we are called from the parent class with only the last generation.
		// but NSGA2, in order to introduce elitism, compares the current generation and the previous one (if any).
		// so we first preprocess the received fitness with the previous generation
		// also a good way to remove solutions at negative infinity (failure)
		this.indiv2fitnessForLastGenerations = getIndivAndFitnessFor2LastGenerations(iterationsMade);
		if (this.indiv2fitnessForLastGenerations.size() > paramPopulationSize) 
			messages.infoUser("elitism : taking into account the results of the previous iteration "+(this.iterationsMade-1), getClass());
		
		// reset internal variables
		this.fronts = null;
		this.individual2rank = null;
		
		// compute fronts and rank on P(t) U Q(t)
		fastNonDominatedSort(this.indiv2fitnessForLastGenerations);
		
	}
	
	protected INextGeneration selectIndividuals(Map<AnIndividual, Double[]> indiv2fitness) {

		// analyze the last run and the one before
		analyzeLastPopulation(indiv2fitness);
		
		// select parents
		// TODO parameter: proportion of parents to select ? 
		Set<AnIndividual> offsprings  = selectParents(this.indiv2fitnessForLastGenerations, paramPopulationSize);

		// now sort the population by genome, as expected by the parent algo
		NextGenerationWithElitism selectedIndividuals = new NextGenerationWithElitism(indiv2fitness.size());
		for (AnIndividual offspring: offsprings) {
			
			selectedIndividuals.addIndividual(offspring.genome, offspring);
			
		}
		
		// clear internal variables (free memory)
		this.fronts = null;
		this.individual2rank = null;
		this.indiv2fitnessForLastGenerations = null;
		
		return selectedIndividuals;
	}
	
	/**
	 * Takes all the pareto fronts detected during simulation, 
	 * and packs them as a table to be exported.
	 * @return
	 */
	protected GenlabTable packParetoFrontsAsTable() {
		
		final String titleIteration = "iteration";
		final String titleParetoGenome = "pareto genome";
		
		GenlabTable tab = new GenlabTable();
		tab.declareColumn(titleIteration);
		tab.setTableMetaData(GeneticExplorationAlgo.TABLE_METADATA_KEY_COLTITLE_ITERATION, titleIteration);
		tab.setTableMetaData(GeneticExplorationAlgo.TABLE_METADATA_KEY_MAX_ITERATIONS, paramStopMaxIterations);
		
		tab.declareColumn(titleParetoGenome);		
		
		// declare columns for each fitness
		final Map<AGenome,String[]> genome2fitnessColumns = declareColumnsForGoals(tab);
		
		// declare columns for each possible gene
		final Map<AGenome,String[]> genome2geneColumns = declareColumnsForGenes(tab);
				
		for (Integer iterationId : generation2firstParetoFront.keySet()) {
			
			// for each iteration
			final Collection<AnIndividual> individuals = generation2firstParetoFront.get(iterationId);
			final Map<AnIndividual,Double[]> generationFitness = getIndivAndFitnessFor2LastGenerations(iterationId);
			final Map<AnIndividual,Object[]> indiv2value = getIndivAndValuesFor2LastGenerations(iterationId);
			final Map<AnIndividual,Object[]> indiv2target = getIndivAndTargetFor2LastGenerations(iterationId);
			
			storeIndividualsData(
					tab, 
					titleIteration, iterationId, titleParetoGenome, 
					genome2fitnessColumns, genome2geneColumns, 
					individuals, generationFitness, indiv2value, indiv2target
					);
			
		}
		
		return tab;
	}
	

	/**
	 * Add something to the result to be exported as an intermediate version.
	 * @param res
	 */
	protected void completeContinuousIntermediateResult(ComputationResult res) {

		super.completeContinuousIntermediateResult(res);
		
		// add our pareto fronts
		res.setResult(
				NSGA2GeneticExplorationAlgo.OUTPUT_TABLE_PARETO, 
				packParetoFrontsAsTable()
				);
		
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
		
		messages.infoTech(countMutations+" mutations !! : "+sb.toString(), getClass());
	}

	/**
	 * Create two children by the N points crossover operator
	 * @param genome
	 * @param nCuts
	 * @param parent1
	 * @param parent2
	 * @return
	 */
	protected final Object[][] crossoverNPoints(final AGenome genome, int nCuts, Object[] parent1, Object[] parent2) {
		
		Object[][] children = new Object[2][];
		Object[] child1 = new Object[genome.getGenes().length];
		Object[] child2 = new Object[genome.getGenes().length];
		
		int t = 0;
		int[] cuts = new int[nCuts+1];
		cuts[0] = 0;
		boolean crossoverApplied = uniform.nextBoolean();
		
		for(int i=1 ; i<=nCuts ; i++) {
			cuts[i] = uniform.nextIntFromTo(1, genome.getGenes().length-1);
		}
		
		Arrays.sort(cuts);
		
		for(int i=0 ; i<genome.getGenes().length ; i++) {
			// if crossoverOn is true then first child genes are copied from parent2
			if( crossoverApplied ) {
				child1[i] = parent2[i];
				child2[i] = parent1[i];
			}
			// else first child genes are copied from parent1
			else {
				child1[i] = parent1[i];
				child2[i] = parent2[i];
			}
			
			if( t<cuts.length && cuts[t]==i ) {
				crossoverApplied = !crossoverApplied;
				t++;
				while( t<cuts.length && cuts[t]==i ) t++;
			}
		}
	
		children[0] = child1;
		children[1] = child2;
		
		return children;
	}
	

	protected AnIndividual crowdedTournamentSelection(AnIndividual ind1, AnIndividual ind2, Map<AnIndividual,Double> individual2distance) {	
		
		Map<AnIndividual, Double[]> individuals2fitness = new HashMap<AnIndividual, Double[]>(getIndivAndFitnessFor2LastGenerations(iterationsMade));
		
		// if 1 dominates 2
		if( dominates(individuals2fitness.get(ind1), individuals2fitness.get(ind2)) ) {
			return ind1;
		}
		// else if 2 dominates 1
		else if( dominates(individuals2fitness.get(ind2), individuals2fitness.get(ind1)) ) {
			return ind2;
		}
		// else if 1 is most spread than 2
		else if( individual2distance.get(ind1)>individual2distance.get(ind2) ) {
			return ind1;
		}
		// else if 2 is most spread than 1
		else if( individual2distance.get(ind2)>individual2distance.get(ind1) ) {
			return ind2;
		}
		// else who's the luckier?
		else if( uniform.nextBoolean() ) {
			return ind1;
		}else {
			return ind2;
		}		
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
			// select 4 different index which will define the index of 4 individuals from the population
			int index1, index2, index3, index4;
			do {
				index1 = uniform.nextIntFromTo(0, selectedPopIndex.size()-1);
				index2 = uniform.nextIntFromTo(0, selectedPopIndex.size()-1);
				index3 = uniform.nextIntFromTo(0, selectedPopIndex.size()-1);
				index4 = uniform.nextIntFromTo(0, selectedPopIndex.size()-1);
			} while( (index1==index2) || (index1==index3) || (index1==index4) || (index2==index3) || (index2==index4) || (index3==index4) );// best optimization ever

			AnIndividual p1 = crowdedTournamentSelection(selectedPopIndex.get(index1), selectedPopIndex.get(index2), calculateCrowdingDistance(indivs, getIndivAndFitnessFor2LastGenerations(iterationsMade)));
			AnIndividual p2 = crowdedTournamentSelection(selectedPopIndex.get(index3), selectedPopIndex.get(index4), calculateCrowdingDistance(indivs, getIndivAndFitnessFor2LastGenerations(iterationsMade)));

			Object[] indiv1 = p1.genes;
			Object[] indiv2 = p2.genes;
			
			//Object[][] novelIndividuals = crossoverOnePoint(genome, indiv1, indiv2);
			if (genome.crossoverProbability == 1.0 || uniform.nextDoubleFromTo(0.0, 1.0) <= genome.crossoverProbability) {
				// TODO use a parameter for crossover method
				Object[][] novelIndividuals = crossoverNPoints(genome, 1, indiv1, indiv2);
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
	

	@Override
	/*
	 * Entry point: called for each novel generation.
	 * (non-Javadoc)
	 * @see genlab.algog.algos.exec.AbstractGeneticExplorationAlgoExec#prepareNextGeneration()
	 */
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
		// we are not yet able to detect if the algo has converged !
		return false;
	}


	@Override
	/**
	 * At the very end of the exploration (end of all generations)
	 * analyzes the last population and keeps the best Pareto print. 
	 */
	protected void hookProcessResults(ComputationResult res, ComputationState ourState) {

		if (ourState != ComputationState.FINISHED_OK)
			return;
		
		// process the last Pareto front
		analyzeLastPopulation(generation2fitness.get(iterationsMade));

		// and define the result for Pareto
		res.setResult(
				NSGA2GeneticExplorationAlgo.OUTPUT_TABLE_PARETO, 
				packParetoFrontsAsTable()
				);
		
	}
}
