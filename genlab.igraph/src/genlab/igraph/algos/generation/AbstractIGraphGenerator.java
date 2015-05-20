package genlab.igraph.algos.generation;

import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.basics.flowtypes.GraphInOut;
import genlab.core.parameters.RNGSeedParameter;
import genlab.igraph.Activator;
import genlab.igraph.commons.IgraphLibFactory;

import org.osgi.framework.Bundle;

public abstract class AbstractIGraphGenerator extends BasicAlgo {

	public static final GraphInOut OUTPUT_GRAPH = new GraphInOut( 
			"out_graph", 
			"graph", 
			"the graph generated"
	);
	

	public static final RNGSeedParameter PARAM_SEED = new RNGSeedParameter(
			"param_seed", 
			"seed", 
			"the seed used to initialize the random generator"
			);
	
	public AbstractIGraphGenerator(
			String name, 
			String description,
			boolean declareParamSeed
			) {
		this(name, description, ExistingAlgoCategories.GENERATORS_GRAPHS, declareParamSeed);
	}
	

	
	public AbstractIGraphGenerator(
			String name, 
			String description,
			AlgoCategory category,
			boolean declareParamSeed
			) {
		super(
				name, 
				description, 
				category,
				"/icons/igraph"+IMAGE_PATH_PLACEHOLDER_SIZE+".png",
				"/icons/igraphBig.png"
				);
		
		outputs.add(OUTPUT_GRAPH);
		
		if (declareParamSeed)
			registerParameter(PARAM_SEED);
	}

	@Override
	public Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}

	@Override
	public boolean isAvailable() {
		return IgraphLibFactory.isIGraphAvailable();
	}
	
}
