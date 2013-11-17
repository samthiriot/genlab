package genlab.algog.internal;

import cern.jet.random.Uniform;

public class AIntegerGene extends ANumericGene<Integer> {

	public AIntegerGene(String name, double mutationProba, Integer min, Integer max) {
		super(name, mutationProba, min, max);
	}

	@Override
	public Integer generateRandomnly(Uniform uniform) {
		return uniform.nextIntFromTo(min, max);
	}

}
