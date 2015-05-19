package genlab.igraph.algos.generation;

import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IAlgoExecutionRemotable;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.GLLogger;

public abstract class AbstractIGraphGeneratorExec 	
										extends AbstractAlgoExecutionOneshot 
										implements IAlgoExecutionRemotable
										{

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
	
	/**
	 * for serialization only
	 */
	public AbstractIGraphGeneratorExec(){}
	
	/**
	 * Should be overriden to do the right call(s) to 
	 * actually generate the graph.
	 * @return
	 */
	protected abstract IGenlabGraph generateGraph();


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
										
			try {
				
				// generate
				IGenlabGraph genlabGraph = generateGraph();
				
				result.setResult(AbstractIGraphGenerator.OUTPUT_GRAPH, genlabGraph);
				
				progress.setProgressMade(1);
				progress.setComputationState(ComputationState.FINISHED_OK);

			} catch(WrongParametersException e) {
				
				messages.errorUser("wrong parameters for this algorithm: "+e.getMessage(), getClass(), e);
				progress.setException(e);
				progress.setComputationState(ComputationState.FINISHED_FAILURE);
				
			} catch (RuntimeException e) {
			
				
				messages.errorTech("error during the igraph call: "+e.getMessage(), getClass(), e);
				progress.setException(e);
				progress.setComputationState(ComputationState.FINISHED_FAILURE);
				//throw new ProgramException("error during the igraph call: "+e.getMessage(), e);
				
			} 
			
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
