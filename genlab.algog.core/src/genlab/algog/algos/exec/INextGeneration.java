package genlab.algog.algos.exec;

import genlab.algog.internal.AGenome;
import genlab.algog.internal.AnIndividual;

import java.util.Map;
import java.util.Set;

public interface INextGeneration {
	
	public Map<AGenome,Set<AnIndividual>> getAllIndividuals();
	
	/**
	 * The set of individuals which should not be mutated,
	 * probably because this is an elitism algorithm
	 */
	public Set<AnIndividual> getIndividualsToProtect();
	
}
