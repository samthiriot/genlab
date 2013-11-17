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

}
