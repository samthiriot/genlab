package genlab.graphstream.algos.generators;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IAlgoInstance;


/**
 * Used for any generator based on graphstream.
 * 
 * @author Samuel Thiriot
 *
 * @param 
 */
public abstract class AbstractGraphStreamGenerator extends AbstractAlgoExecution {

	public AbstractGraphStreamGenerator(IExecution exec, IAlgoInstance algoInst, IComputationProgress progress) {
		super(
				exec,
				algoInst, 
				// a generator always has a progress with steps
				new ComputationProgressWithSteps()
				);
	}

	
}
