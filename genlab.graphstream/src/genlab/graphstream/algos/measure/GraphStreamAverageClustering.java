package genlab.graphstream.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleFlowType;

/**
 * The APSP graphstream algorithm implements the Floyd-Warshall all pair shortest path algorithm.
 * 
 * @see http://graphstream-project.org/api/gs-algo/org/graphstream/algorithm/APSP.html
 * @see http://en.wikipedia.org/wiki/Average_path_length
 * 
 * @author Samuel Thiriot
 *
 */
public class GraphStreamAverageClustering extends AbstractGraphStreamMeasure {


	public static final InputOutput<Double> OUTPUT_AVERAGE_CLUSTERING = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"out_average_clustering", 
			"average clustering", 
			"the average clustering rate in the graph"
	);
	
	
	public GraphStreamAverageClustering() {
		super(
				"clustering (graphstream)", 
				"computes the clustering rate of a graph"
				);
		
		outputs.add(OUTPUT_AVERAGE_CLUSTERING);
		
	}

	@Override
	public IAlgoExecution createExec(
			final IExecution execution,
			final AlgoInstance algoInstance) {
		
		return new GraphStreamAverageClusteringExec(execution, algoInstance, execution);
	}

}
