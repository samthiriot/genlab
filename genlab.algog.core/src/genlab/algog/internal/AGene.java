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
	

	public static final Double INF = StrictMath.pow(10, 14);
	public static final Double EPS = StrictMath.pow(10, -14);
	/** value of distribution index for mutation */
	public final double eta_m;
	/** value of distribution index for crossover */
	public final double eta_c;
	
	public AGene(String name, double mutationProba, Double etam, Double etac) {
		this.name = name;
		this.mutationProba = mutationProba;
		this.eta_m = etam;
		this.eta_c = etac;
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
