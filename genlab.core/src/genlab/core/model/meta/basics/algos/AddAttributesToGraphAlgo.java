package genlab.core.model.meta.basics.algos;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.AnythingFlowType;
import genlab.core.model.meta.basics.flowtypes.GraphInOut;

public class AddAttributesToGraphAlgo extends BasicAlgo {

	public static final GraphInOut INPUT_GRAPH = new GraphInOut(
			"in_graph", 
			"graph", 
			"graph to manipulate"
			);
	

	public static final InputOutput<Object> INPUT_ANYTHING = new InputOutput<Object>(
			AnythingFlowType.SINGLETON, 
			"in_anything", 
			"attributes", 
			"any input that can be stored into a graph attribute",
			true
			);
	
	public static final GraphInOut OUTPUT_GRAPH = new GraphInOut(
			"out_graph", 
			"graph", 
			"graph with attributes"
			);
	
	public AddAttributesToGraphAlgo() {
		super(
				"add as graph attributes", 
				"adds any data in input as a graph attribute", 
				ExistingAlgoCategories.ANALYSIS_GRAPH, 
				null, 
				null
				);
		inputs.add(INPUT_GRAPH);
		inputs.add(INPUT_ANYTHING);
		outputs.add(OUTPUT_GRAPH);
	}

	@Override
	public IAlgoExecution createExec(IExecution execution, AlgoInstance algoInstance) {
		return new AddAttributesToGraphExec(execution, algoInstance);
	}

}
