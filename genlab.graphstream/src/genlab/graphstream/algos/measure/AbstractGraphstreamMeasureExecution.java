package genlab.graphstream.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.graphstream.utils.GraphstreamConvertors;

import java.util.Map;

import org.graphstream.graph.Graph;

public abstract class AbstractGraphstreamMeasureExecution extends
		AbstractAlgoExecution {
	
	protected boolean cancelled = false;

	public AbstractGraphstreamMeasureExecution(
			IExecution exec,
			IAlgoInstance algoInst
			) {
		
		super(
				exec, 
				algoInst, 
				new ComputationProgressWithSteps()
				);
		

	
	}

	/**
	 * Receives as parameters a progress (to be used to define the progress if possible),
	 * a gsGraph that is already a clone of the input one,
	 * and the genlab graph.
	 * Should return a map between expected outputs and the corresponding object.
	 * 
	 * @param progress
	 * @param gsGraph
	 * @param genlabGraph
	 * @return
	 */
	protected abstract Map<IInputOutput<?>,Object> analyzeGraph(IComputationProgress progress, Graph gsGraph, IGenlabGraph genlabGraph, ListOfMessages messages);
	
	
	
	@Override
	public void run() {

		// notify start
		progress.setProgressMade(0);
		progress.setProgressTotal(1);
		progress.setComputationState(ComputationState.STARTED);
		
		ComputationResult result = new ComputationResult(algoInst, progress);
		setResult(result);


		if (noOutputIsUsed() && !exec.getExecutionForced()) {
			
			result.getMessages().warnUser("nobody is using the result of this computation; it will not be computed at all.", getClass());
		
		} else {
		
			// decode parameters
			final IGenlabGraph glGraph = (IGenlabGraph) getInputValueForInput(AbstractGraphStreamMeasure.INPUT_GRAPH);
			
			Graph gsGraph = GraphstreamConvertors.getGraphstreamGraphFromGenLabGraph(glGraph, result.getMessages());
			
			GraphstreamConvertors.GenLabGraphSink ourSink = new GraphstreamConvertors.GenLabGraphSink(
					"opened", 
					result.getMessages()
					);
			
			
			// analyze
			Map<IInputOutput<?>,Object> stats = analyzeGraph(progress, gsGraph, glGraph, result.getMessages());
			
			// use outputs
			for (IInputOutput<?> out: stats.keySet()) {
				Object value = stats.get(out);
				result.setResult(out, value);	
			}
			
		}
		
		progress.setProgressMade(1);
		progress.setComputationState(ComputationState.FINISHED_OK);

		setResult(result);
	}
	
	@Override
	public void kill() {
		cancelled = true;				
	}

	@Override
	public void cancel() {
		cancelled = true;
	}
	

}
