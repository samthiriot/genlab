package genlab.igraph.algos.generation;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.commons.IGraph2GenLabConvertor;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.natjna.IGraphLibrary;
import genlab.igraph.natjna.IGraphRawLibraryPool;

public abstract class AbstractIGraphGeneratorExec extends AbstractAlgoExecution {

	public AbstractIGraphGeneratorExec(
			IExecution exec, 
			IAlgoInstance algoInst
			) {
		super(
				exec, 
				algoInst, 
				new ComputationProgressWithSteps()
				);
		
	}
	
	protected abstract IGraphGraph generateGraph(
			IGraphLibrary lib,
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
			
			IGraphLibrary lib = IGraphRawLibraryPool.singleton.getLibrary();
					
			IGraphGraph igraphGraph = null;
					
			try {
				
				// ask the lib to transmit its information as the result of OUR computations
				lib.setListOfMessages(result.getMessages());
				
				// generate
				igraphGraph = generateGraph(lib, messages);
				
				IGenlabGraph genlabGraph = IGraph2GenLabConvertor.getGenlabGraphForIgraph(igraphGraph, exec);

				result.setResult(AbstractIGraphGenerator.OUTPUT_GRAPH, genlabGraph);
				
				
			} catch (RuntimeException e) {
				messages.errorTech("error during the igraph call: "+e.getMessage(), getClass(), e);
				throw new ProgramException("error during the igraph call: "+e.getMessage(), e);
			} finally {
				// clear memory
				lib.clearGraphMemory(igraphGraph);
				lib.setListOfMessages(null);
				IGraphRawLibraryPool.singleton.returnLibrary(lib);

			}
			
			
		}
		
		
		progress.setProgressMade(1);
		progress.setComputationState(ComputationState.FINISHED_OK);

	}
	
	@Override
	public void kill() {
		GLLogger.infoUser("sorry, for technical reasons, an igraph execution can not be cancelled", getClass());
	}

	@Override
	public void cancel() {
		GLLogger.infoUser("sorry, for technical reasons, an igraph execution can not be cancelled", getClass());
	}

}
