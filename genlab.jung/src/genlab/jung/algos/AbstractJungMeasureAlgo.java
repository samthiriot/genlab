package genlab.jung.algos;

import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.basics.flowtypes.GraphInOut;

public abstract class AbstractJungMeasureAlgo extends BasicAlgo {


	public static final GraphInOut INPUT_GRAPH = new GraphInOut(
			"in_graph", 
			"graph", 
			"the graph to analyze"
			);
	
	public AbstractJungMeasureAlgo(String name, String description) {
		super(
				name, 
				description, 
				null, 
				ExistingAlgoCategories.ANALYSIS_GRAPH.getTotalId(), 
				null
				);
		
		inputs.add(INPUT_GRAPH);

	}
	

}
