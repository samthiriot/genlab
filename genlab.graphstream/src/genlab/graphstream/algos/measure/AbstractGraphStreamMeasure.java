package genlab.graphstream.algos.measure;

import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public abstract class AbstractGraphStreamMeasure extends BasicAlgo {

	public static final InputOutput<IGenlabGraph> INPUT_GRAPH = new InputOutput<IGenlabGraph>(
			new SimpleGraphFlowType(), 
			"TODO.graph", 
			"graph", 
			"the graph to analyze"
	);
	
	public AbstractGraphStreamMeasure(String name, String desc) {
		super(
				name,
				desc,
				ExistingAlgoCategories.PARSER_GRAPH.getTotalId()
				);
		
		inputs.add(INPUT_GRAPH);
	}

	
}
