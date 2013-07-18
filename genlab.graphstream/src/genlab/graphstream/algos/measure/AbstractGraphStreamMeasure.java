package genlab.graphstream.algos.measure;

import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.graphstream.algos.GraphStreamAlgo;

public abstract class AbstractGraphStreamMeasure extends GraphStreamAlgo {

	public static final InputOutput<IGenlabGraph> INPUT_GRAPH = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"in_graph", 
			"graph", 
			"the graph to analyze"
	);
	
	public AbstractGraphStreamMeasure(String name, String desc) {
		super(
				name,
				desc,
				ExistingAlgoCategories.ANALYSIS_GRAPH.getTotalId()
				);
		
		inputs.add(INPUT_GRAPH);
		
	}
	
	
	
}
