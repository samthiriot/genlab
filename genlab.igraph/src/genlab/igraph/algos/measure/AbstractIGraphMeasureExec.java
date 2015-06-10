package genlab.igraph.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.algos.generation.lcffamous.AbstractLCFFamousGraph;
import genlab.igraph.commons.IGraph2GenLabConvertor;
import genlab.igraph.commons.IGraphLibImplementation;
import genlab.igraph.commons.IgraphLibFactory;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.parameters.ChoiceOfImplementationParameter.EIgraphImplementation;

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
			IGenlabGraph genlabGraph,
			ListOfMessages messages
			);

	
	/**
	 * Based on the parameters and constraints of this algo (available only in only library),
	 * returns the library of interest.
	 */
	protected IGraphLibImplementation getLibrary() {

		
		final Integer userParamIdx  = (Integer) algoInst.getValueForParameter(AbstractIGraphMeasure.PARAM_IMPLEMENTATION);
		final EIgraphImplementation userParam  = EIgraphImplementation.values()[userParamIdx];
		
		final EIgraphImplementation developerParam = ((AbstractIGraphMeasure)getAlgoInstance().getAlgo()).implementationAcceptedOnly;
				
		// no specific need: return the choice of the user
		if (developerParam == null)
			return IgraphLibFactory.getImplementation(userParam);
		else {
			// warn user
			if (
					(userParam == EIgraphImplementation.R_ONLY && developerParam == EIgraphImplementation.JNA_ONLY)
					||
					(userParam == EIgraphImplementation.JNA_ONLY && developerParam == EIgraphImplementation.R_ONLY)
					) {
				messages.warnUser("you asked for the Igraph implementation "+userParam.label+", but this algorithm is only available for "+developerParam.label+"; switching to this last one.", getClass());
			}
			return IgraphLibFactory.getImplementation(developerParam);
		}
	}

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
