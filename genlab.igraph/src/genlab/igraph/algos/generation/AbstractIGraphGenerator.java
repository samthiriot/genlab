package genlab.igraph.algos.generation;

import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.basics.flowtypes.GraphInOut;
import genlab.igraph.Activator;

import org.osgi.framework.Bundle;

public abstract class AbstractIGraphGenerator extends BasicAlgo {

	public static final GraphInOut OUTPUT_GRAPH = new GraphInOut( 
			"out_graph", 
			"graph", 
			"the graph generated"
	);
	
	
	public AbstractIGraphGenerator(
			String name, 
			String description
			) {
		this(name, description, ExistingAlgoCategories.GENERATORS_GRAPHS.getTotalId());
	}
	
	public AbstractIGraphGenerator(
			String name, 
			String description,
			String categoryId
			) {
		super(
				name, 
				description, 
				null,
				categoryId,
				"/icons/igraph.gif"
				);
		
		outputs.add(OUTPUT_GRAPH);
	}

	@Override
	public Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}

}
