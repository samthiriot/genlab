package genlab.cytoscape.plugin.randomnetworks.analysis;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.cytoscape.commons.Cytoscape2Genlab;
import cytoscape.randomnetwork.RandomNetwork;

public abstract class RandomNetworkAnalyzeExec extends AbstractAlgoExecutionOneshot {

	public RandomNetworkAnalyzeExec(IExecution exec, IAlgoInstance algoInst) {
		
		super(exec, algoInst, new ComputationProgressWithSteps());

		
	}
	
	protected abstract void analyze(RandomNetwork cyNetwork, boolean directed);
	
	@Override
	public void run() {

		progress.setComputationState(ComputationState.STARTED);
		
		ComputationResult result = new ComputationResult(algoInst, progress, exec.getListOfMessages());
		setResult(result);
		
		// TODO check directionality
		
		final IGenlabGraph glGraph = (IGenlabGraph)getInputValueForInput(RandomNetworkAnalyzerAlgo.INPUT_GRAPH);
		
		final RandomNetwork cyNetwork = Cytoscape2Genlab.getCytoscapeGraphForIGraph(glGraph);
		
		analyze(cyNetwork, glGraph.getDirectionality()!=GraphDirectionality.UNDIRECTED);
		
		
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
