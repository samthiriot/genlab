package genlab.networkoperations.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.parameters.RNGSeedParameter;
import genlab.networkoperations.exec.AddChainsExec;

// TODO doc !
public class AddChainsAlgo extends BasicAlgo {

	public static final InputOutput<IGenlabGraph> INPUT_GRAPH = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"in_graph", 
			"graph", 
			"the graph to modify"
	);
	

	public static final IntegerInOut INPUT_LENGTH = new IntegerInOut(
			"in_length", 
			"length", 
			"the length of chains to create",
			50
			);
	
	public static final IntegerInOut INPUT_COUNT = new IntegerInOut(
			"in_count", 
			"count", 
			"how many chains to create",
			1
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
	
	
	public AddChainsAlgo() {
		super(
				"Add chains", 
				"add to a graph some chains, that is long wires that will increase length and diameter", 
				ExistingAlgoCategories.NOISE_GRAPH, 
				null, 
				null
				);

		inputs.add(INPUT_GRAPH);
		inputs.add(INPUT_LENGTH);
		inputs.add(INPUT_COUNT);

		outputs.add(OUTPUT_GRAPH);
		
		registerParameter(PARAM_SEED);
		
	}

	@Override
	public IAlgoExecution createExec(IExecution execution, AlgoInstance algoInstance) {
		return new AddChainsExec(execution, algoInstance);
	}

}
