package genlab.core.model.exec;

import genlab.core.exec.IExecution;
import genlab.core.model.instance.IAlgoInstance;

public abstract class AbstractAlgoExecutionReduce 
							extends AbstractAlgoExecution 
							implements IReduceAlgoExecution {

	public AbstractAlgoExecutionReduce(IExecution exec, IAlgoInstance algoInst,
			IComputationProgress progress) {
		super(exec, algoInst, progress);
	}


}
