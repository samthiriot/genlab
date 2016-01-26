package genlab.igraph.algos.generation;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.basics.flowtypes.GraphInOut;
import genlab.core.parameters.RNGSeedParameter;
import genlab.igraph.algos.AbstractIGraphAlgo;

public abstract class AbstractIGraphGenerator extends AbstractIGraphAlgo {

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
				null
				);
		
		outputs.add(OUTPUT_GRAPH);
		
		if (declareParamSeed)
			registerParameter(PARAM_SEED);
		
	}



	@Override
	public IAlgoInstance createInstance(IGenlabWorkflowInstance workflow) {
		return new AbstractIGraphGeneratorInstance(this, workflow);
	}



	@Override
	public IAlgoInstance createInstance(String id,
			IGenlabWorkflowInstance workflow) {
		return new AbstractIGraphGeneratorInstance(this, workflow, id);
	}

	
	
	
}


