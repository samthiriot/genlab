package genlab.cytoscape.plugin.randomnetworks.analysis;

import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.basics.flowtypes.GraphInOut;
import genlab.cytoscape.commons.CytoscapeAlgo;

public abstract class RandomNetworkAnalyzerAlgo extends CytoscapeAlgo {

	public static final GraphInOut INPUT_GRAPH = new GraphInOut("in_graph", "graph", "graph to analyze");

	public RandomNetworkAnalyzerAlgo(String name, String description) {
		super(name, description, ExistingAlgoCategories.ANALYSIS_GRAPH.getTotalId());
		
		inputs.add(INPUT_GRAPH);
		
	}


}
