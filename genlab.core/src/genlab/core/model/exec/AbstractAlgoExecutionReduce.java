package genlab.core.model.exec;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IReduceAlgoInstance;

public abstract class AbstractAlgoExecutionReduce 
							extends AbstractAlgoExecution 
							implements IReduceAlgoInstance {

	public AbstractAlgoExecutionReduce(IExecution exec, IAlgoInstance algoInst,
			IComputationProgress progress) {
		super(exec, algoInst, progress);
	}



}
