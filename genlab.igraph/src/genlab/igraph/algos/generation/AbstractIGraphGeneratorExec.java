package genlab.igraph.algos.generation;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
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

public abstract class AbstractIGraphGeneratorExec extends AbstractAlgoExecutionOneshot {

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
		
		try { 
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
				
				IGraphLibrary lib = new IGraphLibrary();
						
				IGraphGraph igraphGraph = null;
						
				try {
					
					result.getMessages().debugUser("using the igraph native library version "+lib.getVersionString(), getClass());
					
					// ask the lib to transmit its information as the result of OUR computations
					lib.setListOfMessages(result.getMessages());
					
					// generate
					igraphGraph = generateGraph(lib, messages);
					
					IGenlabGraph genlabGraph = IGraph2GenLabConvertor.getGenlabGraphForIgraph(igraphGraph, exec);
	
					result.setResult(AbstractIGraphGenerator.OUTPUT_GRAPH, genlabGraph);
	
					
					progress.setProgressMade(1);
					progress.setComputationState(ComputationState.FINISHED_OK);
	
				} catch (RuntimeException e) {
					
					messages.errorTech("error during the igraph call: "+e.getMessage(), getClass(), e);
					progress.setException(e);
					progress.setComputationState(ComputationState.FINISHED_FAILURE);
					//throw new ProgramException("error during the igraph call: "+e.getMessage(), e);
					
				} finally {
					// clear memory
					lib.clearGraphMemory(igraphGraph);
					lib.setListOfMessages(null);
	
				}
				
				
			}
		} catch (RuntimeException e) {
			messages.errorTech("error during the execution of this generator: "+e.getLocalizedMessage(), getClass(), e);
			progress.setException(e);
			progress.setComputationState(ComputationState.FINISHED_FAILURE);
		}
		
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
