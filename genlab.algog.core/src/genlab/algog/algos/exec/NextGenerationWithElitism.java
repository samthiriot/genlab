package genlab.algog.algos.exec;

import genlab.algog.internal.AGenome;
import genlab.algog.internal.AnIndividual;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NextGenerationWithElitism implements INextGeneration {


	Map<AGenome,Set<AnIndividual>> selectedGenome2Population = new HashMap<AGenome, Set<AnIndividual>>();
	
	/**
	 * The set of individuals which should not be mutated,
	 * probably because this is an elitism algorithm
	 */
	Set<AnIndividual> individualsToProtect = new HashSet<AnIndividual>();
	
	private final int nbIndividuals;
	
	public NextGenerationWithElitism(int nbIndividuals) {
		this.nbIndividuals = nbIndividuals;
	}

	@Override
	public Map<AGenome, Set<AnIndividual>> getAllIndividuals() {
		return selectedGenome2Population;
	}

	@Override
	public Set<AnIndividual> getIndividualsToProtect() {
		// TODO
		return null;
	}
	
	public void addIndividual(AGenome genome, AnIndividual individual) {
		Set<AnIndividual> individuals = selectedGenome2Population.get(genome);
		if (individuals == null) {
			individuals = new HashSet<AnIndividual>(nbIndividuals);
			selectedGenome2Population.put(genome, individuals);
		}
		individuals.add(individual);
	}

	@Override
	public int getTotalOfIndividualsAllGenomes() {
		int total = 0;
		for (Set<AnIndividual> set : selectedGenome2Population.values()) {
			total += set.size();
		}
		return total;
	}

}
