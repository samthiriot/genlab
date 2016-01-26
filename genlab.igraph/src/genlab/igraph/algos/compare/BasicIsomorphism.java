package genlab.igraph.algos.compare;

import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.BooleanInOut;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.Activator;
import genlab.igraph.natjna.IGraphRawLibrary;

import org.osgi.framework.Bundle;

// TODO subclass AbstractIGraphAlgo
public abstract class BasicIsomorphism extends BasicAlgo {

	public static final InputOutput<IGenlabGraph> INPUT_GRAPH1 = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"in_graph1", 
			"graph1", 
			"the graph to analyze"
	);
	
	public static final InputOutput<IGenlabGraph> INPUT_GRAPH2 = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"in_graph2", 
			"graph2", 
			"the graph to analyze"
	);
	
	public static final BooleanInOut OUTPUT_ISOMORPHIC = new BooleanInOut(
			"out_iso", 
			"isomorphic", 
			"true if the graphs are isomorphic"
			);
	
	public BasicIsomorphism(String name, String description) {
		super(
				name, 
				description, 
				ExistingAlgoCategories.COMPARISON_GRAPHS, 
				"/icons/igraph"+IMAGE_PATH_PLACEHOLDER_SIZE+".png",
				"/icons/igraphBig.png"
				);

		inputs.add(INPUT_GRAPH1);
		inputs.add(INPUT_GRAPH2);
		outputs.add(OUTPUT_ISOMORPHIC);
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
