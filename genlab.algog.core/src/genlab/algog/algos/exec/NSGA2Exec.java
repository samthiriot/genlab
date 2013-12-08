package genlab.algog.algos.exec;

import genlab.algog.algos.instance.GeneticExplorationAlgoContainerInstance;
import genlab.algog.algos.meta.NSGA2GeneticExplorationAlgo;
import genlab.algog.internal.AGenome;
import genlab.algog.internal.AnIndividual;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;

import java.util.ArrayList;
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
 * TODO changer taille offspring en paramètre
 * 
 * @author Samuel Thiriot
 */
public class NSGA2Exec extends GeneticExplorationMultiObjectiveAlgoExec {
	
	// variables used only during the selection of the next generation

	protected SortedMap<Integer,Collection<AnIndividual>> fronts = null;
	protected Map<AnIndividual,Integer> individual2rank = null;
	protected Map<AnIndividual, Double[]> indiv2fitness = null;
	

	// for each generation, and each individual, stores the corresponding fitness
	protected final LinkedHashMap<Integer,Collection<AnIndividual>> generation2paretoFront;

	
	public NSGA2Exec(IExecution exec,
			GeneticExplorationAlgoContainerInstance algoInst) {
		super(exec, algoInst);

		generation2paretoFront = new LinkedHashMap<Integer,Collection<AnIndividual>>(paramStopMaxIterations);;
	}
	

	/**
	 * returns true if the first individual Pareto-dominates the second one, meaning if for 
	 * any dimension, the fitness of the first individual is better (meaning: lower) than the second one.
	 * @param i1
	 * @param fit1
	 * @param i2
	 * @param fit2
	 * @return
	 */
	protected boolean dominates(Double[] fit1, Double[] fit2) {
		
		for (int i=0; i<fit1.length; i++) {
			
			if (fit1[i] > fit2[i])
				return false;
			
		}
		
		return true;
		
	}

	/**
	 * As described in NSGA-II
	 * @param indiv2fitness
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
		System.err.println("first domination front: "+currentFront.toString());
		// save the first domination front
		generation2paretoFront.put(iterationsMade, currentFront);
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
			System.err.println(frontIdx+"th domination front: "+nextFront.toString());
			
		}
		
		// we computed the fronts. God bless us.
		this.fronts = frontIdx2individuals;
		this.individual2rank = individual2rank;
		
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
			return Double.compare(indiv2fitness.get(o1)[m], indiv2fitness.get(o2)[m]);
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
		public int compare(AnIndividual i, AnIndividual j) {

			final Integer iRank = individual2rank.get(i);
			final Integer jRank = individual2rank.get(j);
		
			if (iRank < jRank)
				return -1;
			
			final Double iDistance = individual2distance.get(i);
			final Double jDistance = individual2distance.get(j);
			
			if ((iRank == jRank) && (iDistance > jDistance))
				return -1;
			
			return 1;
		}
		
	}
	
	
	protected Map<AnIndividual,Double> calculateCrowdingDistance(Collection<AnIndividual> pop, Map<AnIndividual, Double[]> indiv2fitness) {
		
		int l = pop.size();
		
		Map<AnIndividual,Double> individual2distance = new HashMap<AnIndividual, Double>(pop.size());
		
		int objectivesCount = indiv2fitness.values().iterator().next().length;
		
		// init distance
		for (AnIndividual i: pop)
			individual2distance.put(i,0d);
		
		List<AnIndividual> sortedPop = new ArrayList<AnIndividual>(pop);
		for (int m=0; m<objectivesCount; m++) {
			
			Collections.sort(sortedPop, new ComparatorFitness(m, indiv2fitness));
			
			final double minFitness = indiv2fitness.get(sortedPop.get(0))[m];
			final double maxFitness = indiv2fitness.get(sortedPop.get(l-1))[m];
			final double diffFitness = maxFitness - minFitness;
					
			individual2distance.put(sortedPop.get(0), Double.POSITIVE_INFINITY);
			individual2distance.put(sortedPop.get(l-1), Double.POSITIVE_INFINITY);
			
			for (int i=1; i<l-2; i++) {
				Double previousValue = individual2distance.get(sortedPop.get(i));
				Double nextValue = previousValue + 
									(
										indiv2fitness.get(sortedPop.get(i+1))[m] - indiv2fitness.get(sortedPop.get(i-1))[m]
									) / diffFitness	
									
									;
				individual2distance.put(sortedPop.get(i), nextValue);
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
			
			messages.debugUser("keeping as offsprings the "+front.size()+" individual of front "+frontIdx, getClass());

			// add all the fronts
			offsprings.addAll(front);
			
			lastFrontIdx++;
		}
		
		int remaining = parentsCountToSelect - offsprings.size();
		if (remaining > 0) {
			
			messages.debugUser("still have to select "+remaining+" offsprings; will select them from the front "+lastFrontIdx, getClass());
			
			// now complete with only a part of the last front
			
			List<AnIndividual> sortedFront = new ArrayList<AnIndividual>(fronts.get(lastFrontIdx));
			
			Map<AnIndividual,Double> individual2distance = calculateCrowdingDistance(sortedFront, indiv2fitness);
			
			Collections.sort(sortedFront, new ComparatorCrowded(individual2distance));
			
			// add the best ones based on the crowded operator
			for (int i=0; i<remaining; i++) {
				offsprings.add(sortedFront.get(i));
			}
			
		}
		
		
		return offsprings;
	}

	
	@Override
	protected Map<AGenome, Set<AnIndividual>> selectIndividuals(Map<AnIndividual, Double[]> indiv2fitness) {

		// we are called from the parent class with only the last generation.
		// but NSGA2, in order to introduce elitism, compares the current generation and the previous one (if any).
		// so we first preprocess the received fitness with the previous generation
		// also a good way to remove solutions at negative infinity (failure)
		this.indiv2fitness = new HashMap<AnIndividual, Double[]>(indiv2fitness.size()*2);
		
		/*for (Map.Entry<AnIndividual, Double[]> ind2fit : indiv2fitness.entrySet()) {
			for (Double f: ind2fit.getValue()) {
				if (f.isInfinite())
					return;
			}
		}*/
		this.indiv2fitness.putAll(indiv2fitness);	// the current generation
		{
			Map<AnIndividual, Double[]> previousGen = this.generation2fitness.get(this.iterationsMade-1);
			if (previousGen != null)
				this.indiv2fitness.putAll(previousGen); // the previous generation
		}
				
		
		// reset internal variables
		this.fronts = null;
		this.individual2rank = null;
		this.indiv2fitness = indiv2fitness;
		
		// compute fronts and rank
		fastNonDominatedSort(indiv2fitness);
		
		// select parents
		Set<AnIndividual> offsprings  = selectParents(indiv2fitness, indiv2fitness.size()/2);

		// now sort the population by genome, as expected by the parent algo
		HashMap<AGenome, Set<AnIndividual>> genome2individuals = new HashMap<AGenome, Set<AnIndividual>>();
		for (AnIndividual offspring: offsprings) {
			
			Set<AnIndividual> individuals = genome2individuals.get(offspring.genome);
			if (individuals == null) {
				individuals = new HashSet<AnIndividual>(indiv2fitness.size());
				genome2individuals.put(offspring.genome, individuals);
			}
			
			individuals.add(offspring);
			
		}
		
		// clear internal variables (free memory)
		this.fronts = null;
		this.individual2rank = null;
		this.indiv2fitness = null;
		
		return genome2individuals;
	}
	
	@Override
	protected void hookProcessResults(ComputationResult res, ComputationState ourState) {

		if (ourState != ComputationState.FINISHED_OK)
			return;
		
		
		final String titleIteration = "iteration";
		final String titleParetoGenome = "pareto genome";
		
		GenlabTable tab = new GenlabTable();
		tab.declareColumn(titleIteration);
		tab.declareColumn(titleParetoGenome);		
		
		// declare columns for each fitness
		Map<AGenome,String[]> genome2fitnessColumns = new HashMap<AGenome, String[]>(genome2fitnessOutput.size());
		for (AGenome currentGenome: genome2fitnessOutput.keySet()) {
		
			String[] names = new String[genome2fitnessOutput.get(currentGenome).size()];
			int j=0;
			for (IAlgoInstance goalAI: genome2fitnessOutput.get(currentGenome)) {
			
				names[j] = "fitness "+currentGenome.name+" / "+goalAI.getName();
				tab.declareColumn(names[j]);
				
				j++;
			}
			
			genome2fitnessColumns.put(currentGenome, names);
			
		}
		
		// declare columns for each possible gene
		Map<AGenome,String[]> genome2geneColumns = new HashMap<AGenome, String[]>(genome2fitnessOutput.size());
		for (AGenome currentGenome: genome2fitnessOutput.keySet()) {
			
			String[] names = new String[currentGenome.getGenes().length];
			for (int j=0; j<names.length; j++) {
			
				names[j] = "genes "+currentGenome.name+" / "+currentGenome.getGenes()[j].name;
				tab.declareColumn(names[j]);
		
			}
			
			genome2geneColumns.put(currentGenome, names);
			
		}
		
		for (Integer iterationId : generation2paretoFront.keySet()) {
			
			// for each iteration
			Collection<AnIndividual> individuals = generation2paretoFront.get(iterationId);
			Map<AnIndividual,Double[]> generationFitness = generation2fitness.get(iterationId);
			
			for (AnIndividual indiv : individuals) {
				
				int rowId = tab.addRow();
				Double[] fitness = generationFitness.get(indiv);

				tab.setValue(rowId, titleIteration, iterationId);
				tab.setValue(rowId, titleParetoGenome, indiv.genome.name);

				// export fitness
				String[] colnames = genome2fitnessColumns.get(indiv.genome);
				for (int i=0; i<colnames.length; i++) {
					
					tab.setValue(
							rowId, 
							colnames[i], 
							fitness[i]
							);

				}
				
				// export genes
				String[] genesNames = genome2geneColumns.get(indiv.genome);
				for (int i=0; i<genesNames.length; i++) {
				
					tab.setValue(
							rowId, 
							genesNames[i], 
							indiv.genes[i]
							);
				}
			}
			
		}
	
		res.setResult(NSGA2GeneticExplorationAlgo.OUTPUT_TABLE_PARETO, tab);
		
	}

}
