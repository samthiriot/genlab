package genlab.algog.algos.exec;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IAlgoInstance;

public class GeneticExplorationAlgoExec extends AbstractAlgoExecutionOneshot {

	public GeneticExplorationAlgoExec(
			IExecution exec, 
			IAlgoInstance algoInst,
			IComputationProgress progress) {
		
		super(exec, algoInst, progress);
		
		
	}

	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub

	}

}
