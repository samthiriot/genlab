package genlab.algog.internal;

import cern.jet.random.Uniform;

public class AIntegerGene extends ANumericGene<Integer> {

	public AIntegerGene(String name, double mutationProba, Integer min, Integer max, Integer value) {
		super(name, mutationProba, min, max, value);
	}


	@Override
	public Integer crossoverArithmetic(Integer one, Integer other, double weight) {

		Integer result = (int)Math.floor(weight*one) + (int)Math.ceil((1-weight)*other);
		
		if (result > max)
			return max;
		else if (result < min)
			return min;
		else
			return result;
	}

	@Override
	public Integer crossoverArithmetic(Object one, Object other, double weight) {
		return crossoverArithmetic((Integer)one, (Integer)other, weight);
	}

	@Override
	public Integer generateRandomnly(Uniform uniform) {
		this.value = uniform.nextIntFromTo(min, max);
		return this.value;
	}

	@Override
	public Integer mutate(Uniform uniform, Object previousValue) {
		this.value = uniform.nextIntFromTo(min, max);
		return this.value;
	}

	
}
