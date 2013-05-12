package genlab.graphstream.algos.generators;

import genlab.core.algos.AbstractAlgoExecution;
import genlab.core.algos.ComputationProgressWithSteps;
import genlab.core.algos.IAlgoInstance;
import genlab.core.algos.IComputationProgress;


/**
 * Used for any generator based on graphstream.
 * 
 * @author Samuel Thiriot
 *
 * @param 
 */
public abstract class AbstractGraphStreamGenerator extends AbstractAlgoExecution {

	public AbstractGraphStreamGenerator(IAlgoInstance algoInst, IComputationProgress progress) {
		super(
				algoInst, 
				// a generator always has a progress with steps
				new ComputationProgressWithSteps(algoInst.getAlgo())
				);
	}

	
}
