package genlab.igraph.algos.transform;

import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.algos.AbstractIGraphAlgo;

public abstract class AbstractIGraphTransform extends AbstractIGraphAlgo {

	public static final InputOutput<IGenlabGraph> INPUT_GRAPH = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"in_graph", 
			"graph", 
			"the graph to transform"
	);
	
	public static final InputOutput<IGenlabGraph> OUTPUT_GRAPH = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"out_graph", 
			"graph", 
			"the graph transformed"
	);
	
	
	public AbstractIGraphTransform(
			String name, 
			String description,
			AlgoCategory category
			) {
		super(
				name, 
				description, 
				category,
				null
				);
		
		inputs.add(INPUT_GRAPH);
		outputs.add(OUTPUT_GRAPH);
	}

}
