package genlab.graphstream.algos.generators;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.IGenlabWorkflow;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.DoubleFlowType;
import genlab.core.model.meta.basics.flowtypes.IntegerFlowType;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public class WattsStrogatzAlgo extends BasicAlgo {

	public static final String ALGO_NAME = "watts-strogatz beta generator";
			
	public static final InputOutput<Integer> PARAM_N = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON,
			"N", 
			"N", 
			"number of vertices"
	);
	
	public static final InputOutput<Integer> PARAM_K = new InputOutput<Integer>(
			IntegerFlowType.SINGLETON,
			"K", 
			"K", 
			"neighboors (should be even)"
	);
	public static final InputOutput<Double> PARAM_P =  new InputOutput<Double>(
			DoubleFlowType.SINGLETON,
			"p", 
			"p", 
			"rewiring probability"
		);
	
	public static final InputOutput<IGenlabGraph> OUTPUT_GRAPH =  new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"graph", 
			"g", 
			"resulting graph"
		);
	
	
	private IGenlabWorkflow workflow = null;
	
	public WattsStrogatzAlgo() {
		super(
				ALGO_NAME,
				"as implemented into the graphstream library",
				ExistingAlgoCategories.GENERATORS_GRAPHS.getTotalId()
				);
		
		inputs.add(PARAM_N);
		inputs.add(PARAM_K);
		inputs.add(PARAM_P);
		outputs.add(OUTPUT_GRAPH);
	}
		

	@Override
	public IAlgoExecution createExec(IExecution exec, AlgoInstance algoInstance) {
		return new WattsStrogatzExecution(
				exec,
				algoInstance
				);
	}

	


}
