package genlab.algog.internal;

import cern.jet.random.Uniform;

public class ABooleanGene extends AGene<Boolean> {

	public ABooleanGene(String name, double mutationProba, Boolean value) {
		super(name, mutationProba, value);
	}

	@Override
	public Boolean generateRandomnly(Uniform uniform) {
		this.value = uniform.nextBoolean();
		return this.value;
	}

	@Override
	public Boolean mutate(Uniform uniform, Object previousValue) {
		// invert the value
		this.value = !((Boolean)previousValue);
		return this.value;
	}

}
