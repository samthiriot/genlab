package genlab.gephi.algos.measure;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

public abstract class GephiAbstractAlgo extends BasicAlgo {

	public static final InputOutput<IGenlabGraph> INPUT_GRAPH = new InputOutput<IGenlabGraph>(
			new SimpleGraphFlowType(), 
			"TODO.graph", 
			"graph", 
			"the graph to analyze"
	);
	
	public GephiAbstractAlgo(String name, String description) {
		super(
				name, 
				description, 
				ExistingAlgoCategories.ANALYSIS_GRAPH.getTotalId()
				);
		
		inputs.add(INPUT_GRAPH);
	}


}
