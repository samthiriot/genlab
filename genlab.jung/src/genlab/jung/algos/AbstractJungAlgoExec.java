package genlab.jung.algos;

import edu.uci.ics.jung.graph.Graph;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.jung.utils.Converters;

public abstract class AbstractJungAlgoExec extends AbstractAlgoExecutionOneshot {

	public AbstractJungAlgoExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst, new ComputationProgressWithSteps());

	}

	@Override
	public abstract long getTimeout();

	protected abstract void compute(Graph<String,String> jungGraph, IGenlabGraph glGraph, ComputationResult results, IComputationProgress progress);
	
	@Override
	public void run() {
		

		final IGenlabGraph glGraph = (IGenlabGraph) getInputValueForInput(AbstractJungMeasureAlgo.INPUT_GRAPH);
		Graph<String,String> jungGraph = Converters.getJungGraphForGenlabGraphReadonly(glGraph);
	
		progress.setComputationState(ComputationState.STARTED);

		ComputationResult results = new ComputationResult(algoInst, progress, exec.getListOfMessages());
				
		compute(jungGraph, glGraph, results, progress);
		
		
		setResult(results);
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
