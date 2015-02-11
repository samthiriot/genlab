package genlab.igraph.algos.generation;

import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.basics.flowtypes.GraphInOut;
import genlab.igraph.Activator;
import genlab.igraph.natjna.IGraphRawLibrary;

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
		this(name, description, ExistingAlgoCategories.GENERATORS_GRAPHS);
	}
	
	public AbstractIGraphGenerator(
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
		
		outputs.add(OUTPUT_GRAPH);
	}

	@Override
	public Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}

	@Override
	public boolean isAvailable() {
		return IGraphRawLibrary.isAvailable;
	}
	
}
