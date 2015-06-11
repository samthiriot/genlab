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
	
	
	public ANumericGene(String name, double mutationProba, TypeName min, TypeName max, Double etam, Double etac) {
		super(name, mutationProba, etam, etac);
		this.min = min;
		this.max = max;
	}
	
	/**
	 * given a random weight between 0 and 1 passed as parameter, does a crossover
	 * between this gene and another one.
	 * @param uniform
	 * @param weight
	 * @return
	 */
	public abstract TypeName generateRandomnly(Uniform uniform);
	
	public abstract TypeName mutate(Uniform uniform, Object previousValue);
	
	public abstract TypeName crossoverArithmetic(TypeName one, TypeName other, double weight);
	
	public abstract TypeName crossoverArithmetic(Object one, Object other, double weight);

	
}
