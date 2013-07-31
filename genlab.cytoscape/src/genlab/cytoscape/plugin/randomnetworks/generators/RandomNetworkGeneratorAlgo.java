package genlab.cytoscape.plugin.randomnetworks.generators;

import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.basics.flowtypes.GraphInOut;
import genlab.core.parameters.BooleanParameter;
import genlab.cytoscape.commons.CytoscapeAlgo;

public abstract class RandomNetworkGeneratorAlgo extends CytoscapeAlgo {

	public static final GraphInOut OUTPUT_GRAPH = new GraphInOut("out_graph", "graph", "generated graph");
	
	public static BooleanParameter PARAM_LOOPS = new BooleanParameter("loops", "loops", "allow loops", false);

	public static BooleanParameter PARAM_DIRECTED = new BooleanParameter("directed", "directed", "generate directed graphs", false);

	public RandomNetworkGeneratorAlgo(String name, String description) {
		super(
				name, description, 
				ExistingAlgoCategories.GENERATORS_GRAPHS.getTotalId()
				);
		outputs.add(OUTPUT_GRAPH);
		
		registerParameter(PARAM_DIRECTED);
		registerParameter(PARAM_LOOPS);

	}


}
