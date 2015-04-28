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
	
	/** for the last generation associates each rank with its individuals */
	protected SortedMap<Integer,Collection<AnIndividual>> fronts = null;
	/** for the last generation evaluated, associates each individual with its rank */
	protected Map<AnIndividual,Integer> individualWRank = null;
	/** for the last generation, associates each individual with its fitness */
	protected Map<AnIndividual, Double[]> individualWFitness_LastGenerations = null;
	/** for each generation, and each individual, stores the corresponding fitness */
	protected final LinkedHashMap<Integer,Collection<AnIndividual>> generationWFirstPF;

	/**
	 * Constructor
	 * @param exec
	 * @param algoInst
	 */
	public NSGA2Exec(IExecution exec, GeneticExplorationAlgoContainerInstance algoInst) {
		super(exec, algoInst);
		// initializes the map of, for each iteration, the first pareto front
		generationWFirstPF = new LinkedHashMap<Integer,Collection<AnIndividual>>(paramStopMaxIterations);
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
		
		StringBuffer sb = new StringBuffer("");
		
		for (AnIndividual i: front) {
			sb.append(i.toString()).append(" => ").append(Arrays.toString(individualWFitness_LastGenerations.get(i))).append("\n");
		}
		
		return sb.toString();
	}
	
	/**
	 * first pass: discover for each individual how many individuals dominate it, and which children it dominates<br />
	 * then build domination fronts
	 * @param individualsWFitness
	 */
	protected void fastNonDominatedSort(Map<AnIndividual, Double[]> individualsWFitness) {
		
		SortedMap<Integer,Collection<AnIndividual>> frontIndexWIndividuals = new TreeMap<Integer, Collection<AnIndividual>>();
		Map<AnIndividual, Integer> individualWRank = new HashMap<AnIndividual, Integer>(individualsWFitness.size());
		Map<AnIndividual,Integer> individualWDominationCount = new HashMap<AnIndividual, Integer>(individualsWFitness.size());
		Map<AnIndividual,Set<AnIndividual>> individualWDominatedIndividuals = new HashMap<AnIndividual, Set<AnIndividual>>(individualsWFitness.size());
		Collection<AnIndividual> individualsInCurrentFront = new LinkedList<AnIndividual>();
		
		for( AnIndividual p : individualsWFitness.keySet() ) {
			final Double[] pFitness = individualsWFitness.get(p);
			
			// don't even include individuals who have no fitness
			if( pFitness==null )
				continue; 
			
			int dominationCount = 0;
			Set<AnIndividual> dominatedIndividuals = new HashSet<AnIndividual>(individualsWFitness.size());
			
			for( AnIndividual q : individualsWFitness.keySet() ) {
				final Double[] qFitness = individualsWFitness.get(q);
						
				if( dominates(pFitness, qFitness) ) {
					dominatedIndividuals.add(q);
				}else if( dominates(qFitness, pFitness) ) {
					dominationCount++;
				}
			}
			
			individualWDominatedIndividuals.put(p, dominatedIndividuals);
			individualWDominationCount.put(p, dominationCount);

			// does this individual belong to the first front?
			if( dominationCount==0 ) {
				individualsInCurrentFront.add(p);
				individualWRank.put(p, 1);
			}
		}
		
		messages.infoUser("For the "+iterationsMade+". generation, the Pareto front contains "+individualsInCurrentFront.size()+": "+individualsInCurrentFront.toString(), getClass());
		
		StringBuffer _message = new StringBuffer();
		
		_message.append("1. domination front (")
			.append(individualsInCurrentFront.size())
			.append("):\n")
			.append(frontToString(individualsInCurrentFront))
			.append("\n");

		// save the first domination front
		generationWFirstPF.put(iterationsMade, individualsInCurrentFront);
		frontIndexWIndividuals.put(1, individualsInCurrentFront);
		
		// build the second, third, ..., Xth domination fronts
		int frontIndex = 1;
		Collection<AnIndividual> nextFront = null;
		
		while( !individualsInCurrentFront.isEmpty() ) {
			nextFront = new LinkedList<AnIndividual>();
			
			for( AnIndividual p : individualsInCurrentFront ) {
				for( AnIndividual q : individualWDominatedIndividuals.get(p) ) {
					Integer nq = individualWDominationCount.get(q) - 1;
					individualWDominationCount.put(q, nq);
					// if q belongs to the next front
					if( nq==0 ) {
						nextFront.add(q);
						individualWRank.put(q, frontIndex+1);
					}
				}
			}
			
			frontIndex++;
			individualsInCurrentFront = nextFront;
			
			if( !nextFront.isEmpty() ) {
				frontIndexWIndividuals.put(frontIndex, nextFront);
				_message.append(frontIndex)
					.append(". domination front (")
					.append(nextFront.size())
					.append("): ")
					.append(frontToString(nextFront))
					.append("\n");
			}
		}
		
		// we don't always compute the fronts, but when we do: brace yourself
		this.fronts = frontIndexWIndividuals;
		this.individualWRank = individualWRank;
		
		messages.infoUser("There are "+frontIndexWIndividuals.size()+" Pareto domination fronts: "+_message.toString(), getClass());
	}
	
	/**
	 * Compares two individuals based on the fitness computed
	 * @author Samuel Thiriot
	 */
	protected class ComparatorFitness implements Comparator<AnIndividual> {

		private final int m;
		private final Map<AnIndividual, Double[]> individualWFitness;
		
		public ComparatorFitness(int m, Map<AnIndividual, Double[]> individualWFitness) {
			this.m = m;
			this.individualWFitness = individualWFitness;
		}

		@Override
		public int compare(AnIndividual o1, AnIndividual o2) {
			
			final Double fitness1 = individualWFitness.get(o1)[m];
			final Double fitness2 = individualWFitness.get(o2)[m];
			return Double.compare(fitness1, fitness2);
		}
	}

	/**
	 * Compares two individuals based on the crowded stats
	 * @author Samuel Thiriot
	 */
	protected class ComparatorCrowded implements Comparator<AnIndividual> {
		
		final Map<AnIndividual,Double> individualWDistance;
		
		public ComparatorCrowded(Map<AnIndividual,Double> individualWDistance) {
			this.individualWDistance = individualWDistance;
		}
		
		@Override
		public int compare(AnIndividual a, AnIndividual b) {

			final Double aDistance = individualWDistance.get(a);
			final Double bDistance = individualWDistance.get(b);
			return Double.compare(bDistance, aDistance);
		}
	}
	
	/**
	 * Compute the crowding distance
	 * @param population
	 * @param individualWFitness
	 * @return
	 */
	protected Map<AnIndividual,Double> calculateCrowdingDistance(Collection<AnIndividual> population, Map<AnIndividual, Double[]> individualWFitness) {
		
		int l = population.size();
		int objectivesCount = individualWFitness.values().iterator().next().length;
		Map<AnIndividual,Double> individualWDistance = new HashMap<AnIndividual, Double>(population.size());
		List<AnIndividual> sortedPop = new ArrayList<AnIndividual>(population);
		
		// set distance to 0
		for( AnIndividual i : population ) {
			individualWDistance.put(i,0d);
		}
		
		for (int m=0; m<objectivesCount; m++) {			
			Collections.sort(sortedPop, new ComparatorFitness(m, individualWFitness));
			
			final double minFitness = individualWFitness.get(sortedPop.get(0))[m];
			final double maxFitness = individualWFitness.get(sortedPop.get(l-1))[m];
			final double diffFitness = maxFitness - minFitness;
			
			// ignore the individuals which were not evaluated (no data for comparison !)
			if (Double.isNaN(diffFitness))
				continue;
					
			individualWDistance.put(sortedPop.get(0), Double.POSITIVE_INFINITY);
			individualWDistance.put(sortedPop.get(l-1), Double.POSITIVE_INFINITY);
			
			for( int i=1 ; i<l-2 ; i++ ) {
				Double d = individualWDistance.get(sortedPop.get(i));
				d += (individualWFitness.get(sortedPop.get(i+1))[m] - individualWFitness.get(sortedPop.get(i-1))[m] ) / diffFitness;
				individualWDistance.put(sortedPop.get(i), d);
			}
		}
		
		return individualWDistance;
	}

	/**
	 * Select P(t+1)
	 * @param individualWFitness
	 * @param parentsCountToSelect
	 * @return
	 */
	protected Set<AnIndividual> selectParents(Map<AnIndividual, Double[]> individualWFitness, int parentsCountToSelect) {
		
		// TODO manage the numerous genomes ! we have there no guarantee to keep all the genomes !
		Set<AnIndividual> offsprings = new HashSet<AnIndividual>(individualWFitness.size());
		int lastFrontIndex = 1;
		
		// first add as many entire fronts as possible
		for( Integer frontIdx : fronts.keySet() ) {
			Collection<AnIndividual> front = fronts.get(frontIdx);
			// if we selected enough fronts
			if (offsprings.size() + front.size() > parentsCountToSelect)
				break;
			
			messages.infoUser("Keeping (as offspring) the "+front.size()+" individuals of front "+frontIdx, getClass());

			// add all the fronts
			offsprings.addAll(front);
			
			lastFrontIndex++;
		}
		
		int remaining = parentsCountToSelect - offsprings.size();
		
		if( remaining>0 ) {
			if( fronts.get(lastFrontIndex)==null || fronts.get(lastFrontIndex).isEmpty() ) {
				messages.infoUser("No individual to select from front "+lastFrontIndex+" which is empty", getClass());
			}else {
				messages.infoUser("Still have to select "+remaining+" offsprings (will select them from the front "+lastFrontIndex+")", getClass());
				
				// now complete with only a part of the last front				
				List<AnIndividual> sortedFront = new ArrayList<AnIndividual>(fronts.get(lastFrontIndex));
				Map<AnIndividual,Double> individual2distance = calculateCrowdingDistance(sortedFront, individualWFitness);
				
				Collections.sort(sortedFront, new ComparatorCrowded(individual2distance));
				
				// add the best ones based on the crowded operator (as long as we do have some offsprings !)
				for( int i=0 ; i<remaining && i<sortedFront.size() ; i++ ) {
					offsprings.add(sortedFront.get(i));
				}
			}
		}
		
		if( offsprings.size()<parentsCountToSelect ) {
			messages.infoUser("We were not able to select enough individuals from Q(t) and P(t): selected "+offsprings.size()+" for "+parentsCountToSelect+" expected", getClass());
		}
		
		return offsprings;
	}
	
	/**
	 * Get individual and fitness for two last generations
	 * @param iterationsMade
	 * @return
	 */
	public Map<AnIndividual,Double[]> getIndividualWFitnessFor2LastGenerations(int iterationsMade) {
		
		Map<AnIndividual,Double[]> res = new HashMap<AnIndividual, Double[]>();
		Map<AnIndividual,Double[]> previous = generation2fitness.get(iterationsMade-1);
		
		res.putAll(generation2fitness.get(iterationsMade));

		if( previous!=null )
			res.putAll(previous);
		
		return res;
	}
	
	/**
	 * Get individual and targets for two last generations
	 * @param iterationsMade
	 * @return
	 */
	public Map<AnIndividual,Object[]> getIndividualWTargetFor2LastGenerations(int iterationsMade) {
		
		Map<AnIndividual,Object[]> res = new HashMap<AnIndividual, Object[]>();
		Map<AnIndividual,Object[]> previous = generation2targets.get(iterationsMade-1);
		
		res.putAll(generation2targets.get(iterationsMade));

		if( previous!=null )
			res.putAll(previous);
		
		return res;
	}
	
	/**
	 * Get individual and values for two last generations
	 * @param iterationsMade
	 * @return
	 */
	public Map<AnIndividual,Object[]> getIndividualWValuesFor2LastGenerations(int iterationsMade) {
		
		Map<AnIndividual,Object[]> res = new HashMap<AnIndividual, Object[]>();
		Map<AnIndividual,Object[]> previous = generation2values.get(iterationsMade-1);
		
		res.putAll(generation2values.get(iterationsMade));

		if( previous!=null )
			res.putAll(previous);
		
		return res;
	}
	
	/**
	 * We are called from the parent class with only the last generation.
	 * But NSGA2, in order to introduce elitism, compares the current generation and the previous one (if any).
	 * so we first preprocess the received fitness with the previous generation
	 * also a good way to remove solutions at negative infinity (failure)
	 * @param individualWFitness
	 */
	protected void analyzeLastPopulation(Map<AnIndividual, Double[]> individualWFitness) {

		this.individualWFitness_LastGenerations = getIndividualWFitnessFor2LastGenerations(iterationsMade);
		
//		for( AnIndividual i : individualWFitness_LastGenerations.keySet() ) {
//			System.out.println(""+i.toString()+" : "+Arrays.toString(individualWFitness_LastGenerations.get(i)));
//		}
		
//		if( this.individualWFitness_LastGenerations.size()>paramPopulationSize ) 
//			messages.infoUser("elitism : taking into account the results of the previous iteration "+(this.iterationsMade-1), getClass());
		
		// reset internal variables
		this.fronts = null;
		this.individualWRank = null;
		
		// compute fronts and rank on P(t) U Q(t)
		fastNonDominatedSort(this.individualWFitness_LastGenerations);
	}
	
	/**
	 * Select parents
	 * @param individualWFitness
	 * @return
	 */
	protected INextGeneration selectIndividuals(Map<AnIndividual, Double[]> individualWFitness) {
		// analyze the last run and the one before
		analyzeLastPopulation(individualWFitness);
		
		// select parents
		// TODO parameter: proportion of parents to select ? 
		Set<AnIndividual> offsprings  = selectParents(this.individualWFitness_LastGenerations, paramPopulationSize);
		// now sort the population by genome, as expected by the parent algo
		NextGenerationWithElitism selectedIndividuals = new NextGenerationWithElitism(individualWFitness.size());
		
		for( AnIndividual offspring : offsprings ) {
			selectedIndividuals.addIndividual(offspring.genome, offspring);
		}
		
		// clear internal variables (free memory)
		this.fronts = null;
		this.individualWRank = null;
		this.individualWFitness_LastGenerations = null;
		
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
				
		for( Integer iterationId : generationWFirstPF.keySet() ) {
			// for each iteration
			final Collection<AnIndividual> individuals = generationWFirstPF.get(iterationId);
			final Map<AnIndividual,Double[]> generationFitness = getIndividualWFitnessFor2LastGenerations(iterationId);
			final Map<AnIndividual,Object[]> indiv2value = getIndividualWValuesFor2LastGenerations(iterationId);
			final Map<AnIndividual,Object[]> indiv2target = getIndividualWTargetFor2LastGenerations(iterationId);
			
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
	 * @param statsGeneWCountMutations
	 */
	protected void mutatePopulation(AGenome genome, Object[][] novelPopulation, Map<AGene<?>,Integer> statsGeneWCountMutations) {
		
		int countMutations = 0;
		StringBuffer _message = new StringBuffer();
		
		for( int i=0 ; i<novelPopulation.length ; i++) {
			AGene<?>[] genes = genome.getGenes();

			for (int j=0; j<genes.length; j++) {
				if (uniform.nextDoubleFromTo(0.0, 1.0) <= genes[j].getMutationProbability()) {
					Object[] individual = novelPopulation[i];
					String debugIndivBefore = Arrays.toString(individual);
					
					individual[j] = genes[j].mutate(uniform, individual[j]);
					
					_message.append("\nMutate individual n°").append(i)
						.append(" from ").append(debugIndivBefore)
						.append(" to ").append(Arrays.toString(individual));
					
					// stats on mutation
					Integer count = statsGeneWCountMutations.get(genes[j]);
					
					if( count==null ) {
						count = 0;
					}
					
					statsGeneWCountMutations.put(genes[j], count+1);
					countMutations++;
				}
			}
		}
		
		messages.infoTech("Mutations ("+countMutations+")"+_message.toString(), getClass());
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
		
		for( int i=1 ; i<=nCuts ; i++ ) {
			cuts[i] = uniform.nextIntFromTo(1, genome.getGenes().length-1);
		}
		
		Arrays.sort(cuts);
		
		for( int i=0 ; i<genome.getGenes().length ; i++ ) {
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
	
	/**
	 * Crowded Tournament Selection operator
	 * @param ind1
	 * @param ind2
	 * @param individualWDistance
	 * @return
	 */
	protected AnIndividual crowdedTournamentSelection(AnIndividual ind1, AnIndividual ind2, Map<AnIndividual,Double> individualWDistance) {	
		
		Map<AnIndividual, Double[]> individuals2fitness = new HashMap<AnIndividual, Double[]>(getIndividualWFitnessFor2LastGenerations(iterationsMade));
		
		// if 1 dominates 2
		if( dominates(individuals2fitness.get(ind1), individuals2fitness.get(ind2)) ) {
			return ind1;
		}
		// else if 2 dominates 1
		else if( dominates(individuals2fitness.get(ind2), individuals2fitness.get(ind1)) ) {
			return ind2;
		}
		// else if 1 is most spread than 2
		else if( individualWDistance.get(ind1)>individualWDistance.get(ind2) ) {
			return ind1;
		}
		// else if 2 is most spread than 1
		else if( individualWDistance.get(ind2)>individualWDistance.get(ind1) ) {
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
	 * @param individuals
	 * @return
	 */
	protected Object[][] generateNextGenerationWithCrossover(AGenome genome, Set<AnIndividual> individuals, int populationSize) {
		
		Object[][] novelPopulation = new Object[populationSize][];
		int novelPopulationSize = 0, countCrossover = 0;;		
		List<AnIndividual> selectedPopIndex = new LinkedList<AnIndividual>(individuals);

		StringBuffer _message = new StringBuffer();
		
		while( novelPopulationSize<populationSize ) {
			// select 4 different index which will define the index of 4 individuals from the population
			int index1, index2, index3, index4;
			do {
				index1 = uniform.nextIntFromTo(0, selectedPopIndex.size()-1);
				index2 = uniform.nextIntFromTo(0, selectedPopIndex.size()-1);
				index3 = uniform.nextIntFromTo(0, selectedPopIndex.size()-1);
				index4 = uniform.nextIntFromTo(0, selectedPopIndex.size()-1);
			} while( (index1==index2) || (index1==index3) || (index1==index4) || (index2==index3) || (index2==index4) || (index3==index4) );// best optimization ever

			AnIndividual p1 = crowdedTournamentSelection(selectedPopIndex.get(index1), selectedPopIndex.get(index2), calculateCrowdingDistance(individuals, getIndividualWFitnessFor2LastGenerations(iterationsMade)));
			AnIndividual p2 = crowdedTournamentSelection(selectedPopIndex.get(index3), selectedPopIndex.get(index4), calculateCrowdingDistance(individuals, getIndividualWFitnessFor2LastGenerations(iterationsMade)));

			Object[] indiv1 = p1.genes;
			Object[] indiv2 = p2.genes;
			
			if( genome.crossoverProbability==1.0 || uniform.nextDoubleFromTo(0.0, 1.0)<=genome.crossoverProbability ) {
				// TODO use a parameter for crossover method
				Object[][] novelIndividuals = crossoverNPoints(genome, 1, indiv1, indiv2);
				countCrossover++;

				_message.append("\nCrossover between ")
					.append(Arrays.toString(indiv1))
					.append(" and ")
					.append(Arrays.toString(indiv2))
					.append(" : ");
				
				indiv1 = novelIndividuals[0];
				indiv2 = novelIndividuals[1];
				
				_message.append(Arrays.toString(indiv1)).append(" and ").append(Arrays.toString(indiv2));
			}
			
			// add these individuals to the population (if the population is not already filled)
			novelPopulation[novelPopulationSize++] = indiv1;
			
			if( novelPopulationSize>=novelPopulation.length ) 
				break;
			
			novelPopulation[novelPopulationSize++] = indiv2;
		}
		
		messages.infoUser("Evolution ("+countCrossover+")"+_message.toString(), getClass());

		return novelPopulation;
	}
	

	@Override
	protected Map<AGenome,Object[][]> prepareNextGeneration() {
		
		// reuses the previous population
		final Map<AnIndividual,Double[]> individualWFitness =  getIndivAndFitnessForLastGeneration();
		messages.infoUser("Retrieved "+individualWFitness.size()+" individuals from the previous generation ("+iterationsMade+")", getClass());
		
		// SELECT		
		// TODO elitism !
		final INextGeneration selectedIndividuals  = selectIndividuals(individualWFitness);
		final Map<AGenome,Set<AnIndividual>> selectedGenomeWPopulation = selectedIndividuals.getAllIndividuals();
		int totalIndividualsSelected = selectedIndividuals.getTotalOfIndividualsAllGenomes();
		
		messages.infoUser("Selected for "+selectedGenomeWPopulation.size()+" genome(s) a total of "+totalIndividualsSelected+" individuals", getClass());
		
		// CROSS
		// TODO manage multi specy !
		Map<AGenome,Object[][]> novelGenomeWPopulation = new HashMap<AGenome, Object[][]>();
		// stats on the count of mutation (to be returned to the user)
		Map<AGene<?>,Integer> statsGeneWCountMutations = new HashMap<AGene<?>, Integer>();
		
		for( AGenome genome : selectedGenomeWPopulation.keySet() ) {
			Set<AnIndividual> individuals = selectedGenomeWPopulation.get(genome);
			// generate the next generation
			Object[][] novelPopulation = generateNextGenerationWithCrossover(
				genome, 
				individuals, 
				paramPopulationSize
			);
			
			// mutate in this novel generation
			mutatePopulation(genome, novelPopulation, statsGeneWCountMutations);
			
			// store this novel generation
			novelGenomeWPopulation.put(genome, novelPopulation);
		}
		
		{
			StringBuffer _message = new StringBuffer();
			_message.append("During the generation of the population n°").append(iterationsMade);
			_message.append(" there were these mutations per gene:\n");
			for (Map.Entry<AGene<?>,Integer> gene2count : statsGeneWCountMutations.entrySet()) {
				_message.append(gene2count.getKey().name);
				_message.append(":");
				_message.append(gene2count.getValue());
				_message.append("; ");
			}
			
			messages.infoUser(_message.toString(), getClass());
		}

		messages.warnUser( "Loss: " + (paramPopulationSize-totalIndividualsSelected) + " individual(s), thus " + Math.round((paramPopulationSize-totalIndividualsSelected)*100/paramPopulationSize) + "% of the population will must be regenerated", getClass());

		// TODO if the population is not big enough, recreate some novel individuals
		if( totalIndividualsSelected<paramPopulationSize ) {
			
			messages.warnUser( Math.round((paramPopulationSize-totalIndividualsSelected)*100/paramPopulationSize) + "% of the population must be regenerated", getClass());
			
			for( AGenome genome : selectedGenomeWPopulation.keySet() ) {
				Set<AnIndividual> individuals = selectedGenomeWPopulation.get(genome);
				int targetSizeForGenome = paramPopulationSize/genome2algoInstance.size(); // TODO should be what ?
				int delta = targetSizeForGenome - individuals.size(); 

				if( delta>0 ) {
					messages.warnUser("Adding "+delta+" new individuals in the population for genome "+genome.name, getClass());

					Object[][] population = generateInitialPopulation(genome, delta);
					
					for( Object[] i : population ) {
						individuals.add(new AnIndividual(genome, i));
					}
				}
			}
		}
		
		exportContinuousOutput();
		
		return novelGenomeWPopulation;
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

		if( ourState!=ComputationState.FINISHED_OK )
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
