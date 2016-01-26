package genlab.igraph.algos.writers;

import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.algos.AbstractIGraphAlgo;
import genlab.igraph.parameters.ChoiceOfImplementationParameter.EIgraphImplementation;

public abstract class AbstractIGraphWriterAlgo extends AbstractIGraphAlgo {

	public static final InputOutput<IGenlabGraph> INPUT_GRAPH = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"in_graph", 
			"graph", 
			"the graph to write"
	);
	
	protected final String extension;
		
	public AbstractIGraphWriterAlgo(
			String name, 
			String description,
			EIgraphImplementation implementationAcceptedOnly,
			String extension
			) {
		super(
				name, 
				description, 
				ExistingAlgoCategories.WRITER_GRAPH,
				implementationAcceptedOnly
				);
		
		inputs.add(INPUT_GRAPH);
		
		this.extension = extension;
		
	}
	
	
	
}
