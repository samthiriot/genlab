package genlab.igraph.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IComputationProgress;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.IInputOutput;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleFlowType;
import genlab.core.model.meta.basics.graphs.GraphDirectionality;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.natjna.IGraphGraph;

import java.util.HashMap;
import java.util.Map;

public class IGraphClusteringAlgo extends AbstractIGraphMeasure {

	public static final InputOutput<Double> OUTPUT_CLUSTERING_GLOBAL = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"global_clustering", 
			"global clustering", 
			"global clustering of the graph."
	);
	
	public static final InputOutput<Double> OUTPUT_CLUSTERING_AVERAGE = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"average_clustering", 
			"average clustering", 
			"average clustering of the graph."
	);
	
	
	public IGraphClusteringAlgo() {
		super(
				"average clustering (igraph)", 
				"computes the average clustering (or transitivity) of this graph. The transitivity measures the probability that two neighbors of a vertex are connected."
				);
		outputs.add(OUTPUT_CLUSTERING_GLOBAL);
		outputs.add(OUTPUT_CLUSTERING_AVERAGE);

	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new IGraphClusteringExec(execution, algoInstance);
	}

}
