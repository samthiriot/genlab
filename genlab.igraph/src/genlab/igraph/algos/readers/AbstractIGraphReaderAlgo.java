package genlab.igraph.algos.readers;

import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.FileFlowType;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;
import genlab.igraph.algos.AbstractIGraphAlgo;
import genlab.igraph.parameters.ChoiceOfImplementationParameter.EIgraphImplementation;

import java.io.File;

public abstract class AbstractIGraphReaderAlgo extends AbstractIGraphAlgo {

	public static final InputOutput<File> INPUT_FILE = new InputOutput<File>(
			FileFlowType.SINGLETON,
			"in_filename", 
			"file", 
			"the file to read"
	);
	
	public static final InputOutput<IGenlabGraph> OUTPUT_GRAPH = new InputOutput<IGenlabGraph>(
			SimpleGraphFlowType.SINGLETON, 
			"out_graph", 
			"graph", 
			"the graph readen from file"
	);
	
		
	public AbstractIGraphReaderAlgo(
			String name, 
			String description
			) {
		super(
				name, 
				description, 
				ExistingAlgoCategories.READER_GRAPH,
				EIgraphImplementation.R_ONLY
				);
		
		inputs.add(INPUT_FILE);
		outputs.add(OUTPUT_GRAPH);
		
	}
	
	
	
}
