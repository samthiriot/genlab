package genlab.igraph.algos.measure;

import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public abstract class AbstractIGraphMeasure extends BasicAlgo {

	public static final InputOutput<IGenlabGraph> INPUT_GRAPH = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"in_graph", 
			"graph", 
			"the graph to analyze"
	);
	
	
	public AbstractIGraphMeasure(
			String name, 
			String description
			) {
		super(
				name, 
				description, 
				ExistingAlgoCategories.ANALYSIS_GRAPH.getTotalId()
				);
		
		inputs.add(INPUT_GRAPH);
	}


}
