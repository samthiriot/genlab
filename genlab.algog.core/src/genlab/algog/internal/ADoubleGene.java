package genlab.algog.internal;

import cern.jet.random.Uniform;

public class ADoubleGene extends ANumericGene<Double> {

	public ADoubleGene(String name,double mutationProba, Double min, Double max) {
		super(name, mutationProba, min, max);
	}

	@Override
	public Double generateRandomnly(Uniform uniform) {
		return uniform.nextDoubleFromTo(min, max);
	}

	@Override
	public Double crossoverArithmetic(Double one, Double other, double weight) {
		
		return one.doubleValue()*weight+other.doubleValue()*(1-weight);
	}

	@Override
	public Double crossoverArithmetic(Object one, Object other, double weight) {
		return crossoverArithmetic((Double)one, (Double)other, weight);
	}

	@Override
	public Double mutate(Uniform uniform, Object previousValue) {
		return uniform.nextDoubleFromTo(min, max); // TODO check !
	}

}
