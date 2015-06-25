package genlab.algog.algos.exec;

import genlab.algog.algos.instance.GeneticExplorationAlgoContainerInstance;
import genlab.algog.algos.meta.ECrossoverMethod;
import genlab.algog.algos.meta.GeneticExplorationAlgoConstants;
import genlab.algog.algos.meta.NSGA2GeneticExplorationAlgo;
import genlab.algog.internal.ADoubleGene;
import genlab.algog.internal.AGene;
import genlab.algog.internal.AGenome;
import genlab.algog.internal.AnIndividual;
import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;

import java.awt.print.Paper;
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
	protected SortedMap<Integer,Set<AnIndividual>> fronts = null;
	protected Set<AnIndividual> pq_at_t0 = null;
	/** for each generation, stores the first pareto front */
	protected final LinkedHashMap<Integer,Set<AnIndividual>> generationWFirstPF;
	/** number of cuts to make on genes for the crossover operator */
	public static final int NCUTS = 2;
	/** have to be a power of 2, default value is TOURNAMENT_DEPTH=4 */
	public static final int TOURNAMENT_DEPTH = (int)StrictMath.pow(2, 2);

	public static final Double INF = StrictMath.pow(10, 14);
	public static final Double EPS = StrictMath.pow(10, -14);

	protected final ECrossoverMethod paramCrossover;
	
	/**
	 * Constructor
	 * @param exec
	 * @param algoInst
	 */
	public NSGA2Exec(IExecution exec, GeneticExplorationAlgoContainerInstance algoInst) {
		super(exec, algoInst);
		
		// initializes the map of, for each iteration, the first pareto front
		generationWFirstPF = new LinkedHashMap<Integer,Set<AnIndividual>>(paramStopMaxIterations);
		
		// load parameter
		final Integer idxParamCrossover = (Integer)algoInst.getValueForParameter(NSGA2GeneticExplorationAlgo.PARAM_CROSSOVER);
		paramCrossover = ECrossoverMethod.values()[idxParamCrossover];
	
		messages.infoUser("will use for crossover the operator "+paramCrossover.label, getClass());
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
		
		System.err.println("fastNonDominatedSort: in");
		SortedMap<Integer,Set<AnIndividual>> frontIndexWIndividuals = new TreeMap<Integer, Set<AnIndividual>>();
		Map<AnIndividual,Integer> individualWDominationCount = new HashMap<AnIndividual, Integer>(pq_at_t0.size());
		Map<AnIndividual,Set<AnIndividual>> individualWDominatedIndividuals = new HashMap<AnIndividual, Set<AnIndividual>>(pq_at_t0.size());
		Set<AnIndividual> individualsInCurrentFront = new HashSet<AnIndividual>();
		
		for( AnIndividual p : pq_at_t0 ) {
			final Double[] pFitness = p.fitness;
			
			int dominationCount = 0;
			Set<AnIndividual> dominatedIndividuals = new HashSet<AnIndividual>(pq_at_t0.size());
			
			for( AnIndividual q : pq_at_t0 ) {
				final Double[] qFitness = q.fitness;

				// if p and q are not feasible
				if( !p.isFeasible() && !q.isFeasible() ) {
					dominatedIndividuals.add(q);
				}
				// if p is feasible and q is not
				else if( p.isFeasible() && !q.isFeasible() ) {
					dominatedIndividuals.add(q);
				}
				// if q is feasible and p is not
				else if( !p.isFeasible() && q.isFeasible() ) {
					dominationCount++;
				}
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
			}
		}
		
		// we don't always compute the fronts, but when we do: brace yourself
		this.fronts = frontIndexWIndividuals;
		
		System.err.println("fastNonDominatedSort: out");
	}
	
	/**
	 * Compares two individuals based on the fitness computed
	 * @author Samuel Thiriot
	 */
	protected class ComparatorFitness implements Comparator<AnIndividual> {

		private final int m;
		
		public ComparatorFitness(int m) {
			this.m = m;
		}

		@Override
		public int compare(AnIndividual o1, AnIndividual o2) {
			
			final Double fitness1 = o1.fitness[m];
			final Double fitness2 = o2.fitness[m];
			if (fitness1 == null || fitness2 == null)
				throw new ProgramException("trying to compare the fitness of "+o1+" and "+o2+" but it is null");
			return Double.compare(fitness1, fitness2);
		}
	}
	
	/**
	 * Compares two individuals based on the genes
	 * @author Alan Solon
	 */
	protected class ComparatorGenes implements Comparator<AnIndividual> {

		private final int g;
		
		public ComparatorGenes(int g) {
			this.g = g;
		}

		@Override
		public int compare(AnIndividual o1, AnIndividual o2) {
			
			final Number a = (Number)o1.genes[g];
			final Number b = (Number)o2.genes[g];
			return Double.compare(a.doubleValue(), b.doubleValue());
		}
	}

	/**
	 * Compares two individuals based on the crowded stats
	 * @author Samuel Thiriot
	 */
	protected class ComparatorCrowded implements Comparator<AnIndividual> {

		@Override
		public int compare(AnIndividual o1, AnIndividual o2) {
			
			return Double.compare(o2.crowdDistance, o1.crowdDistance); // and in this order!
			
		}
	}

	/**
	 * Compares two individuals based on n1
	 * @author Alan Solon
	 */
	protected class ComparatorN1 implements Comparator<AnIndividual> {
		
		@Override
		public int compare(AnIndividual o1, AnIndividual o2) {
			
			return Double.compare(o1.crowdDistance, o2.crowdDistance);
		}
	}

	/**
	 * Compares two individuals based on n2
	 * @author Alan Solon
	 */
	protected class ComparatorN2 implements Comparator<AnIndividual> {

		
		@Override
		public int compare(AnIndividual o1, AnIndividual o2) {
			
			return Double.compare(o2.centerDistance, o1.centerDistance);
		}
	}
	
	/**
	 * set distance population to 0, sort indivs by fitness, set distance indivs
	 * @param population
	 * @param individualWFitness
	 * @return
	 */
	protected void calculateCrowdingDistance(Set<AnIndividual> population) {
		System.err.println("calculateCrowdingDistance: in");

		List<AnIndividual> indivs = new ArrayList<AnIndividual>(population);
		
		int l = population.size();
		int objectivesCount = indivs.get(0).fitness.length;

		// set distance to 0
		for( AnIndividual i : indivs ) {
			i.crowdDistance = 0d;
		}
		
		for (int m=0; m<objectivesCount; m++) {			
			Collections.sort(indivs, new ComparatorFitness(m));
			
			final double minFitness = indivs.get(0).fitness[m];
			final double maxFitness = indivs.get(l-1).fitness[m];
			final double diffFitness = maxFitness - minFitness;
			
			// ignore the individuals which were not evaluated (no data for comparison !)
			if (Double.isNaN(diffFitness))
				continue;

			indivs.get(0).crowdDistance = INF;
			indivs.get(l-1).crowdDistance = INF;
			
			for( int i=1 ; i<l-1 ; i++ ) {
				Double d = indivs.get(i).crowdDistance;
				d += ( indivs.get(i+1).fitness[m] - indivs.get(i-1).fitness[m] ) / diffFitness;
				indivs.get(i).crowdDistance = d;
			}
		}
		System.err.println("calculateCrowdingDistance: out");

	}
	
	protected Set<AnIndividual> crowdingDistanceByFitness(int howMany, List<AnIndividual> front) {
		System.err.println("crowdingDistanceByFitness: in");
		while( front.size()>howMany ) {
			int l = front.size();
			for( AnIndividual i : front ) {
				i.crowdDistance = 0d;
				i.centerDistance = 0d;
			}
			for( int m=0 ; m<front.get(0).fitness.length ; m++ ) {
				Collections.sort(front, new ComparatorFitness(m));

				final double delta = front.get(l-1).fitness[m] - front.get(0).fitness[m];
	
				front.get(0).crowdDistance = INF;
				front.get(l-1).crowdDistance = INF;

				front.get(0).centerDistance += Math.abs( front.get(0).fitness[m]-front.get(l-1).fitness[m] ) / delta;
				front.get(l-1).centerDistance += Math.abs( front.get(0).fitness[m]-front.get(l-1).fitness[m] ) / delta;
	
				for( int i=1 ; i<l-1 ; i++ ) {
					front.get(i).crowdDistance += Math.abs( front.get(i-1).fitness[m]-front.get(i+1).fitness[m] ) / delta;
					
					// 0 farther from i than n
					if( Math.abs(front.get(i).fitness[m]-front.get(0).fitness[m]) > Math.abs(front.get(l-1).fitness[m]-front.get(i).fitness[m]) ) {
						front.get(i).centerDistance += Math.abs( front.get(i).fitness[m]-front.get(0).fitness[m] ) / delta;
					}else {
						front.get(i).centerDistance += Math.abs( front.get(l-1).fitness[m]-front.get(i).fitness[m] ) / delta;
					}
				}
			}
			
			// call all individual who got the same minimum n1
			List<AnIndividual> er = new ArrayList<AnIndividual>();
			Collections.sort(front, new ComparatorN1());
			Double n1 = front.get(0).crowdDistance;
			for( AnIndividual i : front ) {
				if( i.crowdDistance-n1<EPS ) {
					er.add(i);
				}else {
					break;
				}
			}
			
			// among them, select the best n2
			Collections.sort(er, new ComparatorN2());
			front.remove( er.get(0) );
		}
		System.err.println("crowdingDistanceByFitness: out");
		return new HashSet<AnIndividual>(front);
	}
	
	protected Set<AnIndividual> crowdingDistanceByGenes(int howMany, List<AnIndividual> front) {
		System.err.println("crowdingDistanceByGenes: in");
		while( front.size()>howMany ) {
			int l = front.size();
			for( AnIndividual i : front ) {
				i.crowdDistance = 0d;
				i.centerDistance = 0d;
			}
			for( int g=0 ; g<front.get(0).genes.length ; g++ ) {
				Collections.sort(front, new ComparatorGenes(g));

				double z = ((Number)(front.get(l-1).genes[g])).doubleValue();
				double a = ((Number)(front.get(0).genes[g])).doubleValue();
				final double delta =  z - a;
	
				front.get(0).crowdDistance = INF;
				front.get(l-1).crowdDistance = INF;

				front.get(0).centerDistance += Math.abs( a-z ) / delta;
				front.get(l-1).centerDistance += Math.abs( a-z ) / delta;
	
				for( int i=1 ; i<l-1 ; i++ ) {
					double m = ((Number)(front.get(i-1).genes[g])).doubleValue();
					double o = ((Number)(front.get(i).genes[g])).doubleValue();
					double n = ((Number)(front.get(i+1).genes[g])).doubleValue();
					front.get(i).crowdDistance += Math.abs( m-n ) / delta;
					
					// 0 farther from i than n
					if( Math.abs(o-a) > Math.abs(z-o) ) {
						front.get(i).centerDistance += Math.abs( o-a ) / delta;
					}else {
						front.get(i).centerDistance += Math.abs( z-o ) / delta;
					}
				}
			}
			
			// call all individual who got the same minimum n1
			List<AnIndividual> er = new ArrayList<AnIndividual>();
			Collections.sort(front, new ComparatorN1());
			Double n1 = front.get(0).crowdDistance;
			for( AnIndividual i : front ) {
				if( i.crowdDistance-n1<EPS ) {
					er.add(i);
				}else {
					break;
				}
			}
			
			// among them, select the best n2
			Collections.sort(er, new ComparatorN2());
			front.remove( er.get(0) );
		}
		
		System.err.println("crowdingDistanceByGenes: out");

		return new HashSet<AnIndividual>(front);
	}

	/**
	 * Select individuals by their front ranking: P(t+1)
	 * @param individualWFitness
	 * @param parentsCountToSelect
	 * @return
	 */
	protected Set<AnIndividual> selectParents() {
		
		// TODO manage the numerous genomes ! we have there no guarantee to keep all the genomes !
		Set<AnIndividual> p_at_t1 = new HashSet<AnIndividual>(paramPopulationSize);
		int lastFrontIndex = 1;
		
		// first add as many entire fronts as possible
		for( Integer frontIdx : fronts.keySet() ) {
			Set<AnIndividual> front = fronts.get(frontIdx);
			
			// and then we can compute the new crowding distance
			calculateCrowdingDistance(front);
			
			// if we selected enough fronts
			if (p_at_t1.size() + front.size() > paramPopulationSize)
				break;
			
			messages.infoUser("Keeping (as offspring) the "+front.size()+" individuals of front "+frontIdx, getClass());
						
			// add all the fronts
			p_at_t1.addAll(new HashSet<AnIndividual>(front));
			
			lastFrontIndex++;
		}

		if( p_at_t1.size()<paramPopulationSize ) {
			List<AnIndividual> front = new ArrayList<AnIndividual>(fronts.get(lastFrontIndex));
			if( Math.abs(front.get(0).fitness[0]-INF)<EPS ) {
				p_at_t1.addAll( crowdingDistanceByGenes( (paramPopulationSize-p_at_t1.size()) , front ) );
			}
			else {
				p_at_t1.addAll( crowdingDistanceByFitness( (paramPopulationSize-p_at_t1.size()) , front ) );
			}
		}
		
//		if( p_at_t1.size()<paramPopulationSize ) {
//			messages.infoUser("Add "+(paramPopulationSize-p_at_t1.size())+" individuals from the "+lastFrontIndex+". front", getClass());
//			List<AnIndividual> sortedFront = new ArrayList<AnIndividual>(fronts.get(lastFrontIndex));			
//			Collections.sort(sortedFront, new ComparatorCrowded(sortedFront));
//			p_at_t1.addAll( sortedFront.subList( 0 , (paramPopulationSize-p_at_t1.size()) ) );
//		}
		
		if( p_at_t1.size()<paramPopulationSize ) {
			messages.infoUser("We were not able to select enough individuals from Q(t) and P(t): selected "+p_at_t1.size()+" for "+paramPopulationSize+" expected", getClass());
		}
		
		return p_at_t1;
	}
	
	private void doubleCheckRegressions() {
		
		Set<AnIndividual> iterationBefore = generationWFirstPF.get(iterationsMade-1);
		Set<AnIndividual> iterationLast = generationWFirstPF.get(iterationsMade);
		
		String s = "";
		int i = 0;
		
		if (iterationBefore == null || iterationLast == null)
			return;
		
		HashSet<AnIndividual> individualsLost = new HashSet<AnIndividual>(iterationBefore);
		individualsLost.removeAll(iterationLast);
		
		for (AnIndividual individualLost : individualsLost) {
			// analyze why we lost this individual			
			// build the set of individuals which are dominating this lost individual
			Set<AnIndividual> individualsDominatingLostGuy = new HashSet<AnIndividual>();
			for (AnIndividual individualsNew : iterationLast) {
				if (dominates(individualsNew.fitness, individualLost.fitness)) {
					individualsDominatingLostGuy.add(individualsNew);
				}
			}
			if (individualsDominatingLostGuy.size() == 0) {
				s += individualLost+"\n";
				i++;
			}
		}
		
		if( s.length()>0 )
			messages.errorTech("We lost "+i+" individual(s) even if they were not dominated :-("+s, getClass());
		
	}
	
	public Set<AnIndividual> getIndividualsForTwoLastGenerations() {
		Set<AnIndividual> result = new HashSet<AnIndividual>(parentGeneration.get(iterationsMade));
		
		if( offspringGeneration.get(iterationsMade)!=null )
			result.addAll(offspringGeneration.get(iterationsMade));
		
		return result;
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
			String debugIndivBefore = Arrays.toString(i.genes);

			for (int j=0; j<genes.length; j++) {
				if (uniform.nextDoubleFromTo(0.0, 1.0) <= genes[j].getMutationProbability()) {
					
					i.genes[j] = genes[j].mutate(uniform, i.genes[j]);
					
					// stats on mutation
					Integer count = statsGeneWCountMutations.get(genes[j]);
					
					if( count==null ) {
						count = 0;
					}
					
					statsGeneWCountMutations.put(genes[j], count+1);
					countMutations++;
				}
			}
			
			_message.append("\nMutate individual n°").append(i.hashCode())
				.append(" from ").append(debugIndivBefore)
				.append(" to ").append(Arrays.toString(i.genes));
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
		Object[] g1 = new Object[genome.getGenes().length];
		Object[] g2 = new Object[genome.getGenes().length];
		
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
				g1[i] = parent2.genes[i];
				g2[i] = parent1.genes[i];
			}
			// else first child genes are copied from parent1
			else {
				g1[i] = parent1.genes[i];
				g2[i] = parent2.genes[i];
			}
			
			if( t<cuts.length && cuts[t]==i ) {
				crossoverApplied = !crossoverApplied;
				t++;
				while( t<cuts.length && cuts[t]==i ) t++;
			}
		}
	
		children.add(new AnIndividual(genome, g1));
		children.add(new AnIndividual(genome, g2));
		
		return children;
	}

	/**
	 * Create two children by the SBX crossover operator
	 * @param genome
	 * @param nCuts
	 * @param parent1
	 * @param parent2
	 * @return
	 */
	protected final List<AnIndividual> crossoverSBX(final AGenome genome, AnIndividual parent1, AnIndividual parent2) {
		
		List<AnIndividual> children = new ArrayList<AnIndividual>(2);
		Object[] g1 = new Object[genome.getGenes().length];
		Object[] g2 = new Object[genome.getGenes().length];
		
		for( int i=0 ; i<genome.getGenes().length ; i++ ) {
			Object[] genes = genome.getGenes()[i].crossoverSBX(uniform, parent1.genes[i], parent2.genes[i]);
			g1[i] = genes[0];
			g2[i] = genes[1];
		}
	
		children.add(new AnIndividual(genome, g1));
		children.add(new AnIndividual(genome, g2));
		
		return children;
	}
	
	/**
	 * Crowded Tournament Selection operator
	 * @param ind1
	 * @param ind2
	 * @param parents
	 * @return
	 */
	protected AnIndividual crowdedTournamentSelection(AnIndividual ind1, AnIndividual ind2) {
		
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
		
		// if 1 dominates 2
		if( dominates(ind1.fitness, ind2.fitness) ) {
			return ind1;
		}
		// else if 2 dominates 1
		else if( dominates(ind2.fitness, ind1.fitness) ) {
			return ind2;
		}
		// else if 1 is most spread than 2
		else if( ind1.crowdDistance>ind2.crowdDistance ) {
			return ind1;
		}
		// else if 2 is most spread than 1
		else if( ind2.crowdDistance>ind1.crowdDistance ) {
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
	 * Binary tournament base on the crowded tournament operator
	 * @return two best individuals
	 */
	public List<AnIndividual> recursiveCrowdedTournamentSelection(List<AnIndividual> p) {
		if( p.size()>2 ) {
			List<AnIndividual> a = new ArrayList<AnIndividual>();
			for( int i=0 ; i<p.size()/2 ; i++ ) {
				a.add( crowdedTournamentSelection(p.get( 2*i ), p.get( 2*i+1 )) );
			}
			return recursiveCrowdedTournamentSelection(a);
		}else {
			return p;
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
		
		Set<AnIndividual> offspring = new HashSet<AnIndividual>(populationSize);
		int countCrossover = 0;		
		List<AnIndividual> listOfParents = new LinkedList<AnIndividual>(parents);
		Collections.shuffle(listOfParents);

		StringBuffer _message = new StringBuffer();
		
		while( offspring.size()<populationSize ) {
			
			int indexFrom = uniform.nextIntFromTo(0, populationSize-TOURNAMENT_DEPTH);
			int indexTo = indexFrom + TOURNAMENT_DEPTH;
			List<AnIndividual> subList = listOfParents.subList(indexFrom, indexTo);
			
			List<AnIndividual> twoParents = recursiveCrowdedTournamentSelection(subList);

			AnIndividual p1 = twoParents.get(0);
			AnIndividual p2 = twoParents.get(1);

			if( genome.crossoverProbability==1.0 || uniform.nextDoubleFromTo(0.0, 1.0)<=genome.crossoverProbability ) {
				
				List<AnIndividual> novelIndividuals = null;
				switch (paramCrossover) {
				case N_POINTS:
					novelIndividuals = crossoverNPoints(genome, NCUTS, p1, p2);
					break;
				case SBX:
					novelIndividuals = crossoverSBX(genome, p1, p2);
					break;
				default:
					throw new ProgramException("unknown crossover method: "+paramCrossover);
				}
				
				countCrossover++;

				_message.append("\nCrossover between ")
					.append(p1)
					.append(" and ")
					.append(p2)
					.append(" : ");

				offspring.add(new AnIndividual(novelIndividuals.get(0)));
				
				if( offspring.size()>populationSize ) 
					break;

				offspring.add(new AnIndividual(novelIndividuals.get(1)));
				
				_message.append(novelIndividuals.get(0)).append(" and ").append(novelIndividuals.get(1));
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
	
	/**
	 * Send statistics
	 */
	public void analyzeGeneration() {
		
		StringBuffer msg = new StringBuffer("Statistics (for generation "+iterationsMade+"):");
		int n = pq_at_t0.size();
		int f = fronts.size();
		int p = fronts.get(1).size();
		int g = 0;
		
		for( AnIndividual ind : pq_at_t0 ) {
			if( ind.isFeasible() ) g++;
		}

		msg
		.append("\n"+n+" individual(s) in the population")
		.append("\n"+g+" individual(s) have been evaluated with success")
		.append("\n"+f+" front(s) have been computed")
		.append("\n"+p+" individual(s) are in the first Pareto front\n")
		;
		for( Integer i : fronts.keySet() ) {
			msg.append("\nFront "+i+"("+fronts.get(i).size()+"):\n");
			for( AnIndividual a : fronts.get(i) ) {
				msg.append(" -- "+a.toString());
			}
		}

		messages.infoUser(msg.toString(), getClass());
	}
	

	@Override
	protected Map<AGenome,Set<AnIndividual>> prepareNextGeneration() {
		
		exportContinuousOutput();
		
		/*
		 * Retrieving Q(t) and P(t)
		 */
		this.pq_at_t0 = new HashSet<AnIndividual>(getIndividualsForTwoLastGenerations());
		// reset front
		this.fronts = null;
		
		/*
		 * Build domination ranking through P(t) and Q(t)
		 */
		fastNonDominatedSort();
		
		doubleCheckRegressions();
		
		analyzeGeneration();

		/*
		 * P(t+1)
		 * Create the new parent population by selection of the best one amongst Q(t) and P(t)
		 */
		Set<AnIndividual> p_at_t1 = selectParents();
		parentGeneration.put(iterationsMade+1, p_at_t1);	
		
		/*
		 * Sort the population by genome
		 */
		NextGenerationWithElitism selectedIndividuals = new NextGenerationWithElitism(pq_at_t0.size());
		
		for( AnIndividual p : p_at_t1 ) {
			selectedIndividuals.addIndividual(p.genome, p);
		}
		
		// reset fronts
		this.fronts = null;
		// reset individuals
		this.pq_at_t0 = null;
		
		Map<AGenome,Set<AnIndividual>> selectedGenomeWPopulation = selectedIndividuals.getAllIndividuals();
		
		messages.infoUser("Selected for "+selectedGenomeWPopulation.size()+" genome(s) a total of "+selectedIndividuals.getTotalOfIndividualsAllGenomes()+" parents", getClass());
		
		Map<AGenome,Set<AnIndividual>> novelGenomeWPopulation = new HashMap<AGenome, Set<AnIndividual>>();
		// stats on the count of mutation (to be returned to the user)
		Map<AGene<?>,Integer> statsGeneWCountMutations = new HashMap<AGene<?>, Integer>();
		
		/*
		 * For each genome
		 */
		for( AGenome genome : selectedGenomeWPopulation.keySet() ) {
			/*
			 * Select all individuals in P(t+1) with the right genome
			 */
			Set<AnIndividual> parents = new HashSet<AnIndividual>(selectedGenomeWPopulation.get(genome));

			/*
			 * Generate the offspring using selection/crossover and mutation
			 */
			Set<AnIndividual> offspring = generateNextGenerationWithCrossover(
				genome, 
				parents, 
				paramPopulationSize
			);

			// mutate in this novel generation
			mutatePopulation(genome, offspring, statsGeneWCountMutations);

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
		this.pq_at_t0 = getIndividualsForTwoLastGenerations();		
		// reset internal variables
		this.fronts = null;		
		// compute fronts and rank on P(t) U Q(t)
		fastNonDominatedSort();

		// and define the result for Pareto
		res.setResult(
			NSGA2GeneticExplorationAlgo.OUTPUT_TABLE_PARETO, 
			packParetoFrontsAsTable()
		);
	}
}
