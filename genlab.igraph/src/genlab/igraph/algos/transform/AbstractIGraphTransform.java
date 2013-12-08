package genlab.igraph.algos.transform;

import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.Activator;

import org.osgi.framework.Bundle;

public abstract class AbstractIGraphTransform extends BasicAlgo {

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
				"/icons/igraph"+IMAGE_PATH_PLACEHOLDER_SIZE+".png",
				"/icons/igraphBig.png"
				);
		
		inputs.add(INPUT_GRAPH);
		outputs.add(OUTPUT_GRAPH);
	}

	@Override
	public Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}

}
