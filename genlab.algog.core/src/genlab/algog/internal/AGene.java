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
	public TypeName value;
	
	public AGene(String name, double mutationProba, TypeName value) {
		this.name = name;
		this.mutationProba = mutationProba;
		this.value = value;
	}
	
	public abstract TypeName generateRandomnly(Uniform uniform);
	
	public abstract TypeName mutate(Uniform uniform, Object previousValue);

	@Override
	public final String toString() {
		return name;
	}
	
	public final double getMutationProbability() {
		return mutationProba;
	}
	
	
	
}
