package genlab.random.colt;

import java.util.Date;

import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import genlab.core.random.IRandomNumberGenerator;

public final class ColtRandomGenerator implements IRandomNumberGenerator {

	private RandomEngine random = null;
	private Uniform uniform = null;
	
	public ColtRandomGenerator() {
		init();
	}

	@Override
	public void init() {
		random = new MersenneTwister(new Date());
		uniform = new Uniform(random);
	}

	@Override
	public void init(long seed) {
		// TODO ugly
		random = new MersenneTwister((int)seed);
		uniform = new Uniform(random);
	}

	@Override
	public double nextDoubleUniform() {
		return uniform.nextDouble();
	}

	@Override
	public void clear() {
		random = null;
		uniform = null;
	}

	@Override
	public int nextIntBetween(int min, int max) {
		return uniform.nextIntFromTo(min, max);
	}


	@Override
	public double nextDoubleUniform(double min, double max) {
		return uniform.nextDoubleFromTo(min, max);
	}

}
