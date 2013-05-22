package genlab.graphstream.algos.generators;

import genlab.basics.flow.DoubleFlowType;
import genlab.basics.flow.IntegerFlowType;
import genlab.basics.flow.SimpleGraphFlowType;
import genlab.basics.javaTypes.graphs.IGenlabGraph;
import genlab.basics.workflow.IWorkflowListener;
import genlab.core.algos.AbstractAlgoExecution;
import genlab.core.algos.AlgoInstance;
import genlab.core.algos.BasicAlgo;
import genlab.core.algos.ComputationProgressWithSteps;
import genlab.core.algos.ExistingAlgoCategories;
import genlab.core.algos.IAlgo;
import genlab.core.algos.IAlgoExecution;
import genlab.core.algos.IAlgoInstance;
import genlab.core.algos.IComputationProgress;
import genlab.core.algos.IGenlabWorkflow;
import genlab.core.algos.IInputOutput;
import genlab.core.algos.InputOutput;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WattsStrogatzAlgo extends BasicAlgo {

	public static final String ALGO_NAME = "watts-strogatz beta generator";
			
	public static final InputOutput<Integer> PARAM_N = new InputOutput<Integer>(
			new IntegerFlowType(), 
			ALGO_NAME+".N", 
			"N", 
			"number of vertices"
	);
	
	public static final InputOutput<Integer> PARAM_K = new InputOutput<Integer>(
			new IntegerFlowType(), 
			ALGO_NAME+".K", 
			"K", 
			"neighboors"
	);
	public static final InputOutput<Double> PARAM_P =  new InputOutput<Double>(
			new DoubleFlowType(), 
			ALGO_NAME+".p", 
			"p", 
			"rewiring probability"
		);
	
	public static final InputOutput<IGenlabGraph> OUTPUT_GRAPH =  new InputOutput<IGenlabGraph>(
			new SimpleGraphFlowType(), 
			ALGO_NAME+".graph", 
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
	public IAlgoInstance createInstance(IGenlabWorkflow workflow) {
		return new AlgoInstance(this, workflow);
	}


	@Override
	public IAlgoExecution createExec(AlgoInstance algoInstance,
			Map<IInputOutput, Object> inputs) {
		return new WattsStrogatzExecution(
				algoInstance, 
				PARAM_N.decodeFromParameters(inputs), 
				PARAM_K.decodeFromParameters(inputs), 
				PARAM_P.decodeFromParameters(inputs)
				);
	}

	


}
