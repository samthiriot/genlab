package genlab.igraph.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.algos.AbstractIGraphExec;

import java.util.Map;

public abstract class AbstractIGraphMeasureExec extends AbstractIGraphExec {

	
	public AbstractIGraphMeasureExec(
			IExecution exec, 
			IAlgoInstance algoInst
			) {
		super(
				exec, 
				algoInst
				);
		
	}
	
	protected abstract Map<IInputOutput<?>,Object> analyzeGraph(
			IComputationProgress progress, 
			IGenlabGraph genlabGraph,
			ListOfMessages messages
			);

	
	@Override
	public void run() {
		
		// notify start
		progress.setProgressMade(0);
		progress.setProgressTotal(1);
		progress.setComputationState(ComputationState.STARTED);
		
		ComputationResult result = new ComputationResult(algoInst, progress, exec.getListOfMessages());
		setResult(result);
		

		if (noOutputIsUsed() && !exec.getExecutionForced()) {
			
			result.getMessages().debugUser("nobody is using the result of this computation; it will not be computed at all.", getClass());
		
		} else {
		
			// decode parameters
			final IGenlabGraph glGraph = (IGenlabGraph) getInputValueForInput(AbstractIGraphMeasure.INPUT_GRAPH);
			
			try {
				
				// analyze
				Map<IInputOutput<?>,Object> stats = analyzeGraph(progress, glGraph, result.getMessages());
				
				// use outputs
				for (IInputOutput<?> out: stats.keySet()) {
					Object value = stats.get(out);
					result.setResult(out, value);	
				}
				

				
				progress.setProgressMade(1);
				progress.setComputationState(ComputationState.FINISHED_OK);
				
			} catch (Exception e) {
				messages.errorTech("the measure of graph properties failed: "+e.getMessage(), getClass(), e);
				progress.setComputationState(ComputationState.FINISHED_FAILURE);
				progress.setException(e);
			} 			
			
		}

	}
	
	@Override
	public void kill() {
		//GLLogger.infoUser("sorry, for technical reasons, an igraph execution can not be cancelled", getClass());
		progress.setComputationState(ComputationState.FINISHED_CANCEL);
	}

	@Override
	public void cancel() {
		//GLLogger.infoUser("sorry, for technical reasons, an igraph execution can not be cancelled", getClass());
		progress.setComputationState(ComputationState.FINISHED_CANCEL);
	}
	
	/**
	 * for serialization only
	 */
	public AbstractIGraphMeasureExec() {}

}
