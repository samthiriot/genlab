package genlab.igraph.algos.measure;

import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.algos.AbstractIGraphAlgo;
import genlab.igraph.parameters.ChoiceOfImplementationParameter.EIgraphImplementation;

public abstract class AbstractIGraphMeasure extends AbstractIGraphAlgo {

	public static final InputOutput<IGenlabGraph> INPUT_GRAPH = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"in_graph", 
			"graph", 
			"the graph to analyze"
	);
		
	public AbstractIGraphMeasure(
			String name, 
			String description,
			EIgraphImplementation implementationAcceptedOnly
			) {
		super(
				name, 
				description, 
				ExistingAlgoCategories.ANALYSIS_GRAPH,
				implementationAcceptedOnly
				);
		
		inputs.add(INPUT_GRAPH);
		
	}
	
	public AbstractIGraphMeasure(
			String name, 
			String description,
			AlgoCategory category,
			EIgraphImplementation implementationAcceptedOnly
			) {
		super(
				name, 
				description, 
				category,
				implementationAcceptedOnly
				);
				
		inputs.add(INPUT_GRAPH);
	}
	
	
}
