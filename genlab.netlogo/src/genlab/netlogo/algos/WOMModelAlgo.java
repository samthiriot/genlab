package genlab.netlogo.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import genlab.core.model.meta.basics.flowtypes.IntegerInOut;
import genlab.core.model.meta.basics.flowtypes.ProbabilityInOut;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.parameters.IntParameter;
import genlab.netlogo.exec.WOMModelExec;

public class WOMModelAlgo extends BasicAlgo {

	public static final InputOutput<IGenlabGraph> INPUT_GRAPH = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"in_graph", 
			"graph", 
			"the graph to simulate over"
	);

	public static final DoubleInOut INPUT_PROPORTION_KNOWLEDGEABLE = new DoubleInOut(
			"in_prop_K", 
			"prop K", 
			"proportion of knowledgeablae agents",
			0.1,
			0.0,
			1.0
			);


	public static final DoubleInOut INPUT_PROPORTION_SEEKERS = new DoubleInOut(
			"in_prop_seekers", 
			"prop seekers", 
			"proportion of agents seeking info",
			0.1,
			0.0,
			1.0
			);

	public static final DoubleInOut INPUT_PROPORTION_PROMOTERS = new DoubleInOut(
			"in_prop_promoters", 
			"prop promoters", 
			"proportion of agents diffusing info",
			0.1,
			0.0,
			1.0
			);
	

	public static final IntegerInOut INPUT_DURATION_SEEKERS = new IntegerInOut(
			"in_duration_seekers", 
			"duration seekers", 
			"count of timesteps where seekers search information",
			5,
			0
			);
	

	public static final IntegerInOut INPUT_DURATION_PROMOTERS = new IntegerInOut(
			"in_duration_promoters", 
			"duration promoters", 
			"count of timesteps where promoters broadcast info",
			5,
			0
			);
	

	public static final DoubleInOut OUTPUT_A = new DoubleInOut(
			"out_aware", 
			"awareness", 
			"proportion of aware people at the end of the simulation"
			);
	
	public static final DoubleInOut OUTPUT_AK = new DoubleInOut(
			"out_ak", 
			"ak", 
			"proportion of AK people at the end of the simulation"
			);
	
	public static final IntegerInOut OUTPUT_DURATION = new IntegerInOut(
			"out_duration", 
			"duration", 
			"duration of simulation in steps"
			);
	
	

	public static final IntParameter PARAM_MAX_STEPS = new IntParameter(
			"param_maxiteration", 
			"max steps", 
			"maximum duration of the simulation in steps", 
			1000,
			1
			);
	
	
	public WOMModelAlgo() {
		super(
				"wom Netlogo", 
				"search and diffusion of info", 
				ExistingAlgoCategories.MODELS, 
				null,
				null);

		inputs.add(INPUT_GRAPH);
		
		inputs.add(INPUT_PROPORTION_KNOWLEDGEABLE);
		
		inputs.add(INPUT_PROPORTION_SEEKERS);
		inputs.add(INPUT_PROPORTION_PROMOTERS);
		
		inputs.add(INPUT_DURATION_SEEKERS);
		inputs.add(INPUT_DURATION_PROMOTERS);
		
		outputs.add(OUTPUT_A);
		outputs.add(OUTPUT_AK);
		outputs.add(OUTPUT_DURATION);
		
		registerParameter(PARAM_MAX_STEPS);
		
	}

	@Override
	public IAlgoExecution createExec(IExecution execution, AlgoInstance algoInstance) {
		return new WOMModelExec(execution, algoInstance);
	}

}
