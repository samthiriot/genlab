package genlab.algog.internal;

import cern.jet.random.Uniform;

/**
 * TODO  use a plugable random generator !
 * 
 * @author Samuel THiriot
 *
 * @param <TypeName>
 */
public abstract class AGene<TypeName> {

	public final String name;
	public final double mutationProba;
	/** value of distribution index for mutation */
	public final double eta_m = 20.0;
	/** value of distribution index for crossover */
	public final double eta_c = 20.0;
	
	public AGene(String name, double mutationProba) {
		this.name = name;
		this.mutationProba = mutationProba;
	}
	
	public abstract TypeName generateRandomnly(Uniform uniform);
	
	public abstract TypeName mutate(Uniform uniform, Object previousValue);
	
	public abstract TypeName[] crossoverSBX(Uniform U, Object geneA, Object geneB);

	@Override
	public final String toString() {
		return name;
	}
	
	public final double getMutationProbability() {
		return mutationProba;
	}
	
	
	
}
