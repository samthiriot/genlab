package genlab.igraph.algos.transform;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.commons.IGraph2GenLabConvertor;
import genlab.igraph.natjna.IGraphGraph;

public abstract class AbstractIGraphTransformExec extends AbstractAlgoExecutionOneshot {

	public AbstractIGraphTransformExec(
			IExecution exec, 
			IAlgoInstance algoInst
			) {
		super(
				exec, 
				algoInst, 
				new ComputationProgressWithSteps()
				);
		
	}
	
	protected abstract void transformGraph(
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
			final IGenlabGraph glGraph = (IGenlabGraph) getInputValueForInput(AbstractIGraphTransform.INPUT_GRAPH);
			
			// retrieve an igraph graph
			IGraphGraph igraphGraph = IGraph2GenLabConvertor.getIGraphGraphForGenlabGraph(glGraph, exec);
			
			try {
				
				// ask the lib to transmit its information as the result of OUR computations
				igraphGraph.lib.setListOfMessages(result.getMessages());
				
				// transform
				transformGraph(progress, igraphGraph, glGraph, messages);
				
				// use outputs
				// transform back it
				IGenlabGraph outGraph = IGraph2GenLabConvertor.getGenlabGraphForIgraph(igraphGraph, exec);
				
				result.setResult(AbstractIGraphTransform.OUTPUT_GRAPH, outGraph);
				
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
		GLLogger.infoUser("sorry, for technical reasons, an igraph execution can not be cancelled", getClass());
		progress.setComputationState(ComputationState.FINISHED_CANCEL);
	}

	@Override
	public void cancel() {
		GLLogger.infoUser("sorry, for technical reasons, an igraph execution can not be cancelled", getClass());
		progress.setComputationState(ComputationState.FINISHED_CANCEL);
	}

}
