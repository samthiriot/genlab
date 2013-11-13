package genlab.algog.internal;

import cern.jet.random.Uniform;

public class AIntegerGene extends AGene<Integer> {

	public AIntegerGene(String name, Integer min, Integer max) {
		super(name, min, max);
	}

	@Override
	public Integer generateRandomnly(Uniform uniform) {
		return uniform.nextIntFromTo(min, max);
	}

}
