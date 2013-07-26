package genlab.igraph.algos.generation;

import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.GraphInOut;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.Activator;

import org.eclipse.core.runtime.Plugin;
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
		super(
				name, 
				description, 
				null,
				ExistingAlgoCategories.GENERATORS_GRAPHS.getTotalId(),
				"/icons/igraph.gif"
				);
		
		outputs.add(OUTPUT_GRAPH);
	}

	@Override
	public Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}

}
