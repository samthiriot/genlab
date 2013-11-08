package genlab.igraph.algos.measure;

import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.Activator;

import org.osgi.framework.Bundle;

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
				null,
				ExistingAlgoCategories.ANALYSIS_GRAPH.getTotalId(),
				"/icons/igraph.gif"
				);
		
		inputs.add(INPUT_GRAPH);
	}
	
	public AbstractIGraphMeasure(
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
		
		inputs.add(INPUT_GRAPH);
	}

	@Override
	public Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}

}
