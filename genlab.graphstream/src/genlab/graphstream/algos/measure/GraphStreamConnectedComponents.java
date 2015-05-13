package genlab.graphstream.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.BooleanInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public class GraphStreamConnectedComponents extends AbstractGraphStreamMeasure {


	public static final InputOutput<Integer> OUTPUT_COUNT = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON, 
			"out_count", 
			"count", 
			"the number of components found in the graph"
	);
	public static final BooleanInOut OUTPUT_CONNECTED = new BooleanInOut(
			"out_connected", 
			"is connected", 
			"true if the graph is connected"
	);
	public static final InputOutput<Integer> OUTPUT_GIANT_COMPONENT_SIZE = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON, 
			"out_giantComponentSize", 
			"giant component size", 
			"the number of vertices found in the giant component"
	);
	public static final InputOutput<IGenlabGraph> OUTPUT_GRAPH = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"out_graph", 
			"graph", 
			"the graph analyzed, with some data added as attributes"
	);
	
	
	public GraphStreamConnectedComponents() {
		super(
				"connected components", 
				"detects connected components (provided by graphstream)"
				);
		
		outputs.add(OUTPUT_COUNT);
		outputs.add(OUTPUT_CONNECTED);
		outputs.add(OUTPUT_GRAPH);
		outputs.add(OUTPUT_GIANT_COMPONENT_SIZE);
		
	}

	@Override
	public IAlgoExecution createExec(
			IExecution execution,
			AlgoInstance algoInstance) {
		
		return new GraphStreamConnectedComponentsExec(execution, algoInstance);
	}

}
