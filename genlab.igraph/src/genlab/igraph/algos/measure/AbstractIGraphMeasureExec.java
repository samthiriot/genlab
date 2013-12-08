package genlab.igraph.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.commons.IGraph2GenLabConvertor;
import genlab.igraph.natjna.IGraphGraph;

import java.util.Map;

public abstract class AbstractIGraphMeasureExec extends AbstractAlgoExecutionOneshot {

	public AbstractIGraphMeasureExec(
			IExecution exec, 
			IAlgoInstance algoInst
			) {
		super(
				exec, 
				algoInst, 
				new ComputationProgressWithSteps()
				);
		
	}
	
	protected abstract Map<IInputOutput<?>,Object> analyzeGraph(
			IComputationProgress progress, 
			IGraphGraph igraphGraph, 
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
			
			IGraphGraph igraphGraph = IGraph2GenLabConvertor.getIGraphGraphForGenlabGraph(glGraph, exec);
			
			try {
				
				// ask the lib to transmit its information as the result of OUR computations
				igraphGraph.lib.setListOfMessages(result.getMessages());
				
				// analyze
				Map<IInputOutput<?>,Object> stats = analyzeGraph(progress, igraphGraph, glGraph, result.getMessages());
				
				// use outputs
				for (IInputOutput<?> out: stats.keySet()) {
					Object value = stats.get(out);
					result.setResult(out, value);	
				}
				
			} finally {
				// clear memory
				igraphGraph.lib.clearGraphMemory(igraphGraph);
				igraphGraph.lib.setListOfMessages(null);

			}
			
			
		}
		
		progress.setProgressMade(1);
		progress.setComputationState(ComputationState.FINISHED_OK);

	}
	
	@Override
	public void kill() {
		//GLLogger.infoUser("sorry, for technical reasons, an igraph execution can not be cancelled", getClass());
	}

	@Override
	public void cancel() {
		//GLLogger.infoUser("sorry, for technical reasons, an igraph execution can not be cancelled", getClass());
	}

}
