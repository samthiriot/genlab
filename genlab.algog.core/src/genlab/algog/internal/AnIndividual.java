package genlab.algog.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	
	public AnIndividual(AGenome genome, Object[] genes) {
		super();
		this.genome = genome;
		this.genes = genes;
		this.fitness = new Double[genome.getGenes().length];
		this.targets = new Object[genome.getGenes().length];
		this.values = new Object[genome.getGenes().length];
		this.rank = -1;
		this.crowdedDistance = -1d;
	}

	@Override
	public int compareTo(AnIndividual arg0) {
		
		return crowdedDistance.compareTo(arg0.crowdedDistance);
	}
	
	/**
	 * if one fitness isn't computed then this individual is not feasible
	 * @return true if feasible, false else
	 */
	public Boolean isFeasible() {
		
		for( Double fitness : this.fitness ) {
			if( fitness==null )
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
		return genome+" "+this.genesToString();
	}
	
	public String genesToString() {
		return genome.getGenes().toString();
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
