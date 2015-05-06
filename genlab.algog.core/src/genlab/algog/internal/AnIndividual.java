package genlab.algog.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnIndividual implements Comparable<AnIndividual> {

	public final AGenome genome;
	public final Object[] genes;
//	public Double fitness;
	
	/**
	 * List of (fitness/target/value)
	 */
	private List<AFitnessBoard> fitnessBoard;
	private int rank;
	private int dominationCount;
	private Double crowdedDistance;
	
	public AnIndividual(AGenome genome, Object[] genes) {
		super();
		this.genome = genome;
		this.genes = genes;
		this.fitnessBoard = new ArrayList<AFitnessBoard>();
		this.rank = -1;
		this.dominationCount = -1;
		this.crowdedDistance = Double.POSITIVE_INFINITY;
	}

	@Override
	public int compareTo(AnIndividual arg0) {
		
		return crowdedDistance.compareTo(arg0.crowdedDistance);
	}
	
	public Boolean isFeasible() {
		
		for( AFitnessBoard fb : fitnessBoard ) {
			if( fb.getValue()==null )
				return false;
		}
		
		return true;
	}
	
	/*
	 * 
	 * 
	 * GETTERS
	 * & SETTERS
	 * 
	 * 
	 */

	public Double[] getFitnessFromFitnessBoard() {
		Double[] d = new Double[fitnessBoard.size()];
		for( int i=0 ; i<fitnessBoard.size() ; i++ ) {
			d[i] = fitnessBoard.get(i).getFitness();
		}
		
		return d;
	}
	
	public Object[] getTargetsFromFitnessBoard() {
		Object[] o = new Object[fitnessBoard.size()];
		for( int i=0 ; i<fitnessBoard.size() ; i++ ) {
			o[i] = fitnessBoard.get(i).getTarget();
		}
		
		return o;
	}
	
	public Object[] getValuesFromFitnessBoard() {
		Object[] o = new Object[fitnessBoard.size()];
		for( int i=0 ; i<fitnessBoard.size() ; i++ ) {
			o[i] = fitnessBoard.get(i).getValue();
		}
		
		return o;
	}

	/**
	 * @return the fitnessBoard
	 */
	public List<AFitnessBoard> getFitnessBoard() {
		return fitnessBoard;
	}

	/**
	 * @param fitnessBoard the fitnessBoard to add
	 */
	public void addFitnessBoard(AFitnessBoard fitnessBoard) {
		this.fitnessBoard.add(fitnessBoard);
	}

	/**
	 * @param fitnessBoard the fitnessBoard to set
	 */
	public void setFitnessBoard(List<AFitnessBoard> fitnessBoard) {
		this.fitnessBoard.clear();
		this.fitnessBoard.addAll(fitnessBoard);
	}
	
	/**
	 * @return the rank
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * @param rank the rank to set
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}

	/**
	 * @return the dominationCount
	 */
	public int getDominationCount() {
		return dominationCount;
	}

	/**
	 * @param dominationCount the dominationCount to set
	 */
	public void setDominationCount(int dominationCount) {
		this.dominationCount = dominationCount;
	}
	
	/**
	 * @return the crowdedDistance
	 */
	public Double getCrowdedDistance() {
		return crowdedDistance;
	}

	/**
	 * @param crowdedDistance the crowdedDistance to set
	 */
	public void setCrowdedDistance(Double crowdedDistance) {
		this.crowdedDistance = crowdedDistance;
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
		List<Double> list = new ArrayList<Double>(fitnessBoard.size());
		for( int i=0 ; i<fitnessBoard.size() ; i++ ) {
			list.add(fitnessBoard.get(i).getFitness());
		}
		return genome.readableValues(list.toArray());
	}
	
	public String targetsToString() {
		List<Object> list = new ArrayList<Object>(fitnessBoard.size());
		for( int i=0 ; i<fitnessBoard.size() ; i++ ) {
			list.add(fitnessBoard.get(i).getTarget());
		}
		return genome.readableValues(list.toArray());
	}
	
	public String valuesToString() {
		List<Object> list = new ArrayList<Object>(fitnessBoard.size());
		for( int i=0 ; i<fitnessBoard.size() ; i++ ) {
			list.add(fitnessBoard.get(i).getValue());
		}
		return genome.readableValues(list.toArray());
	}
}
