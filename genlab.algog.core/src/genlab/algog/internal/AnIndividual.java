package genlab.algog.internal;

import java.util.Arrays;


public class AnIndividual implements Comparable<AnIndividual> {

	/** Definition of the genome */
	public final AGenome genome;
	/** Values for each gene defined */
	public final Object[] genes;
	/** Fitness for each objective defined */
	public final Double[] fitness;
	/** Target values to reach for each objective defined */
	public final Object[] targets;
	/** Actual value for each objective defined */
	public final Object[] values;
	/** Pareto front ranking */
	public int rank;
	/** Crowded distance */
	public Double crowdedDistance;
	
	public static int lastId = 1;
	
	public final int id;
	
	public AnIndividual(AGenome genome, Object[] genes) {
		super();
		
		this.id = lastId++;
		
		this.genome = genome;
		this.genes = genes;
		this.fitness = new Double[genome.getGenes().length];
		this.targets = new Object[genome.getGenes().length];
		this.values = new Object[genome.getGenes().length];
		this.rank = Integer.MAX_VALUE;
		this.crowdedDistance = -1d;
	}
	
	public AnIndividual(AnIndividual ind) {
		this(ind.genome, ind.genes);
	}

	@Override
	public int compareTo(AnIndividual arg0) {
		
		return crowdedDistance.compareTo(arg0.crowdedDistance);
	}
	
	/**
	 * if one value isn't computed then this individual is not feasible
	 * @return true if feasible, false else
	 */
	public Boolean isFeasible() {
		
		for( Object value : this.values ) {
			if( value==null )
				return false;
		}
		
		return true;
	}

	/*
	 * 
	 * 
	 * toString
	 * 
	 * 
	 */
	
	@Override
	public String toString() {
		return "["+id+"] "+genome+" "+this.genesToString()+" => "+valuesToString();
	}
	
	public String toMiniString() {
		return "ID "+hashCode()
				+", GENES "+Arrays.toString(genes)
				+", VALUES "+Arrays.toString(values)
				+", TARGETS "+Arrays.toString(targets)
				+", FITNESS "+Arrays.toString(fitness);
	}
	
	public String genesToString() {
		return genome.readableValues(this.genes);
	}
	
	public String fitnessToString() {
		return genome.readableValues( this.fitness );
	}
	
	public String targetsToString() {
		return genome.readableValues( this.targets );
	}
	
	public String valuesToString() {
		return genome.readableValues( this.values );
	}
}
