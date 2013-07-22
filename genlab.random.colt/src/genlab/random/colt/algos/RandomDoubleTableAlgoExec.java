package genlab.random.colt.algos;

import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import cern.jet.random.engine.RandomGenerator;
import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.algos.AbstractTableGeneratorExec;
import genlab.core.usermachineinteraction.GLLogger;

// TODO doc ! http://en.wikipedia.org/wiki/Colt_(libraries)
// see also http://vanillajava.blogspot.fr/2012/02/high-performance-libraries-in-java.html
// http://code.google.com/p/java-matrix-benchmark/
// http://acs.lbl.gov/software/colt/license.html
public class RandomDoubleTableAlgoExec extends AbstractTableGeneratorExec {

	private RandomEngine coltRandom = null;
	private Uniform coltUniform = null;
	
	public RandomDoubleTableAlgoExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
	}
	
	@Override
	protected void initRun() {
		GLLogger.debugTech("initializing the COLT random generator...", getClass());
		
		// TODO parameter for seed 
		coltRandom = new MersenneTwister();
		
		Double min = (Double)algoInst.getValueForParameter(RandomDoubleTableAlgo.PARAMETER_MIN.getId());
		Double max = (Double)algoInst.getValueForParameter(RandomDoubleTableAlgo.PARAMETER_MAX.getId());
		
		coltUniform = new Uniform(min, max, coltRandom);
		
		GLLogger.infoUser(
				"COLT random generator initialized with a Mersenne-Twister pseudo-random generator " +
				"with min "+min+" and max "+max+" (as defined in the algorithm's parameters)", 
				getClass());

		
	}

	@Override
	protected Object[] generateValues(int count) {
	
		Object[] res = new Object[count];
		
		for (int i=0; i<count; i++) {
			res[i] = coltUniform.nextDouble();
		}
		
		return res;
	}

}
