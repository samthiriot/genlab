package genlab.algog.algos.exec;

import genlab.algog.algos.instance.GeneticExplorationAlgoContainerInstance;
import genlab.algog.algos.meta.GeneticExplorationAlgoConstants;
import genlab.algog.algos.meta.NSGA2GeneticExplorationAlgo;
import genlab.algog.internal.ADoubleGene;
import genlab.algog.internal.AGene;
import genlab.algog.internal.AGenome;
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
	protected Set<AnIndividual> individuals = null;
//	/** for the last generation evaluated, associates each individual with its rank */
//	protected Map<AnIndividual,Integer> individualWRank = null;
//	/** for the last generation, associates each individual with its fitness */
//	protected Map<AnIndividual, Double[]> individualWFitness_LastGenerations = null;
	/** for each generation, and each individual, stores the corresponding fitness */
	protected final LinkedHashMap<Integer,Set<AnIndividual>> generationWFirstPF;
	/** number of cuts to make on genes for the crossover operator */
	public static final int NCUTS = 2;

	protected AnIndividual ourBestFriend = null;
	
	/**
	 * Constructor
	 * @param exec
	 * @param algoInst
	 */
	public NSGA2Exec(IExecution exec, GeneticExplorationAlgoContainerInstance algoInst) {
		super(exec, algoInst);
		// initializes the map of, for each iteration, the first pareto front
		generationWFirstPF = new LinkedHashMap<Integer,Set<AnIndividual>>(paramStopMaxIterations);
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
			sb.append(i.toString()).append(" => ").append(i.fitnessToString()).append("\n");
		}
		
		return sb.toString();
	}
	
	/**
	 * first pass: discover for each individual how many individuals dominate it, and which children it dominates<br />
	 * then build domination fronts
	 * @param individualsWFitness
	 */
	protected void fastNonDominatedSort() {
		
		SortedMap<Integer,Collection<AnIndividual>> frontIndexWIndividuals = new TreeMap<Integer, Collection<AnIndividual>>();
//		Map<AnIndividual, Integer> individualWRank = new HashMap<AnIndividual, Integer>(individualsWFitness.size());
		Map<AnIndividual,Integer> individualWDominationCount = new HashMap<AnIndividual, Integer>(individuals.size());
		Map<AnIndividual,Set<AnIndividual>> individualWDominatedIndividuals = new HashMap<AnIndividual, Set<AnIndividual>>(individuals.size());
		Set<AnIndividual> individualsInCurrentFront = new HashSet<AnIndividual>();
		
		for( AnIndividual p : individuals ) {
			final Double[] pFitness = p.fitness;
			
			// don't even include individuals who have no fitness
			if( pFitness==null )
				continue; 
			
			int dominationCount = 0;
			Set<AnIndividual> dominatedIndividuals = new HashSet<AnIndividual>(individuals.size());
			
			for( AnIndividual q : individuals ) {
				final Double[] qFitness = q.fitness;

				// if p is feasible and q is not
				if( p.isFeasible() && !q.isFeasible() ) {
					dominatedIndividuals.add(q);
				}
				// if q is feasible and p is not
				else if( !p.isFeasible() && q.isFeasible() ) {
					dominationCount++;
				}
//				// if both are infeasible
//				else if( !p.isFeasible() && !q.isFeasible() ) {
//					// random choice
//					if( uniform.nextBoolean() ) {
//						dominatedIndividuals.add(q);
//					}else {
//						dominationCount++;
//					}
//				}
				// with fitness: p dominates q?
				else if( dominates(pFitness, qFitness) ) {
					dominatedIndividuals.add(q);
				}
				// with fitness: q dominates p?
				else if( dominates(qFitness, pFitness) ) {
					dominationCount++;
				}
			}
			
			individualWDominatedIndividuals.put(p, dominatedIndividuals);
			individualWDominationCount.put(p, dominationCount);

			// does this individual belong to the first front?
			if( dominationCount==0 ) {
				individualsInCurrentFront.add(p);
				p.rank = 1;
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
		Set<AnIndividual> nextFront = null;
		
		while( !individualsInCurrentFront.isEmpty() ) {
			nextFront = new HashSet<AnIndividual>();
			
			for( AnIndividual p : individualsInCurrentFront ) {
				for( AnIndividual q : individualWDominatedIndividuals.get(p) ) {
					Integer nq = individualWDominationCount.get(q) - 1;
					individualWDominationCount.put(q, nq);
					// if q belongs to the next front
					if( nq==0 ) {
						nextFront.add(q);
						q.rank = frontIndex + 1;
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
//		this.individualWRank = individualWRank;
		
		messages.infoUser("There are "+frontIndexWIndividuals.size()+" Pareto domination fronts: "+_message.toString(), getClass());
	}
	
	/**
	 * Compares two individuals based on the fitness computed
	 * @author Samuel Thiriot
	 */
	protected class ComparatorFitness implements Comparator<AnIndividual> {

		private final int m;
		private final List<AnIndividual> individuals;
		
		public ComparatorFitness(int m, List<AnIndividual> individuals) {
			this.m = m;
			this.individuals = individuals;
		}

		@Override
		public int compare(AnIndividual o1, AnIndividual o2) {
			
			final Double fitness1 = individuals.get( individuals.lastIndexOf(o1) ).fitness[m];
			final Double fitness2 = individuals.get( individuals.lastIndexOf(o2) ).fitness[m];
			return Double.compare(fitness1, fitness2);
		}
	}

	/**
	 * Compares two individuals based on the crowded stats
	 * @author Samuel Thiriot
	 */
	protected class ComparatorCrowded implements Comparator<AnIndividual> {

		private final List<AnIndividual> individuals;
		
		public ComparatorCrowded(List<AnIndividual> individuals) {
			this.individuals = individuals;
		}

		@Override
		public int compare(AnIndividual o1, AnIndividual o2) {
			
			final Double aDistance = individuals.get( individuals.lastIndexOf(o1) ).crowdedDistance;
			final Double bDistance = individuals.get( individuals.lastIndexOf(o2) ).crowdedDistance;
			return Double.compare(aDistance, bDistance);
		}
	}
	
	/**
	 * set distance population to 0, sort indivs by fitness, set distance indivs
	 * @param population
	 * @param individualWFitness
	 * @return
	 */
	protected void calculateCrowdingDistance(Collection<AnIndividual> population, Set<AnIndividual> inds) {
		
		List<AnIndividual> indivs = new ArrayList<AnIndividual>(inds);
		
		int l = population.size();
		int objectivesCount = indivs.get(0).fitness.length;
//		Map<AnIndividual,Double> individualWDistance = new HashMap<AnIndividual, Double>(population.size());
//		List<AnIndividual> sortedPop = new ArrayList<AnIndividual>(population);
		
		// set distance to 0
		for( AnIndividual i : population ) {
			i.crowdedDistance = 0d;
		}
		
		for (int m=0; m<objectivesCount; m++) {			
			Collections.sort(indivs, new ComparatorFitness(m, indivs));
			
			final double minFitness = indivs.get(0).fitness[m];//individualWFitness.get(sortedPop.get(0))[m];
			final double maxFitness = indivs.get(l-1).fitness[m];//individualWFitness.get(sortedPop.get(l-1))[m];
			final double diffFitness = maxFitness - minFitness;
			
			// ignore the individuals which were not evaluated (no data for comparison !)
			if (Double.isNaN(diffFitness))
				continue;

			indivs.get(0).crowdedDistance = Double.POSITIVE_INFINITY;
			indivs.get(l-1).crowdedDistance = Double.POSITIVE_INFINITY;
//			individualWDistance.put(sortedPop.get(0), Double.POSITIVE_INFINITY);
//			individualWDistance.put(sortedPop.get(l-1), Double.POSITIVE_INFINITY);
			
			for( int i=1 ; i<l-2 ; i++ ) {
				Double d = indivs.get(i).crowdedDistance;
				//Double d = individualWDistance.get(sortedPop.get(i));
				d += ( indivs.get(i+1).fitness[m] - indivs.get(i-1).fitness[m] ) / diffFitness;
				//d += (individualWFitness.get(sortedPop.get(i+1))[m] - individualWFitness.get(sortedPop.get(i-1))[m] ) / diffFitness;
				indivs.get(i).crowdedDistance = d;
				//individualWDistance.put(sortedPop.get(i), d);
			}
		}
	}

	/**
	 * Select individuals by their front ranking: P(t+1)
	 * @param individualWFitness
	 * @param parentsCountToSelect
	 * @return
	 */
	protected Set<AnIndividual> selectParents(int parentsCountToSelect) {
		
		// TODO manage the numerous genomes ! we have there no guarantee to keep all the genomes !
		Set<AnIndividual> offsprings = new HashSet<AnIndividual>(individuals.size());
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
				
				calculateCrowdingDistance(sortedFront, individuals);
				
				List<AnIndividual> t_inds = new ArrayList<AnIndividual>(individuals);
				Collections.sort(sortedFront, new ComparatorCrowded(t_inds));
				
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
	
	public Set<AnIndividual> getIndividualsForTwoLastGenerations() {
		Set<AnIndividual> result = new HashSet<AnIndividual>(parentGeneration.get(iterationsMade));
		
		if( offspringGeneration.get(iterationsMade)!=null )
			result.addAll(offspringGeneration.get(iterationsMade));
		
		return result;
	}
	
//	/**
//	 * Get individual and fitness for two last generations
//	 * @param iterationsMade
//	 * @return
//	 */
//	public Map<AnIndividual,Double[]> getIndividualWFitnessFor2LastGenerations(int iterationsMade) {
//		
//		Map<AnIndividual,Double[]> res = new HashMap<AnIndividual, Double[]>();
//		Map<AnIndividual,Double[]> previous = generation2fitness.get(iterationsMade-1);
//		
//		res.putAll(generation2fitness.get(iterationsMade));
//
//		if( previous!=null )
//			res.putAll(previous);
//		
//		return res;
//	}
//	
//	/**
//	 * Get individual and targets for two last generations
//	 * @param iterationsMade
//	 * @return
//	 */
//	public Map<AnIndividual,Object[]> getIndividualWTargetFor2LastGenerations(int iterationsMade) {
//		
//		Map<AnIndividual,Object[]> res = new HashMap<AnIndividual, Object[]>();
//		Map<AnIndividual,Object[]> previous = generation2targets.get(iterationsMade-1);
//		
//		res.putAll(generation2targets.get(iterationsMade));
//
//		if( previous!=null )
//			res.putAll(previous);
//		
//		return res;
//	}
//	
//	/**
//	 * Get individual and values for two last generations
//	 * @param iterationsMade
//	 * @return
//	 */
//	public Map<AnIndividual,Object[]> getIndividualWValuesFor2LastGenerations(int iterationsMade) {
//		
//		Map<AnIndividual,Object[]> res = new HashMap<AnIndividual, Object[]>();
//		Map<AnIndividual,Object[]> previous = generation2values.get(iterationsMade-1);
//		
//		res.putAll(generation2values.get(iterationsMade));
//
//		if( previous!=null )
//			res.putAll(previous);
//		
//		return res;
//	}
	
	/**
	 * We are called from the parent class with only the last generation.
	 * But NSGA2, in order to introduce elitism, compares the current generation and the previous one (if any).
	 * so we first preprocess the received fitness with the previous generation
	 * also a good way to remove solutions at negative infinity (failure)
	 * @param individualWFitness
	 */
	protected void analyzeLastPopulation() {

		this.individuals = getIndividualsForTwoLastGenerations();//getIndividualWFitnessFor2LastGenerations(iterationsMade);
		
//		for( AnIndividual i : individualWFitness_LastGenerations.keySet() ) {
//			System.out.println(""+i.toString()+" : "+Arrays.toString(individualWFitness_LastGenerations.get(i)));
//		}
		
//		if( this.individualWFitness_LastGenerations.size()>paramPopulationSize ) 
//			messages.infoUser("elitism : taking into account the results of the previous iteration "+(this.iterationsMade-1), getClass());
		
		// reset internal variables
		this.fronts = null;
//		this.individualWRank = null;
		
		// compute fronts and rank on P(t) U Q(t)
		fastNonDominatedSort();
	}
	
	/**
	 * generate P(t+1)
	 * @param individuals
	 * @return P(t+1)
	 */
	protected INextGeneration selectIndividuals() {
		// analyze the last run and the one before
//		analyzeLastPopulation();
		
		// select parents
		// TODO parameter: proportion of parents to select ? 
		Set<AnIndividual> offsprings = selectParents(paramPopulationSize);
		// now sort the population by genome, as expected by the parent algo
		NextGenerationWithElitism selectedIndividuals = new NextGenerationWithElitism(individuals.size());
		
		for( AnIndividual offspring : offsprings ) {
			selectedIndividuals.addIndividual(offspring.genome, offspring);
		}
		
		// clear internal variables (free memory)
		this.fronts = null;
//		this.individualWRank = null;
		this.individuals = null;
		
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
		tab.setTableMetaData(GeneticExplorationAlgoConstants.TABLE_METADATA_KEY_COLTITLE_ITERATION, titleIteration);
		tab.setTableMetaData(GeneticExplorationAlgoConstants.TABLE_METADATA_KEY_MAX_ITERATIONS, paramStopMaxIterations);
		
		tab.declareColumn(titleParetoGenome);		
		
		// declare columns for each fitness
		final Map<AGenome,String[]> genome2fitnessColumns = declareColumnsForGoals(tab);		
		// declare columns for each possible gene
		final Map<AGenome,String[]> genome2geneColumns = declareColumnsForGenes(tab);
				
		for( Integer iterationId : generationWFirstPF.keySet() ) {
			// for each iteration
			final Set<AnIndividual> indivs = generationWFirstPF.get(iterationId);
			
			storeIndividualsData(
				tab, 
				titleIteration, iterationId, titleParetoGenome, 
				genome2fitnessColumns, genome2geneColumns, 
				indivs
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
	protected void mutatePopulation(AGenome genome, Set<AnIndividual> novelPopulation, Map<AGene<?>,Integer> statsGeneWCountMutations) {
		
		int countMutations = 0;
		StringBuffer _message = new StringBuffer();
		
		for( AnIndividual i : novelPopulation ) {
			AGene<?>[] genes = genome.getGenes();

			for (int j=0; j<genes.length; j++) {
				if (uniform.nextDoubleFromTo(0.0, 1.0) <= genes[j].getMutationProbability()) {
					String debugIndivBefore = Arrays.toString(i.genes);
					
					i.genes[j] = genes[j].mutate(uniform, i.genes[j]);
					
					_message.append("\nMutate individual n°").append(i)
						.append(" from ").append(debugIndivBefore)
						.append(" to ").append(Arrays.toString(i.genes));
					
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
	protected final List<AnIndividual> crossoverNPoints(final AGenome genome, int nCuts, AnIndividual parent1, AnIndividual parent2) {
		
		List<AnIndividual> children = new ArrayList<AnIndividual>(2);
		AnIndividual child1 = new AnIndividual(genome, new Object[genome.getGenes().length]);//new Object[genome.getGenes().length];
		AnIndividual child2 = new AnIndividual(genome, new Object[genome.getGenes().length]);//new Object[genome.getGenes().length];
		
		int t = 0;
		int[] cuts = new int[nCuts];
		boolean crossoverApplied = uniform.nextBoolean();
		
		for( int i=0 ; i<nCuts ; i++ ) {
			cuts[i] = uniform.nextIntFromTo(0, genome.getGenes().length-1);
		}
		
		Arrays.sort(cuts);
		
		for( int i=0 ; i<genome.getGenes().length ; i++ ) {
			// if crossoverOn is true then first child genes are copied from parent2
			if( crossoverApplied ) {
				child1.genes[i] = parent2.genes[i];
				child2.genes[i] = parent1.genes[i];
			}
			// else first child genes are copied from parent1
			else {
				child1.genes[i] = parent1.genes[i];
				child2.genes[i] = parent2.genes[i];
			}
			
			if( t<cuts.length && cuts[t]==i ) {
				crossoverApplied = !crossoverApplied;
				t++;
				while( t<cuts.length && cuts[t]==i ) t++;
			}
		}
	
		children.add(child1);
		children.add(child2);
		
		return children;
	}

	/**
	 * Create two children by the N points crossover operator
	 * @param genome
	 * @param nCuts
	 * @param parent1
	 * @param parent2
	 * @return
	 */
	protected final List<AnIndividual> crossoverSBX(final AGenome genome, AnIndividual parent1, AnIndividual parent2) {
		
		List<AnIndividual> children = new ArrayList<AnIndividual>(2);
		AnIndividual child1 = new AnIndividual(genome, new Object[genome.getGenes().length]);//new Object[genome.getGenes().length];
		AnIndividual child2 = new AnIndividual(genome, new Object[genome.getGenes().length]);//new Object[genome.getGenes().length];
		
		for( int i=0 ; i<genome.getGenes().length ; i++ ) {
			if( uniform.nextDoubleFromTo(0, 1)<0.5 ) {
				Double geneP1 = (Double)parent1.genes[i];
				Double geneP2 = (Double)parent2.genes[i];
                ADoubleGene dg = (ADoubleGene)(genome.getGenes()[i]);
				double rand;
                double y1, y2, yl, yu;
                double c1, c2;
                double alpha, beta, betaq;

                if( StrictMath.abs(geneP1-geneP2)>Double.MIN_VALUE ) {
                    if( geneP1<geneP2 ) {
                        y1 = geneP1;
                        y2 = geneP2;
                    }else {
                        y1 = geneP2;
                        y2 = geneP1;
                    }
                    
                    yl = dg.min;
                    yu = dg.max;
                    rand = uniform.nextDoubleFromTo(0, 1);
                    beta = 1.0 + (2.0*(y1-yl)/(y2-y1));
                    alpha = 2.0 - StrictMath.pow( beta , -(dg.eta_c+1.0) );
                    
                    if( rand<=(1.0/alpha) ) {
                        betaq = StrictMath.pow( (rand*alpha) , (1.0/(dg.eta_c+1.0)) );
                    }else {
                        betaq = StrictMath.pow( (1.0/(2.0-rand*alpha)) , (1.0/(dg.eta_c+1.0)) );
                    }
                    
                    c1 = 0.5*((y1+y2)-betaq*(y2-y1));
                    beta = 1.0 + (2.0*(yu-y2)/(y2-y1));
                    alpha = 2.0 - StrictMath.pow( beta , -(dg.eta_c+1.0) );
                    
                    if( rand<=(1.0/alpha) ) {
                        betaq = StrictMath.pow( (rand*alpha) , (1.0/(dg.eta_c+1.0)) );
                    }else {
                        betaq = StrictMath.pow( (1.0/(2.0-rand*alpha)) , (1.0/(dg.eta_c+1.0)) );
                    }
                    
                    c2 = 0.5*((y1+y2)+betaq*(y2-y1));
                    
                    if( c1<yl ) c1 = yl;
                    if( c2<yl ) c2 = yl;
                    if( c1>yu ) c1 = yu;
                    if( c2>yu ) c2 = yu;
                    
                    if( uniform.nextDoubleFromTo(0, 1)<=0.5 ) {
                        child1.genes[i] = c2;
                        child2.genes[i] = c1;
                    }else {
                        child1.genes[i] = c1;
                        child2.genes[i] = c2;
                    }
                }else {
                    if( uniform.nextDoubleFromTo(0, 1)<=0.5 ) {
                        child1.genes[i] = geneP1;
                        child2.genes[i] = geneP2;
                    }else {
                        child1.genes[i] = geneP2;
                        child2.genes[i] = geneP1;
                    }
                }
			}else {
				child1.genes[i] = parent1.genes[i];
				child2.genes[i] = parent2.genes[i];
			}
		}
	
		children.add(child1);
		children.add(child2);
		
		return children;
	}
	
	/**
	 * Crowded Tournament Selection operator
	 * @param ind1
	 * @param ind2
	 * @param parents
	 * @return
	 */
	protected AnIndividual crowdedTournamentSelection(AnIndividual ind1, AnIndividual ind2, Set<AnIndividual> p) {
		
		List<AnIndividual> parents = new ArrayList<AnIndividual>(p);

		// if 1 is feasible and 2 is not
		if( ind1.isFeasible() && !ind2.isFeasible() ) {
			return ind1;
		}
		// if 2 is feasible and 1 is not
		else if( !ind1.isFeasible() && ind2.isFeasible() ) {
			return ind2;
		}
		// if both are infeasible
		else if( !ind1.isFeasible() && !ind2.isFeasible() ) {
			// random choice
			if( uniform.nextBoolean() ) {
				return ind1;
			}else {
				return ind2;
			}
		}
		
		int index1 = parents.lastIndexOf(ind1);
		int index2 = parents.lastIndexOf(ind2);
		
		// if 1 dominates 2
		if( dominates(parents.get(index1).fitness, parents.get(index2).fitness) ) {
			return ind1;
		}
		// else if 2 dominates 1
		else if( dominates(parents.get(index2).fitness, parents.get(index1).fitness) ) {
			return ind2;
		}
		// else if 1 is most spread than 2
		else if( parents.get(index1).crowdedDistance>parents.get(index2).crowdedDistance ) {
			return ind1;
		}
		// else if 2 is most spread than 1
		else if( parents.get(index2).crowdedDistance>parents.get(index1).crowdedDistance ) {
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
	 * @param parents
	 * @return
	 */
	protected Set<AnIndividual> generateNextGenerationWithCrossover(AGenome genome, Set<AnIndividual> parents, int populationSize) {
		
		Set<AnIndividual> offspring = new HashSet<AnIndividual>(populationSize);// new Object[populationSize][];
		int countCrossover = 0;		
		List<AnIndividual> selectedPopIndex = new LinkedList<AnIndividual>(parents);

		StringBuffer _message = new StringBuffer();
		
		while( offspring.size()<populationSize ) {
			// select 4 different index which will define the index of 4 individuals from the population
			int index1, index2, index3, index4;
			do {
				index1 = uniform.nextIntFromTo(0, selectedPopIndex.size()-1);
				index2 = uniform.nextIntFromTo(0, selectedPopIndex.size()-1);
				index3 = uniform.nextIntFromTo(0, selectedPopIndex.size()-1);
				index4 = uniform.nextIntFromTo(0, selectedPopIndex.size()-1);
			} while( (index1==index2) || (index1==index3) || (index1==index4) || (index2==index3) || (index2==index4) || (index3==index4) );// best optimization ever

			AnIndividual p1 = crowdedTournamentSelection(selectedPopIndex.get(index1), selectedPopIndex.get(index2), parents);
			AnIndividual p2 = crowdedTournamentSelection(selectedPopIndex.get(index3), selectedPopIndex.get(index4), parents);

			if( genome.crossoverProbability==1.0 || uniform.nextDoubleFromTo(0.0, 1.0)<=genome.crossoverProbability ) {
				// TODO use a parameter for crossover method
//				List<AnIndividual> novelIndividuals = crossoverNPoints(genome, NCUTS, p1, p2);
				List<AnIndividual> novelIndividuals = crossoverSBX(genome, p1, p2);
				countCrossover++;

				_message.append("\nCrossover between ")
					.append(Arrays.toString(p1.genes))
					.append(" and ")
					.append(Arrays.toString(p2.genes))
					.append(" : ");

				offspring.add(novelIndividuals.get(0));
				
				if( offspring.size()>populationSize ) 
					break;
				
				offspring.add(novelIndividuals.get(1));
				
				_message.append(Arrays.toString(novelIndividuals.get(0).genes)).append(" and ").append(Arrays.toString(novelIndividuals.get(1).genes));
			}else {
				offspring.add(new AnIndividual(p1));
				
				if( offspring.size()>populationSize ) 
					break;
				
				offspring.add(new AnIndividual(p2));
			}
		}
		
		messages.infoUser("Evolution ("+countCrossover+")"+_message.toString(), getClass());

		return offspring;
	}
	

	@Override
	protected Map<AGenome,Set<AnIndividual>> prepareNextGeneration() {
		
		exportContinuousOutput();

		String s = "So... at iteration "+iterationsMade+":\n";
		
		// get Q(t) and P(t)
		this.individuals = getIndividualsForTwoLastGenerations();
		// reset front
		this.fronts = null;

		s += "\nP(t) and Q(t)\n";
		s = "(pq0:"+individuals.size()+")"+s;
		for( AnIndividual i : individuals ) {
			s += i.toMiniString();
		}
		
		
		// build domination ranking through the two last generations: P(t) and Q(t)
		fastNonDominatedSort();
		
		s += "\nFirst Pareto Front\n";
		for( AnIndividual ind : generationWFirstPF.get(iterationsMade) ) {
			s += ind.toMiniString();
		}

		// P(t+1)
		INextGeneration selectedIndividuals = selectIndividuals();
		Map<AGenome,Set<AnIndividual>> selectedGenomeWPopulation = selectedIndividuals.getAllIndividuals();
		
		messages.infoUser("Selected for "+selectedGenomeWPopulation.size()+" genome(s) a total of "+selectedIndividuals.getTotalOfIndividualsAllGenomes()+" parents", getClass());
		
		Map<AGenome,Set<AnIndividual>> novelGenomeWPopulation = new HashMap<AGenome, Set<AnIndividual>>();
		// stats on the count of mutation (to be returned to the user)
		Map<AGene<?>,Integer> statsGeneWCountMutations = new HashMap<AGene<?>, Integer>();
		
		AGenome t_genome;

		// for each genome
		for( AGenome genome : selectedGenomeWPopulation.keySet() ) {t_genome=genome;
			// add parents to generation
//			List<AnIndividual> p = new ArrayList(selectedGenomeWPopulation.get(genome));
//			generation.put(iterationsMade+1, p);
			
			// select all individuals in P(t+1) with the right genome
			Set<AnIndividual> parents = new HashSet<AnIndividual>(selectedGenomeWPopulation.get(genome));
			
			parentGeneration.put(iterationsMade+1, parents);
			
			s += "\nP(t+1)\n";
			s = "(p1:"+parents.size()+")"+s;
			for( AnIndividual i : parents ) {
				s += i.toMiniString();
			}		

			// generate the next generation
			Set<AnIndividual> offspring = generateNextGenerationWithCrossover(
				genome, 
				parents, 
				paramPopulationSize
			);

			// mutate in this novel generation
			mutatePopulation(genome, offspring, statsGeneWCountMutations);

			// do not forget parents!
			//Set<AnIndividual> merged = new HashSet<AnIndividual>();//new Object[parents.size()+offspring.length][];
			s = "(off:"+offspring.size()+")"+"(pq1:"+(parents.size()+offspring.size())+")"+s;
			//merged.addAll(parents);
			//merged.addAll(offspring);
			
			//countPeople += merged.size();

			// store this novel generation
			novelGenomeWPopulation.put(genome, offspring);
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

		// TODO if the population is not big enough, recreate some novel individuals
//		if( countPeople<paramPopulationSize*2 ) {
//			
//			messages.warnUser( Math.round((paramPopulationSize-countPeople)*100/paramPopulationSize) + "% of the population must be regenerated", getClass());
//			
//			for( AGenome genome : selectedGenomeWPopulation.keySet() ) {
//				Set<AnIndividual> individuals = selectedGenomeWPopulation.get(genome);
//				int targetSizeForGenome = paramPopulationSize/genome2algoInstance.size(); // TODO should be what ?
//				int delta = targetSizeForGenome - individuals.size(); 
//
//				if( delta>0 ) {
//					messages.warnUser("Adding "+delta+" new individuals in the population for genome "+genome.name, getClass());
//
//					Set<AnIndividual> population = generateInitialPopulation(genome, delta);
//					
//					for( AnIndividual i : population ) {
//						individuals.add(i);
//					}
//				}
//				
//				Set<AnIndividual> merged = novelGenomeWPopulation.get(genome);
//				merged.addAll(individuals);
//
//				novelGenomeWPopulation.put(genome, merged);
//			}
//		}
		
		s += "\nP(t+1) and Q(t+1)\n";
		for( Set<AnIndividual> i : novelGenomeWPopulation.values() ) {
			for( AnIndividual j : i ) {
				s += j.toMiniString();
			}
		}
		
		messages.infoTech(s, getClass());
		
		
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
		analyzeLastPopulation();//generation.get(iterationsMade));

		// and define the result for Pareto
		res.setResult(
			NSGA2GeneticExplorationAlgo.OUTPUT_TABLE_PARETO, 
			packParetoFrontsAsTable()
		);
	}
}
