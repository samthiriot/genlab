package genlab.graphstream.algos.generators;

import genlab.basics.flow.DoubleFlowType;
import genlab.basics.flow.IntegerFlowType;
import genlab.basics.flow.SimpleGraphFlowType;
import genlab.basics.javaTypes.graphs.IGenlabGraph;
import genlab.core.algos.AbstractAlgoExecution;
import genlab.core.algos.AlgoInstance;
import genlab.core.algos.ComputationProgressWithSteps;
import genlab.core.algos.IAlgo;
import genlab.core.algos.IAlgoExecution;
import genlab.core.algos.IAlgoInstance;
import genlab.core.algos.IComputationProgress;
import genlab.core.algos.IInputOutput;
import genlab.core.algos.InputOutput;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WattsStrogatzAlgo implements IAlgo {

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
	
	private final Set<IInputOutput> inputs = new HashSet<IInputOutput>() {{
		add(PARAM_N);
		add(PARAM_K);
		add(PARAM_P);
	}};
	
	public static final InputOutput<IGenlabGraph> OUTPUT_GRAPH =  new InputOutput<IGenlabGraph>(
			new SimpleGraphFlowType(), 
			ALGO_NAME+".graph", 
			"g", 
			"resulting graph"
		);
	
	private Set<IInputOutput> outputs = new HashSet<IInputOutput>() {{
		
		add(OUTPUT_GRAPH);
		
	}};
	
	public WattsStrogatzAlgo() {
		
	}
	

	@Override
	public String getName() {
		return ALGO_NAME;
	}

	@Override
	public String getDescription() {
		return "as implemented into the graphstream library";
	}
	
	@Override
	public Set<IInputOutput> getInputs() {
		return inputs;
	}

	@Override
	public Set<IInputOutput> getOuputs() {
		return outputs;
	}

	@Override
	public IAlgoInstance createInstance() {
		return new AlgoInstance(this);
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
