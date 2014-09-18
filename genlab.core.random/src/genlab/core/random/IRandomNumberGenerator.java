package genlab.core.random;

public interface IRandomNumberGenerator {
	
	/**
	 * Inits the random number generator from random seed (if any)
	 */
	public void init();
	
	/**
	 * Inits the random number generator from given seed 
	 * (ignored if not a pseudo-random number generator)
	 * @param seed
	 */
	public void init(long seed);
	
	/**
	 * Returns the next double in [0:1] from 
	 * a uniform probability distribution
	 * @return
	 */
	public double nextDoubleUniform();
	
	public int nextIntBetween(int min, int max);
	public double nextDoubleUniform(double min, double max);
	
	/**
	 * Clears the random network generator 
	 * for destruction
	 */
	public void clear();
	
}
