package genlab.jung.generators;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public abstract class AbstractJungGeneratorExec extends AbstractAlgoExecutionOneshot {

	public AbstractJungGeneratorExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst, new ComputationProgressWithSteps());

	}

	@Override
	public abstract long getTimeout();

	protected abstract IGenlabGraph generate();
	
	@Override
	public void run() {
		
		progress.setComputationState(ComputationState.STARTED);

		ComputationResult results = new ComputationResult(algoInst, progress, exec.getListOfMessages());
		setResult(results);
		
		IGenlabGraph glGraph = generate();
		
		results.setResult(
				AbstractJungGeneratorAlgo.OUTPUT_GRAPH, 
				glGraph
				);
		
		progress.setComputationState(ComputationState.FINISHED_OK);
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

}
