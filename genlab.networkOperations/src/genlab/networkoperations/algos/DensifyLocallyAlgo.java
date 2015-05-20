package genlab.networkoperations.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.parameters.RNGSeedParameter;
import genlab.networkoperations.exec.AddChainsExec;
import genlab.networkoperations.exec.DensityLocallyExec;

// TODO doc !
public class DensifyLocallyAlgo extends BasicAlgo {

	public static final InputOutput<IGenlabGraph> INPUT_GRAPH = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"in_graph", 
			"graph", 
			"the graph to modify"
	);
	

	
	public static final IntegerInOut INPUT_PLACES = new IntegerInOut(
			"in_places", 
			"places", 
			"how many places to word on",
			1
			);
	
	public static final IntegerInOut INPUT_MAX_LINKS = new IntegerInOut(
			"in_maxlinks", 
			"max edges", 
			"maximum number of edges to create per place",
			40
			);
	
	public static final IntegerInOut INPUT_HORIZON = new IntegerInOut(
			"in_horizon", 
			"horizon", 
			"how far to look at neighboors",
			2
			);
	
	public static final DoubleInOut INPUT_PCLOSURE = new DoubleInOut(
			"in_closure", 
			"closure", 
			"the probability to close empty triads",
			0.8
			);
	
	

	public static final RNGSeedParameter PARAM_SEED = new RNGSeedParameter(
			"param_seed", 
			"seed", 
			"the seed used to initialize the random generator"
			);
	
	
	public static final InputOutput<IGenlabGraph> OUTPUT_GRAPH = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"out_graph", 
			"graph", 
			"the graph modified"
	);
	
	
	public DensifyLocallyAlgo() {
		super(
				"Densify locally", 
				"selects some nodes randomly, then for a given horizon, closes the triads with the parameter probability", 
				ExistingAlgoCategories.NOISE_GRAPH, 
				null, 
				null
				);

		inputs.add(INPUT_GRAPH);
		inputs.add(INPUT_PLACES);
		inputs.add(INPUT_HORIZON);
		inputs.add(INPUT_MAX_LINKS);
		inputs.add(INPUT_PCLOSURE);
		
		outputs.add(OUTPUT_GRAPH);
		
		registerParameter(PARAM_SEED);
		
	}

	@Override
	public IAlgoExecution createExec(IExecution execution, AlgoInstance algoInstance) {
		return new DensityLocallyExec(execution, algoInstance);
	}

}
