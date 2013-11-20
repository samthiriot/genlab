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
	
	public AGene(String name, double mutationProba) {
		this.name = name;
		this.mutationProba = mutationProba;
	}
	
	public abstract TypeName generateRandomnly(Uniform uniform);
	
	@Override
	public final String toString() {
		return name;
	}
	
	public final double getMutationProbability() {
		return mutationProba;
	}
	
	
	
}
