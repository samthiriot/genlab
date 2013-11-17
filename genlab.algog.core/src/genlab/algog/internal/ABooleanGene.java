package genlab.algog.internal;

import cern.jet.random.Uniform;

public class ABooleanGene extends AGene<Boolean> {

	public ABooleanGene(String name, double mutationProba) {
		super(name, mutationProba);
	}

	@Override
	public Boolean generateRandomnly(Uniform uniform) {
		return uniform.nextBoolean();
	}

}
