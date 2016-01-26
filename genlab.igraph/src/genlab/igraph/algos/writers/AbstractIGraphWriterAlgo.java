package genlab.igraph.algos.writers;

import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.core.parameters.StringParameter;
import genlab.igraph.algos.AbstractIGraphAlgo;
import genlab.igraph.parameters.ChoiceOfImplementationParameter.EIgraphImplementation;

public abstract class AbstractIGraphWriterAlgo extends AbstractIGraphAlgo {

	public static final InputOutput<IGenlabGraph> INPUT_GRAPH = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"in_graph", 
			"graph", 
			"the graph to write"
	);
	
	public static final StringParameter PARAM_FILENAME_PREFIX = new StringParameter(
			"param_filename_prefix",
			"filename prefix",
			"the file will be written in the experiment output directory with <prefix><number>.<extension>",
			"graph_"
	);
	
	public final StringParameter PARAM_FILENAME_EXTENSION = new StringParameter(
			"param_filename_extension",
			"filename extension",
			"the file will be written in the experiment output directory with <prefix><number>.<extension>",
			".net"
	);
		
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
		
		PARAM_FILENAME_EXTENSION.setDefaultValue(extension);
		
		registerParameter(PARAM_FILENAME_PREFIX);
		registerParameter(PARAM_FILENAME_EXTENSION);
		
		
	}
	
	
	
}
