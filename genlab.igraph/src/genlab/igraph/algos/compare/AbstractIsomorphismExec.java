package genlab.igraph.algos.compare;

import genlab.core.commons.WrongParametersException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecution;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.commons.IGraph2GenLabConvertor;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.natjna.IGraphRawLibraryPool;

public abstract class AbstractIsomorphismExec extends AbstractAlgoExecution {

	public AbstractIsomorphismExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst, new ComputationProgressWithSteps());
	}

	
	protected abstract boolean compareGraphs(IGraphGraph igraphGraph1, IGraphGraph igraphGraph2);

	/**
	 * Called when no iso can exists (different count of edges for instance)
	 */
	protected void noIsomorphismPossible(ComputationResult result) {
		
	}
	
	@Override
	public void run() {
		
		progress.setComputationState(ComputationState.STARTED);
		ComputationResult result = new ComputationResult(algoInst, progress, messages);
		setResult(result);
		
		IGenlabGraph glGraph1 = (IGenlabGraph)getInputValueForInput(BasicIsomorphism.INPUT_GRAPH1);
		IGenlabGraph glGraph2 = (IGenlabGraph)getInputValueForInput(BasicIsomorphism.INPUT_GRAPH2);
		
		Boolean iso = null;
		
		// check params

		if (glGraph1.getDirectionality() == GraphDirectionality.MIXED)
			throw new WrongParametersException("unable to compare mixed graphs");
		if (glGraph2.getDirectionality() == GraphDirectionality.MIXED)
			throw new WrongParametersException("unable to compare mixed graphs");
		
		if (glGraph1.getDirectionality() != glGraph2.getDirectionality())
			throw new WrongParametersException("unable to compare graphs with different directionalities");

		// quick answers
		if (glGraph1.getVerticesCount() != glGraph2.getVerticesCount()) {
			noIsomorphismPossible(result);
			iso = false;
		}
		
		if (iso == null && glGraph1.getEdgesCount() != glGraph2.getEdgesCount()) {
			noIsomorphismPossible(result);
			iso = false;
		}
		
		if (iso == null) {
			IGraphGraph igraphGraph1 = null;
			IGraphGraph igraphGraph2 = null;
			try {
				
				
				igraphGraph1 = IGraph2GenLabConvertor.getIGraphGraphForGenlabGraph(glGraph1, exec);
				igraphGraph2 = IGraph2GenLabConvertor.getIGraphGraphForGenlabGraph(glGraph2, exec, igraphGraph1.lib);
				
				iso = compareGraphs(igraphGraph1, igraphGraph2);
				
				
			} finally {
				
				if (igraphGraph1 != null)
					igraphGraph1.lib.clearGraphMemory(igraphGraph1);
				if (igraphGraph2 != null)
					igraphGraph2.lib.clearGraphMemory(igraphGraph2);
				
				IGraphRawLibraryPool.singleton.returnLibrary(igraphGraph1.lib);
				
			}
		}
		
		result.setResult(BasicIsomorphism.OUTPUT_ISOMORPHIC, iso);
		
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
