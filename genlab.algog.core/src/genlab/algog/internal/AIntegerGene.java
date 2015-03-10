package genlab.algog.internal;

import cern.jet.random.Uniform;

public class AIntegerGene extends ANumericGene<Integer> {

	public AIntegerGene(String name, double mutationProba, Integer min, Integer max) {
		super(name, mutationProba, min, max);
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
		return uniform.nextIntFromTo(min, max);
	}

	@Override
	public Integer mutate(Uniform uniform, Object previousValue) {
		
		return uniform.nextIntFromTo(min, max);
	}

	
}
