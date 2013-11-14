package genlab.algog.internal;

import cern.colt.Arrays;

public class AnIndividual {

	public final AGenome genome;
	public final Object[] genes;
	public Double fitness;
	
	public AnIndividual(AGenome genome, Object[] genes) {
		super();
		this.genome = genome;
		this.genes = genes;
	}
	
	@Override
	public String toString() {
		return genome+" "+Arrays.toString(genes);
	}
	
	
}
