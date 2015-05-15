package genlab.gephi.algos.measure;

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
import genlab.gephi.utils.GephiConvertors;
import genlab.gephi.utils.GephiGraph;

import java.util.Map;

import org.gephi.project.api.Workspace;

public abstract class GephiAbstractAlgoExecution extends AbstractAlgoExecutionOneshot {

	protected boolean cancelled = false;
	
	public GephiAbstractAlgoExecution(
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
	protected abstract Map<IInputOutput<?>,Object> analyzeGraph(
			IComputationProgress progress, 
			GephiGraph gephiGraph, 
			IGenlabGraph genlabGraph
			);
	

	@Override
	public void run() {
		
		// notify start
		progress.setProgressMade(0);
		progress.setProgressTotal(1);
		progress.setComputationState(ComputationState.STARTED);
		
		messages.warnUser(
				"the underlying Gephi implementation used by this algorithm ("+algoInst.getName()+") "
						+ "is known to be buggy and consume all ressources; "
						+ "you should replace this algorithm by an equivalent algorithm from another library", 
						getClass()
						);

		ComputationResult result = new ComputationResult(algoInst, progress, exec.getListOfMessages());
		setResult(result);


		if (noOutputIsUsed() && !exec.getExecutionForced()) {
			
			result.getMessages().warnUser("nobody is using the result of this computation; it will not be computed at all.", getClass());
		
		} else {
		
			// decode parameters
			final IGenlabGraph glGraph = (IGenlabGraph) getInputValueForInput(GephiAbstractAlgo.INPUT_GRAPH);
			
			
			final GephiGraph gephiGraph = GephiConvertors.loadIntoAGephiWorkspace(
					glGraph, 
					result.getMessages(), 
					false, 
					false, 
					false
					);
									
			// analyze
			Map<IInputOutput<?>,Object> stats = analyzeGraph(progress, gephiGraph , glGraph);
			
			// use outputs
			for (IInputOutput<?> out: stats.keySet()) {
				Object value = stats.get(out);
				//GLLogger.debugTech("result :"+out.getName()+"= "+value, getClass());
				result.setResult(out, value);	
			}
		
			// clear data
			GephiConvertors.clearGraph(gephiGraph);
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
