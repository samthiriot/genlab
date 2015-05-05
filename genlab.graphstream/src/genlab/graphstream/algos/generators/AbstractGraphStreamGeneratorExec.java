package genlab.graphstream.algos.generators;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.instance.IAlgoInstance;


/**
 * Used for any generator based on graphstream.
 * 
 * @author Samuel Thiriot
 *
 * @param 
 */
public abstract class AbstractGraphStreamGeneratorExec extends AbstractAlgoExecutionOneshot {

	public AbstractGraphStreamGeneratorExec(IExecution exec, IAlgoInstance algoInst) {
		super(
				exec,
				algoInst, 
				// a generator always has a progress with steps
				new ComputationProgressWithSteps()
				);
		
		
	}

	
}
