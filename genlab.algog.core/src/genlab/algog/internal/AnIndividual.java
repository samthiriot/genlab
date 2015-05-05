package genlab.algog.internal;

public class AnIndividual implements Comparable<AnIndividual> {

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
		return genome+" "+genome.readableValues(genes);
	}

	@Override
	public int compareTo(AnIndividual arg0) {
		
		return fitness.compareTo(arg0.fitness);
	}
}
