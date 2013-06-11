package genlab.graphstream.algos.readers;

import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.BasicAlgo;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.InputOutput;
import genlab.core.model.meta.basics.flowtypes.FileFlowType;
import genlab.core.model.meta.basics.flowtypes.SimpleGraphFlowType;
import genlab.core.model.meta.basics.graphs.IGenlabGraph;

import java.io.File;

import org.graphstream.stream.file.FileSource;

public abstract class AbstractGraphStreamGraphParser extends BasicAlgo {

	public static final InputOutput<File> PARAM_FILE = new InputOutput<File>(
			new FileFlowType(), 
			"TODO.file", 
			"file", 
			"the file to read the graph from"
	);
	
	public static final InputOutput<IGenlabGraph> OUTPUT_GRAPH = new InputOutput<IGenlabGraph>(
			new SimpleGraphFlowType(), 
			"TODO.graph", 
			"graph", 
			"the graph loaded from the file"
	);
	
	public AbstractGraphStreamGraphParser(String name, String desc) {
		super(name, desc, ExistingAlgoCategories.PARSER_GRAPH.getTotalId());
		inputs.add(PARAM_FILE);
		outputs.add(OUTPUT_GRAPH);
	}
	
	protected abstract FileSource getGraphStreamFileSource();

	@Override
	public IAlgoExecution createExec(IExecution exec, AlgoInstance algoInstance) {
		
		return new AbstractGraphstreamGraphParserExecution(
				exec,
				algoInstance, 
				getGraphStreamFileSource() 
				);
	}
	

}
