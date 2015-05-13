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
public class GraphStreamAPSP extends AbstractGraphStreamMeasure {


	public static final InputOutput<Double> OUTPUT_AVERAGE_PATH_LENGTH = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"out_average_path_length", 
			"average path length", 
			"the average length of the shortest pathes"
	);
	

	public static final InputOutput<Double> OUTPUT_DIAMETER = new InputOutput<Double>(
			DoubleFlowType.SINGLETON, 
			"out_diameter", 
			"diameter", 
			"the diameter, that is the longest shortest path in the graph. "
	);
	
	
	public GraphStreamAPSP() {
		super(
				"Floyd-Warshall", 
				"computes all the shortest pathes on the giant component of a graph "
				);
		
		outputs.add(OUTPUT_AVERAGE_PATH_LENGTH);
		outputs.add(OUTPUT_DIAMETER);
		
	}

	@Override
	public IAlgoExecution createExec(
			IExecution execution,
			AlgoInstance algoInstance) {
		
		return new GraphStreamAPSPExec(execution, algoInstance);
	}

}
