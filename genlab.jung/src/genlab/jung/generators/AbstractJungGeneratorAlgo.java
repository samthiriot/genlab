package genlab.jung.generators;

import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.basics.flowtypes.GraphInOut;

public abstract class AbstractJungGeneratorAlgo extends BasicAlgo {


	public static final GraphInOut OUTPUT_GRAPH = new GraphInOut(
			"out_graph", 
			"graph", 
			"the graph generated"
			);
	
	public AbstractJungGeneratorAlgo(String name, String description) {
		super(
				name, 
				description, 
				ExistingAlgoCategories.GENERATORS_GRAPHS, 
				null,
				null
				);
		
		outputs.add(OUTPUT_GRAPH);

	}

	

}
