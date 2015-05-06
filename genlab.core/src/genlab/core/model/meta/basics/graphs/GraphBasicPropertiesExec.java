package genlab.core.model.meta.basics.graphs;

import genlab.core.exec.IAlgoExecutionRemotable;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.algos.GraphBasicPropertiesAlgo;

public class GraphBasicPropertiesExec 
									extends AbstractAlgoExecutionOneshot 
									implements IAlgoExecutionRemotable {

	public GraphBasicPropertiesExec(final IExecution execution,
									final AlgoInstance algoInstance) {
		super(execution, algoInstance, new ComputationProgressWithSteps());
	}
	
	public GraphBasicPropertiesExec() {}
		
	
	@Override
	public void cancel() {
		progress.setComputationState(ComputationState.FINISHED_CANCEL);
	}
		
	@Override
	public void run() {
	
		progress.setComputationState(ComputationState.STARTED);
		
		ComputationResult result = new ComputationResult(this.algoInst, getProgress(), this.exec.getListOfMessages());
		setResult(result);
		
		IGenlabGraph glGraph = (IGenlabGraph)getInputValueForInput(GraphBasicPropertiesAlgo.INPUT_GRAPH);
		
		result.setResult(GraphBasicPropertiesAlgo.OUTPUT_COUNT_VERTICES, glGraph.getVerticesCount());
		result.setResult(GraphBasicPropertiesAlgo.OUTPUT_COUNT_EDGES, glGraph.getEdgesCount());
		
		double density;
		if (glGraph.getDirectionality() == GraphDirectionality.UNDIRECTED) {
		density = 2.0*(double)glGraph.getEdgesCount()/(double)(glGraph.getVerticesCount()*(glGraph.getVerticesCount()-1));
		} else {
		density = (double)glGraph.getEdgesCount()/(double)(glGraph.getVerticesCount()*(glGraph.getVerticesCount()-1));
		}
		result.setResult(GraphBasicPropertiesAlgo.OUTPUT_DENSITY, density);
		
		
		result.setResult(GraphBasicPropertiesAlgo.OUTPUT_AVERAGE_DEGREE, ((double)glGraph.getEdgesCount())/(double)glGraph.getVerticesCount());
		
		progress.setComputationState(ComputationState.FINISHED_OK);
		
		
	}
		
	@Override
	public void kill() {
		progress.setComputationState(ComputationState.FINISHED_CANCEL);
	}
		
	@Override
	public long getTimeout() {
		return 500;
	}


}
