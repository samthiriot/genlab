package genlab.algog.internal;

import cern.jet.random.Uniform;

/**
 * TODO  use a plugable random generator !
 * 
 * @author Samuel THiriot
 *
 * @param <TypeName>
 */
public abstract class ANumericGene<TypeName extends Number> extends AGene<TypeName> {

	public final TypeName min;
	public final TypeName max;
	
	public ANumericGene(String name, double mutationProba, TypeName min, TypeName max) {
		super(name, mutationProba);
		this.min = min;
		this.max = max;
	}
	
	public abstract TypeName generateRandomnly(Uniform uniform);
	

	
}
