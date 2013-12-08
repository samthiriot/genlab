package genlab.algog.algos.exec;

import genlab.algog.algos.instance.GeneticExplorationAlgoContainerInstance;
import genlab.algog.internal.AGenome;
import genlab.algog.internal.AnIndividual;
import genlab.core.exec.IExecution;
import genlab.core.model.instance.IConnection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A mono objective genetic algorithm.
 * 
 * @author Samuel Thiriot
 *
 */
public class GeneticExplorationMonoObjectiveAlgoExec extends AbstractGeneticExplorationAlgoExec {

		
	public GeneticExplorationMonoObjectiveAlgoExec(
			IExecution exec, 
			GeneticExplorationAlgoContainerInstance algoInst) {
		
		super(exec, algoInst);
		
	}
	
	protected Double reduceFitness(Double[] fitness) {
		
		int count = 0;
		double total = 0.0;
		
		for (Double d: fitness) {
			
			total += d;
			count ++;
			
		}
		
		return total/(double)count;
	}
	
	protected Map<AnIndividual,Double> reduceFitness(Map<AnIndividual,Double[]> indiv2fitness) {
		
		Map<AnIndividual,Double> res = new HashMap<AnIndividual, Double>(indiv2fitness.size());
		
		for (AnIndividual i: indiv2fitness.keySet()) {
			
			res.put(
					i, 
					reduceFitness(indiv2fitness.get(i))
					);
		}
		
		return res;
	}
	
	protected Map<AGenome,Set<AnIndividual>> selectIndividuals(Map<AnIndividual,Double[]> indiv2fitnesses) {
		
		Map<AnIndividual,Double> indiv2fitness = reduceFitness(indiv2fitnesses);

		Map<AGenome,Set<AnIndividual>> selectedGenome2Population = new HashMap<AGenome, Set<AnIndividual>>();
		
		// copy in order
		double sumOfFitness = 0;
		for (Map.Entry<AnIndividual,Double> i2f : indiv2fitness.entrySet()) {
			sumOfFitness += i2f.getValue();
		}
		
		System.err.println("sum fitness: "+sumOfFitness);
		// TODO idée: dans notre cas, conserver les N meilleurs permettra d'éprouver leur efficacité malgré l'aspect random
		
		int toSelect = indiv2fitness.size()/2;
		while (toSelect > 0) {
			
			// run wheel !
			double r = uniform.nextDoubleFromTo(0, sumOfFitness);
			double localSum = 0;
			wheel: for (Map.Entry<AnIndividual,Double> i2f : indiv2fitness.entrySet()) {
				final AnIndividual individual = i2f.getKey();
				final Double fitness = i2f.getValue();
				localSum += fitness;
				if (localSum >= r) {
					//selected.add(i2f.getKey());
					
					Set<AnIndividual> indiv = selectedGenome2Population.get(individual.genome);
					if (indiv == null) {
						indiv = new HashSet<AnIndividual>(indiv2fitness.size());
						selectedGenome2Population.put(individual.genome, indiv);
					}
					if (indiv.add(individual)) {
						toSelect --;
						System.err.println("keeping: "+i2f.getKey()+", with fitness "+fitness);
					}
					break wheel;
				}
			}
			
		}		
		

		
		return selectedGenome2Population;

	}

	
	@Override
	protected void manageStatisticsForCurrentGeneration(Map<AnIndividual,Double[]> result2) {

		// display info on it
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		double total = 0.0;
		final int count = result2.size();
		
		// TODO avoid to do it twice...
		Map<AnIndividual,Double> result = reduceFitness(result2);
		
		AnIndividual best = null;
		
		for (Map.Entry<AnIndividual,Double> indiv2fitness : result.entrySet()) {
			
			double d = indiv2fitness.getValue();
			
			total += d;
			if (d < min) {
				min = d;
				best = indiv2fitness.getKey();
			}
			max = Math.max(d, max);
		
			System.err.println("raw fitness for "+Arrays.toString(indiv2fitness.getKey().genes)+": "+d);
		}
		
		// now reverse all the values so the genetic algo is attempting to maximize the fitness
		for (Map.Entry<AnIndividual,Double> indiv2fitness : result.entrySet()) {
		
			indiv2fitness.setValue(max-indiv2fitness.getValue()+1);
			
			
		}
		
		// recompute stats
		min = Double.MAX_VALUE;
		max = Double.MIN_VALUE;
		total = 0.0;
		for (Map.Entry<AnIndividual,Double> indiv2fitness : result.entrySet()) {
			
			double d = indiv2fitness.getValue();
			
			total += d;
			if (d < min) {
				min = d;
			}
			if (d > max) {
				max = d;
				best = indiv2fitness.getKey();
			}
			
		}
		double average = total / count;
		
		messages.infoUser("for generation "+iterationsMade+": best fitness "+max+", worst "+min+", average "+average+"; best individual "+best, getClass());
		//displayOnStream(System.out, gen, res)
		

		displayOnStream(System.err, iterationsMade);
	}


	@Override
	protected boolean hasConverged() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
